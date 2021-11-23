package com.github.kommodus.constraints.definitions

import com.github.kommodus.Validation

object NotBlank: Validation.Constraint<String> {
    override fun message(): String = ""

    override fun check(value: String): Boolean = value.trim().isNotBlank()
}