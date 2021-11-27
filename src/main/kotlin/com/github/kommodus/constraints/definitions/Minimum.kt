package com.github.kommodus.constraints.definitions

import com.github.kommodus.Validation

class Minimum<A: Comparable<A>>(val limit: A, val inclusive: Boolean): Validation.Constraint<A> {
    override fun message(): String =
        if (inclusive) "Value should be greater than or equal to $limit"
        else "Value should be greater than $limit"

    override fun check(value: A): Boolean =
        if (inclusive) value >= limit else value > limit
}