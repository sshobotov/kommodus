package com.github.kommodus.constraints.definitions

import com.github.kommodus.Validation

class NotEmpty<T: Collection<*>>: Validation.Constraint<T>() {
    override fun message(): String = "Vale should not be empty"

    override fun check(value: T): Boolean = !value.isEmpty()
}