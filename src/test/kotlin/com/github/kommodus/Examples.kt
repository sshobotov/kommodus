package com.github.kommodus

import com.github.kommodus.constraints.*

data class Test(val one: Int, val two: String?, val three: String, val four: Double?)

data class Test2(val head: Test, val rest: List<Test>, val some: List<Long>)

// Dependent fields case
data class Geometry(val width: Int?, val height: Int?)

data class Position(val x: Int?, val y: Int?)

data class Changes(val geometry: Geometry?, val position: Position?)

fun main() {
    val instance1 = Test(1, "ha!", "", 45.0)
    val validator1: Validation<Test> =
        Validation
            .whereProperty(Test::one).lessThan(100)
            .andProperty(Test::two).required().notBlank()
            .andProperty(Test::three).notBlank()
            .andProperty(Test::four).greaterThan(25.0)

    val result1 = validator1.applyTo(instance1)
    println(result1)

    val instance2 = Test2(instance1, listOf(instance1), listOf())
    val validator2: Validation<Test2> =
        Validation
            .whereProperty(Test2::head).passes(
                Validation
                    .whereProperty(Test::three).notBlank().matches(Regex("^#[a-f]{6}$"), "Color hex")
            )
            .andProperty(Test2::rest).notEmpty().withElementsEachAdheres {
                it.passes(
                    Validation
                        .whereProperty(Test::one).lessOrEqualsTo(0)
                )
            }
            .andProperty(Test2::some).withElementsEachAdheres { it.lessThan(10) }

    val result2 = validator2.applyTo(instance2)
    println(result2)

    val instance3 = Changes(Geometry(null, null), Position(10, null))
    val validator3 =
        Validation
            .whereInstanceOf<Changes>().withAtLeastOnePropertySet(Changes::geometry, Changes::position)
            .andProperty(Changes::geometry)
                .satisfies("Either height or width should be provided") {
                    (it.height == null) xor (it.width == null)
                }
                .passes(Validation
                    .whereProperty(Geometry::height).greaterThan(0)
                    .andProperty(Geometry::width).greaterThan(0)
                )
            .andProperty(Changes::position).withAtLeastOnePropertySet(Position::x, Position::y)

    val result3 = validator3.applyTo(instance3)
    println(result3)
}