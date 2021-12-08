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
 *   - extensibility
 *   - tests
 */
interface Validation<T> {
    fun applyTo(value: T): Result<T>

    sealed class Result<T> {
        abstract fun isValid(): Boolean

        fun isInvalid(): Boolean = !isValid()

        data class Valid<T>(val value: T): Result<T>() {
            override fun isValid(): Boolean = true
        }

        data class Invalid<T>(val errors: ValidationErrors): Result<T>() {
            override fun isValid(): Boolean = false

            override fun toString(): String = asStringsMap().toString()

            private fun asStringsMap(): Map<String, List<String>> =
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

        sealed interface Segment

        @JvmInline
        value class Property(val value: KProperty<*>): Segment

        @JvmInline
        value class Index(val value: Int): Segment
    }

    data class InvalidCause<T: Constraint<T>>(val constraint: KClass<T>, val message: String)

    sealed class Validator<in T> {
        internal abstract fun validate(value: T): ValidationErrors
    }

    abstract class Constraint<in T>: Validator<T>() {
        abstract fun message(): String

        abstract fun check(value: T): Boolean

        override fun validate(value: T): ValidationErrors =
            if (check(value)) mapOf()
            else mapOf(InvalidPath.empty() to listOf(InvalidCause(this::class, message())))
    }

    companion object {
        fun <T, A> whereProperty(property: KProperty1<T, A>): FieldDescriptor.Opened<T, A> =
            ValidationDescriptor<T>().whereProperty(property)

        fun <T> whereInstanceOf(): ClassDescriptor.Opened<T> =
            ValidationDescriptor<T>().whereInstance()
    }
}

/**
 * Wrapper constraint to provide single method definitions for the same nullable and non-nullable type
 * e.g. String and String?
 */
internal data class NullableValueValidator<T>(
    val inner: Validation.Validator<T>
): Validation.Validator<T?>() {
    override fun validate(value: T?): ValidationErrors =
        if (value == null) mapOf() else inner.validate(value)
}

internal data class InstanceFieldsValidator<T>(
    private val nested: Validation<T>
): Validation.Validator<T>() {
    override fun validate(value: T): ValidationErrors =
        when (val result = nested.applyTo(value)) {
            is Validation.Result.Valid -> mapOf()
            is Validation.Result.Invalid -> result.errors
        }
}

internal data class CollectionElementsValidator<E, T: Collection<E>>(
    private val nested: List<Validation.Validator<E>>
): Validation.Validator<T>() {
    override fun validate(value: T): ValidationErrors =
        value.mapIndexedNotNull { i, element ->
            nested
                .flatMap { it.validate(element).toList() }
                // group causes by path
                .groupBy({ it.first }, { it.second })
                .map {
                    it.key.prepend(Validation.InvalidPath.Index(i)) to it.value.flatten()
                }
        }.fold(mapOf()) { acc, indexResult -> acc + indexResult }
}
