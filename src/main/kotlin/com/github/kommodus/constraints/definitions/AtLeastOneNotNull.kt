package com.github.kommodus.constraints.definitions

import com.github.kommodus.Validation
import kotlin.reflect.KProperty1

class AtLeastOneNotNull<T> private constructor(private val set: Set<KProperty1<T, Any?>>): Validation.Constraint<T> {
    override fun check(value: T): Boolean {
        for (e in set) {
            if (e.get(value) != null) {
                return true
            }
        }
        return false
    }

    override fun message(): String = "At least one of ${set.joinToString(transform = { it.name })} should be set"

    companion object {
        operator fun <T> invoke(
            fst: KProperty1<T, Any?>,
            snd: KProperty1<T, Any?>,
            vararg rest: KProperty1<T, Any?>
        ): AtLeastOneNotNull<T> = AtLeastOneNotNull(setOf(fst) + snd + rest)
    }
}