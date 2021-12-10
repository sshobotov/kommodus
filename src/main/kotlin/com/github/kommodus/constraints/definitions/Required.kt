package com.github.kommodus.constraints.definitions

import com.github.kommodus.Validation

class Required<T>: Validation.Constraint<T?>(), StdConstraint {
    override fun message(): String = "Value is required"

    override fun check(value: T?): Boolean = value != null
}