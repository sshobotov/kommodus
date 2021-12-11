package com.github.kommodus.descriptors

import com.github.kommodus.Validation
import com.github.kommodus.Validator
import kotlin.reflect.KProperty1

sealed class FieldDescriptor<T, A> {
    fun satisfies(constraint: Validation.Constraint<A>): Terminal<T, A> =
        satisfies(constraint as Validator<A>)

    internal abstract fun satisfies(constraint: Validator<A>): Terminal<T, A>

    class Opened<T, A> internal constructor(
        private val property: KProperty1<T, A>,
        private val container: ValidationDescriptor<T>
    ) : FieldDescriptor<T, A>() {
        override fun satisfies(constraint: Validator<A>): Terminal<T, A> =
            Terminal(property, container, listOf(constraint))
    }

    class Terminal<T, A> internal constructor(
        private val property: KProperty1<T, A>,
        private val container: ValidationDescriptor<T>,
        private val constraints: List<Validator<A>>
    ) : FieldDescriptor<T, A>(), Validation<T> {
        override fun satisfies(constraint: Validator<A>): Terminal<T, A> =
            Terminal(property, container, constraints + constraint)

        fun <B> andProperty(property: KProperty1<T, B>): Opened<T, B> =
            container.put(this.property, constraints).whereProperty(property)

        override fun applyTo(value: T): Validation.Result<T> =
            container.put(this.property, constraints).applyTo(value)
    }
}