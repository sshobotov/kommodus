package com.github.kommodus.constraints.definitions

import com.github.kommodus.Validation

class Matches(private val pattern: Regex, val description: String): Validation.Constraint<String> {
    override fun check(value: String): Boolean =
        pattern.matches(value)

    override fun message(): String = "Value doesn't look like $description"
}