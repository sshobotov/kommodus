package com.github.kommodus

import com.github.kommodus.constraints.*

data class Test(val one: Int, val two: String?, val three: String, val four: Double?)

data class Test2(val two: Int)

fun main(args: Array<String>) {
    val instance1 = Test(1, "ha!", "", 45.0)
    val validator1: Validation<Test> =
        Validation
            .where(Test::one).maximum(100)
            .where(Test::two).notBlank()
            .where(Test::three).notBlank()
            .where(Test::four).maximum(25.0)

    val result = validator1.invoke(instance1)
    println(result)
}