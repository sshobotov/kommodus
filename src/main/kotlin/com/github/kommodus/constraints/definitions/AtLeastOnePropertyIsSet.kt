package com.github.kommodus.constraints.definitions

import com.github.kommodus.Validation
import kotlin.reflect.KProperty1

class AtLeastOnePropertyIsSet<T> private constructor(
    val of: Set<KProperty1<T, Any?>>
): Validation.Constraint<T>() {
    override fun message(): String = "At least one of ${of.joinToString { it.name }} should be provided"

    override fun check(value: T): Boolean {
        for (e in of) {
            if (e.get(value) != null) {
                return true
            }
        }
        return false
    }

    companion object {
        operator fun <T> invoke(
            fst: KProperty1<T, Any?>,
            snd: KProperty1<T, Any?>,
            vararg rest: KProperty1<T, Any?>
        ): AtLeastOnePropertyIsSet<T> = AtLeastOnePropertyIsSet(setOf(fst) + snd + rest)
    }
}