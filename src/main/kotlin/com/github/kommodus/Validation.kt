package com.github.kommodus

import kotlin.reflect.KProperty
import kotlin.reflect.KProperty1

/**
 * TODO:
 *   - validate nested
 *   - validate collection
 *   - validate dependent fields
 *   - extensibility
 *   - composable
 *   - java beans interoperability
 */
interface Validation<T> {
    fun invoke(value: T): Result<T>

    interface Constraint<in T> {
        fun message(): String

        fun check(value: T): Boolean
    }

    sealed class Result<T> {
        abstract fun isValid(): Boolean

        fun isInvalid(): Boolean = !isValid()

        data class Valid<T>(val value: T): Result<T>() {
            override fun isValid(): Boolean = true
        }

        data class Invalid<T>(val errors: Map<KProperty<*>?, List<Constraint<*>>>): Result<T>() {
            override fun isValid(): Boolean = false
        }
    }

    companion object {
        fun <T, A> where(property: KProperty1<T, A>): FieldDescriptor.Opened<T, A> =
            ValidationDescriptor<T>().where(property)

        fun <T> where(): ClassDescriptor.Opened<T> =
            ValidationDescriptor<T>().where()
    }
}

class ValidationDescriptor<T> internal constructor(
    private val constraints: Map<KProperty1<T, *>?, ConstraintsRun<T>> = mapOf()
): Validation<T> {
    fun <A> where(property: KProperty1<T, A>): FieldDescriptor.Opened<T, A> =
        FieldDescriptor.Opened(property, this)

    fun where(): ClassDescriptor.Opened<T> =
        ClassDescriptor.Opened(this)

    override fun invoke(value: T): Validation.Result<T> =
        constraints.mapNotNull { entry ->
            entry.value.run(value)?.let { entry.key to it }
        }.let { propertiesViolations ->
            if (propertiesViolations.isNotEmpty())
                Validation.Result.Invalid(propertiesViolations.toMap())
            else
                Validation.Result.Valid(value)
        }

    internal class ConstraintsRun<T>(
        val run: (T) -> List<Validation.Constraint<*>>?
    ) {
        companion object {
            operator fun <T, A> invoke(
                property: KProperty1<T, A>,
                constraints: List<Validation.Constraint<A>>
            ): ConstraintsRun<T> =
                ConstraintsRun { instance ->
                    collectFailedConstraints(constraints, property.get(instance))
                }

            operator fun <T> invoke(
                constraints: List<Validation.Constraint<T>>
            ): ConstraintsRun<T> =
                ConstraintsRun { instance ->
                    collectFailedConstraints(constraints, instance)
                }

            private fun <A> collectFailedConstraints(constraints: List<Validation.Constraint<A>>, value: A) =
                constraints
                    .mapNotNull { constraint ->
                        if (constraint.check(value)) null else when (constraint) {
                            is NullableConstraint<*> -> constraint.inner
                            else -> constraint
                        }
                    }
                    .takeIf { it.isNotEmpty() }
        }
    }

    internal fun <A> put(
        property: KProperty1<T, A>,
        constraints: List<Validation.Constraint<A>>
    ): ValidationDescriptor<T> =
        ValidationDescriptor(this.constraints + (property to ConstraintsRun(property, constraints)))

    internal fun put(
        constraints: List<Validation.Constraint<T>>
    ): ValidationDescriptor<T> =
        ValidationDescriptor(this.constraints + (null to ConstraintsRun(constraints)))
}

sealed class ClassDescriptor<T> {
    abstract fun demands(constraint: Validation.Constraint<T>): Terminal<T>

    class Opened<T> internal constructor(
        private val container: ValidationDescriptor<T>
    ): ClassDescriptor<T>() {
        override fun demands(constraint: Validation.Constraint<T>): Terminal<T> =
            Terminal(container, listOf(constraint))
    }

    class Terminal<T> internal constructor(
        private val container: ValidationDescriptor<T>,
        private val constraints: List<Validation.Constraint<T>>
    ): ClassDescriptor<T>(), Validation<T> {
        override fun demands(constraint: Validation.Constraint<T>): Terminal<T> =
            Terminal(container, constraints + constraint)

        fun <B> where(property: KProperty1<T, B>): FieldDescriptor.Opened<T, B> =
            container.put(constraints).where(property)

        override fun invoke(value: T): Validation.Result<T> =
            container.put(constraints).invoke(value)
    }
}

sealed class RepeatableDescriptor<E> {
    abstract fun demands(constraint: Validation.Constraint<E>): Terminal<E>

    class Opened<E> internal constructor(): RepeatableDescriptor<E>() {
        override fun demands(constraint: Validation.Constraint<E>): Terminal<E> =
            Terminal(listOf(constraint))
    }

    class Terminal<E> internal constructor(
        internal val constraints: List<Validation.Constraint<E>>
    ): RepeatableDescriptor<E>() {
        override fun demands(constraint: Validation.Constraint<E>): Terminal<E> =
            Terminal(constraints + constraint)
    }
}

sealed class FieldDescriptor<T, A> {
    abstract fun demands(constraint: Validation.Constraint<A>): Terminal<T, A>

    class Opened<T, A> internal constructor(
        private val property: KProperty1<T, A>,
        private val container: ValidationDescriptor<T>
    ): FieldDescriptor<T, A>() {
        override fun demands(constraint: Validation.Constraint<A>): Terminal<T, A> =
            Terminal(property, container, listOf(constraint))
    }

    class Terminal<T, A> internal constructor(
        private val property: KProperty1<T, A>,
        private val container: ValidationDescriptor<T>,
        private val constraints: List<Validation.Constraint<A>>
    ): FieldDescriptor<T, A>(), Validation<T> {
        override fun demands(constraint: Validation.Constraint<A>): Terminal<T, A> =
            Terminal(property, container, constraints + constraint)

        fun <B> where(property: KProperty1<T, B>): Opened<T, B> =
            container.put(this.property, constraints).where(property)

        override fun invoke(value: T): Validation.Result<T> =
            container.put(this.property, constraints).invoke(value)
    }
}

/**
 * Wrapper constraint to provide single method definitions for the same nullable and non-nullable type
 * e.g. String and String?
 */
internal data class NullableConstraint<T>(
    val inner: Validation.Constraint<T>
): Validation.Constraint<T?> {
    override fun message(): String = inner.message()

    override fun check(value: T?): Boolean =
        if (value == null) true else inner.check(value)
}

internal data class ValidPropertiesConstraint<T>(
    private val nested: Validation<T>
): Validation.Constraint<T> {
    override fun message(): String = TODO()

    override fun check(value: T): Boolean =
        nested.invoke(value).isValid()
}

internal data class ValidElementsConstraint<E, T: Collection<E>>(
    private val nested: List<Validation.Constraint<E>>
): Validation.Constraint<T> {
    override fun message(): String = TODO()

    override fun check(value: T): Boolean =
        value.mapIndexedNotNull { i, element ->
            nested.filter { it.check(element) }
                .takeIf { it.isNotEmpty() }
                ?.let { i to it }
        }.isNotEmpty()
}
