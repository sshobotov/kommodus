package com.github.kommodus

import com.github.kommodus.constraints.*
import kotlin.reflect.KProperty1

data class Test(val one: Int, val two: String?, val three: String, val four: Double?)

data class Test2(val head: Test, val rest: List<Test>, val some: List<Long>)

// Dependent fields case
data class Geometry(val width: Int?, val height: Int?)

data class Position(val x: Int?, val y: Int?)

data class Changes(val geometry: Geometry?, val position: Position?)

class AtLeastOneIsPresent<T> private constructor(private val set: Set<KProperty1<T, Any?>>): Validation.Constraint<T> {
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
        ): AtLeastOneIsPresent<T> = AtLeastOneIsPresent(setOf(fst) + snd + rest)
    }
}

fun main(args: Array<String>) {
    val instance1 = Test(1, "ha!", "", 45.0)
    val validator1: Validation<Test> =
        Validation
            .where(Test::one).maximum(100)
            .where(Test::two).required().notBlank()
            .where(Test::three).notBlank()
            .where(Test::four).maximum(25.0)

    val result1 = validator1.invoke(instance1)
    println(result1)

    val instance2 = Test2(instance1, listOf(instance1), listOf())
    val validator2: Validation<Test2> =
        Validation
            .where(Test2::head).hasValidProperties(
                Validation
                    .where(Test::three).notBlank()
            )
            .where(Test2::rest).notEmpty().forEachElement {
                it.hasValidProperties(
                    Validation
                        .where(Test::one).maximum(0)
                )
            }
            .where(Test2::some).forEachElement { it.maximum(10) }

    val result2 = validator2.invoke(instance2)
    println(result2)

    val instance3 = Changes(Geometry(null, null), Position(10, null))
    val validator3 =
        Validation
            .where(Changes::geometry)
                .demands(AtLeastOneIsPresent(Geometry::width, Geometry::height).considerNullableInput())
            .where(Changes::position)
                .demands(AtLeastOneIsPresent(Position::x, Position::y).considerNullableInput())
            // TODO: Validation for geometry itself

    val result3 = validator3.invoke(instance3)
    println(result3)
}