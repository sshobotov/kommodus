package com.github.kommodus.descriptors

import com.github.kommodus.Validation
import kotlin.reflect.KProperty1

class ValidationDescriptor<T> internal constructor(
    private val constraints: Map<KProperty1<T, *>?, ConstraintsRun<T>> = mapOf()
): Validation<T> {
    fun <A> whereProperty(property: KProperty1<T, A>): FieldDescriptor.Opened<T, A> =
        FieldDescriptor.Opened(property, this)

    fun whereInstance(): ClassDescriptor.Opened<T> =
        ClassDescriptor.Opened(this)

    override fun applyTo(value: T): Validation.Result<T> =
        constraints.values.fold(mapOf<Validation.InvalidPath, List<Validation.InvalidCause<*>>>()) { acc, entry ->
            acc + entry.run(value)
        }.let { propertiesViolations ->
            if (propertiesViolations.isNotEmpty())
                Validation.Result.Invalid(propertiesViolations)
            else
                Validation.Result.Valid(value)
        }

    internal class ConstraintsRun<T>(
        val run: (T) -> Map<Validation.InvalidPath, List<Validation.InvalidCause<*>>>
    ) {
        companion object {
            operator fun <T, A> invoke(
                property: KProperty1<T, A>,
                constraints: List<Validation.Validator<A>>
            ): ConstraintsRun<T> =
                ConstraintsRun { validatedValue ->
                    collectFailures(constraints, property.get(validatedValue))
                        .mapKeys { entry -> entry.key.prepend(Validation.InvalidPath.Property(property)) }
                }

            operator fun <T> invoke(
                constraints: List<Validation.Validator<T>>
            ): ConstraintsRun<T> =
                ConstraintsRun { validatedValue ->
                    collectFailures(constraints, validatedValue)
                }

            private fun <A> collectFailures(constraints: List<Validation.Validator<A>>, value: A) =
                constraints
                    .flatMap { it.validate(value).toList() }
                    .groupBy({ it.first }, { it.second })
                    .mapValues { it.value.flatten() }
        }
    }

    internal fun <A> put(
        property: KProperty1<T, A>,
        constraints: List<Validation.Validator<A>>
    ): ValidationDescriptor<T> =
        ValidationDescriptor(this.constraints + (property to ConstraintsRun(property, constraints)))

    internal fun put(
        constraints: List<Validation.Validator<T>>
    ): ValidationDescriptor<T> =
        ValidationDescriptor(this.constraints + (null to ConstraintsRun(constraints)))
}