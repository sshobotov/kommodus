package com.github.kommodus.descriptors

import com.github.kommodus.Validation
import kotlin.reflect.KProperty1

sealed class ClassDescriptor<T> {
    fun satisfies(constraint: Validation.Constraint<T>): Terminal<T> =
        satisfies(constraint as Validation.Validator<T>)

    internal abstract fun satisfies(constraint: Validation.Validator<T>): Terminal<T>

    class Opened<T> internal constructor(
        private val container: ValidationDescriptor<T>
    ): ClassDescriptor<T>() {
        override fun satisfies(constraint: Validation.Validator<T>): Terminal<T> =
            Terminal(container, listOf(constraint))
    }

    class Terminal<T> internal constructor(
        private val container: ValidationDescriptor<T>,
        private val constraints: List<Validation.Validator<T>>
    ): ClassDescriptor<T>(), Validation<T> {
        override fun satisfies(constraint: Validation.Validator<T>): Terminal<T> =
            Terminal(container, constraints + constraint)

        fun <B> andProperty(property: KProperty1<T, B>): FieldDescriptor.Opened<T, B> =
            container.put(constraints).whereProperty(property)

        override fun applyTo(value: T): Validation.Result<T> =
            container.put(constraints).applyTo(value)
    }
}