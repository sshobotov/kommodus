package com.github.kommodus.descriptors

import com.github.kommodus.Validation

sealed class RepeatableDescriptor<E> {
    fun satisfies(constraint: Validation.Constraint<E>): Terminal<E> =
        satisfies(constraint as Validation.Validator<E>)

    internal abstract fun satisfies(constraint: Validation.Validator<E>): Terminal<E>

    class Opened<E> internal constructor(): RepeatableDescriptor<E>() {
        override fun satisfies(constraint: Validation.Validator<E>): Terminal<E> =
            Terminal(listOf(constraint))
    }

    class Terminal<E> internal constructor(
        internal val constraints: List<Validation.Validator<E>>
    ): RepeatableDescriptor<E>() {
        override fun satisfies(constraint: Validation.Validator<E>): Terminal<E> =
            Terminal(constraints + constraint)
    }
}