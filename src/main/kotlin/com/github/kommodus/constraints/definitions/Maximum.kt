package com.github.kommodus.constraints.definitions

import com.github.kommodus.Validation

class Maximum<A: Comparable<A>>(private val limit: A): Validation.Constraint<A> {
    override fun message(): String = ""

    override fun check(value: A): Boolean = value < limit
}