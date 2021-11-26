package com.github.kommodus.constraints.definitions

import com.github.kommodus.Validation

/**
 * General purpose constraint
 */
class Predicate<T>(private val predicate: (T) -> Boolean, private val message: String): Validation.Constraint<T> {
    override fun check(value: T): Boolean = predicate(value)

    override fun message(): String = message
}