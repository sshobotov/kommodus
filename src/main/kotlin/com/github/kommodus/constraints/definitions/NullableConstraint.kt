package com.github.kommodus.constraints.definitions

import com.github.kommodus.Validation

internal data class NullableConstraint<T>(
    val inner: Validation.Constraint<T>,
    private val ifNullResult: Boolean
): Validation.Constraint<T?> {
    override fun message(): String = inner.message()

    override fun check(value: T?): Boolean =
        if (value == null) ifNullResult else inner.check(value)
}