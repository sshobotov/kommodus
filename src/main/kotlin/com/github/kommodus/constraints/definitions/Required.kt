package com.github.kommodus.constraints.definitions

import com.github.kommodus.Validation

class Required<T>: Validation.Constraint<T?> {
    override fun check(value: T?): Boolean = value != null

    override fun message(): String = "Value is required"
}