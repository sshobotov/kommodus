package com.github.kommodus.constraints.definitions

import com.github.kommodus.Validation

class NotEmpty<T: Collection<*>>: Validation.Constraint<T> {
    override fun check(value: T): Boolean = !value.isEmpty()

    override fun message(): String = "Should not be empty"
}