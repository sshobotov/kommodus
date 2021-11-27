package com.github.kommodus.constraints.definitions

import com.github.kommodus.Validation

object NotBlank: Validation.Constraint<CharSequence> {
    override fun message(): String = "Value cannot be blank"

    override fun check(value: CharSequence): Boolean = value.isNotBlank()
}