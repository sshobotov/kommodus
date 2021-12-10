package com.github.kommodus

import com.github.kommodus.descriptors.ClassDescriptor
import com.github.kommodus.descriptors.FieldDescriptor
import com.github.kommodus.descriptors.ValidationDescriptor
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.KProperty1

typealias ValidationErrors = Map<Validation.InvalidPath, List<Validation.InvalidCause<*>>>

/**
 * TODO:
 * - more tests (validators and their composition)
 * - whys and output in doc
 * - examples as a separate module
 */

/**
 * Gives an ability to validate object providing paths to properties that failed
 * validation with typed reason for that failure as a result
 */
interface Validation<T> {
    fun applyTo(value: T): Result<T>

    companion object {
        fun <T, A> whereProperty(property: KProperty1<T, A>): FieldDescriptor.Opened<T, A> =
            ValidationDescriptor<T>().whereProperty(property)

        fun <T> whereInstanceOf(): ClassDescriptor.Opened<T> =
            ValidationDescriptor<T>().whereInstance()
    }

    sealed class Result<T> {
        abstract fun isValid(): Boolean

        fun isInvalid(): Boolean = !isValid()

        data class Valid<T>(val value: T): Result<T>() {
            override fun isValid(): Boolean = true
        }

        data class Invalid<T>(val errors: ValidationErrors): Result<T>() {
            override fun isValid(): Boolean = false

            override fun toString(): String = asStringsMap().toString()

            fun asStringsMap(): Map<String, List<String>> =
                errors.map { entry ->
                    entry.key.plain() to entry.value.map { it.message }
                }.toMap()
        }
    }

    @JvmInline
    value class InvalidPath(val path: List<Segment>) {
        fun prepend(segment: Segment): InvalidPath {
            val updatable = path.toMutableList()
            updatable.add(0, segment)
            return InvalidPath(updatable)
        }

        fun plain(): String = path.joinToString(separator = ".") {
            when (it) {
                is Property -> it.value.name
                is Index -> it.value.toString()
            }
        }

        companion object {
            fun empty() = InvalidPath(listOf())
        }

        /**
         * Represents one of the breadcrumbs on the failed path
         */
        sealed interface Segment

        @JvmInline
        value class Property(val value: KProperty<*>): Segment

        @JvmInline
        value class Index(val value: Int): Segment
    }

    data class InvalidCause<T: Constraint<T>>(val constraint: KClass<T>, val message: String)

    /**
     * Base class to validate something specific about type T to be composed into Result,
     * marked as internal to be used only by library implementation
     */
    sealed class Validator<in T> {
        internal abstract fun validate(value: T): ValidationErrors
    }

    /**
     * Simplified validity check to expose a straightforward way for a user to define
     * a custom validation logic that might be reused (otherwise use .satisfies(<predicate>))
     */
    abstract class Constraint<in T>: Validator<T>() {
        abstract fun message(): String

        abstract fun check(value: T): Boolean

        override fun validate(value: T): ValidationErrors =
            if (check(value)) mapOf()
            else mapOf(InvalidPath.empty() to listOf(InvalidCause(this::class, message())))
    }
}

/**
 * Wrapper validator to provide a way of reusing one validator definition
 * for both nullable and non-nullable inputs (e.g. validate String and String?
 * values having only Constraint<String>), see .consideringNullableInput()
 */
internal data class NullableValueValidator<T>(
    private val nested: Validation.Validator<T>
): Validation.Validator<T?>() {
    override fun validate(value: T?): ValidationErrors =
        if (value == null) mapOf() else nested.validate(value)
}

/**
 * Validator to provide a way of validating nested object by composing Validation
 * definitions for them
 */
internal data class InstanceFieldsValidator<T>(
    private val nested: Validation<T>
): Validation.Validator<T>() {
    override fun validate(value: T): ValidationErrors =
        when (val result = nested.applyTo(value)) {
            is Validation.Result.Valid -> mapOf()
            is Validation.Result.Invalid -> result.errors
        }
}

/**
 * Wrapper validator to apply validators and get failures for a specific elements
 * of the collection (e.g. checking what numbers in collection are < 0)
 */
internal data class CollectionElementsValidator<E, T: Collection<E>>(
    private val nested: List<Validation.Validator<E>>
): Validation.Validator<T>() {
    override fun validate(value: T): ValidationErrors =
        value
            .mapIndexedNotNull { i, element -> nested.validateAll(element, Validation.InvalidPath.Index(i)) }
            .fold(mapOf()) { acc, indexResult -> acc + indexResult }
}
