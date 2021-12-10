package com.github.kommodus.examples

import com.github.kommodus.constraints.*
import com.github.kommodus.Validation

data class Geometry(val width: Int?, val height: Int?)
data class Position(val x: Int?, val y: Int?)
data class Tag(val name: String, val color: String?)
data class Changes(val geometry: Geometry?, val position: Position?, val tags: Set<Tag>?)

fun main() {
    val examinable = Changes(Geometry(null, null), Position(10, null), setOf(
        Tag("problem", "#FF0000"),
        Tag("solution", "blue")
    ))
    val validation =
        Validation
            .whereInstanceOf<Changes>()
                .withAtLeastOnePropertySet(Changes::geometry, Changes::position, Changes::tags)
            .andProperty(Changes::geometry)
                .satisfies("Either height or width should be provided") {
                    (it.height == null) xor (it.width == null)
                }
                .passes(Validation
                    .whereProperty(Geometry::height).greaterThan(0)
                    .andProperty(Geometry::width).greaterThan(0)
                )
            .andProperty(Changes::position).withAtLeastOnePropertySet(Position::x, Position::y)
            .andProperty(Changes::tags).withElementsEachAdheres {
                it.passes(Validation
                    .whereProperty(Tag::name).notBlank()
                    .andProperty(Tag::color).matches(Regex("^#[a-f0-9]{6}$", RegexOption.IGNORE_CASE), "color hex")
                )
            }

    val result = validation.applyTo(examinable)
    println(result)
}