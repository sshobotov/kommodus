package com.github.kommodus.descriptors

import com.github.kommodus.Validation
import com.github.kommodus.Validator

sealed class RepeatableDescriptor<E> {
    fun satisfies(constraint: Validation.Constraint<E>): Terminal<E> =
        satisfies(constraint as Validator<E>)

    internal abstract fun satisfies(constraint: Validator<E>): Terminal<E>

    class Opened<E> internal constructor() : RepeatableDescriptor<E>() {
        override fun satisfies(constraint: Validator<E>): Terminal<E> =
            Terminal(listOf(constraint))
    }

    class Terminal<E> internal constructor(
        internal val constraints: List<Validator<E>>
    ) : RepeatableDescriptor<E>() {
        override fun satisfies(constraint: Validator<E>): Terminal<E> =
            Terminal(constraints + constraint)
    }
}