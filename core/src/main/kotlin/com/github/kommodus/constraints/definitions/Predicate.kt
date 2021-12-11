package com.github.kommodus.constraints.definitions

import com.github.kommodus.Validation

/**
 * General purpose constraint
 */
class Predicate<T>(
    private val predicate: (T) -> Boolean,
    private val ifFailsMessage: String
) : Validation.Constraint<T>(), StdConstraint {
    override fun check(value: T): Boolean = predicate(value)

    override fun message(): String = ifFailsMessage
}