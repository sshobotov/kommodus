package com.github.kommodus.constraints.definitions

import com.github.kommodus.Validation

class Matches(
    private val pattern: Regex,
    val description: String
) : Validation.Constraint<String>(), StdConstraint {
    override fun message(): String = "Value doesn't look like a $description"

    override fun check(value: String): Boolean =
        pattern.matches(value)
}