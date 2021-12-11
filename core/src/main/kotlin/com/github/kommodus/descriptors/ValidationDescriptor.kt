package com.github.kommodus.descriptors

import com.github.kommodus.Validation
import com.github.kommodus.Validator
import com.github.kommodus.ValidationErrors
import com.github.kommodus.validateAll
import kotlin.reflect.KProperty
import kotlin.reflect.KProperty1

/**
 * Entry point to start building Validation for a type T, validators aggregate and runner. Aggregates validators per
 * class property (or for a whole class to validate properties combination if property is null) to avoid multiple
 * conflicting definition like:
 * <pre>
 * Validation
 *   ...
 *   .andProperty(MyClass::one).greaterThan(10)
 *   ...
 *   .andProperty(MyClass::one).lessThan(5)
 * </pre>
 */
class ValidationDescriptor<T> private constructor(
    private val validators: Map<KProperty1<T, *>?, ValidatorsRun<T>>
) : Validation<T> {
    constructor() : this(mapOf())

    fun <A> whereProperty(property: KProperty1<T, A>): FieldDescriptor.Opened<T, A> =
        FieldDescriptor.Opened(property, this)

    fun whereInstance(): ClassDescriptor.Opened<T> =
        ClassDescriptor.Opened(this)

    override fun applyTo(value: T): Validation.Result<T> {
        val errors: ValidationErrors = validators.values.fold(mapOf()) { acc, entry ->
            acc + entry.run(value)
        }
        return if (errors.isEmpty()) Validation.Result.Valid(value) else Validation.Result.Invalid(errors)
    }

    internal fun <A> put(
        property: KProperty1<T, A>,
        constraints: List<Validator<A>>
    ): ValidationDescriptor<T> =
        ValidationDescriptor(this.validators + (property to ValidatorsRun(property, constraints)))

    internal fun put(
        constraints: List<Validator<T>>
    ): ValidationDescriptor<T> =
        ValidationDescriptor(this.validators + (null to ValidatorsRun(constraints)))

    /**
     * Wraps the actual object validation execution to preserve types alignment for property and validator while keep
     * validators aggregate generic enough
     */
    private class ValidatorsRun<T>(val run: (T) -> ValidationErrors) {
        companion object {
            operator fun <T, A> invoke(
                property: KProperty1<T, A>,
                validators: List<Validator<A>>
            ): ValidatorsRun<T> =
                ValidatorsRun { validatedObject ->
                    collectErrors(validators, property.get(validatedObject), property)
                }

            operator fun <T> invoke(
                validators: List<Validator<T>>
            ): ValidatorsRun<T> =
                ValidatorsRun { validatedObject ->
                    collectErrors(validators, validatedObject, property = null)
                }

            private fun <T> collectErrors(
                validators: List<Validator<T>>,
                value: T,
                property: KProperty<T>?
            ) =
                validators.validateAll(value, property?.let(Validation.InvalidPath::Property))
        }
    }
}