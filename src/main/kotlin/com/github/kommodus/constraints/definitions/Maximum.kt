package com.github.kommodus.constraints.definitions

import com.github.kommodus.Validation

class Maximum<A: Comparable<A>>(
    val limit: A,
    val inclusive: Boolean
): Validation.Constraint<A>(), StdConstraint {
    override fun message(): String =
        if (inclusive) "Value should be lesser than or equal to $limit"
        else "Value should be lesser than $limit"

    override fun check(value: A): Boolean =
        if (inclusive) value <= limit else value < limit
}