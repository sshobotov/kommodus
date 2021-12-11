package com.github.kommodus

import com.github.kommodus.constraints.definitions.Predicate
import com.github.kommodus.Validation.InvalidCause
import com.github.kommodus.Validation.InvalidPath
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class ExtensionsTest : StringSpec({
    "List<Validator>.validateAll combines validation errors of all validators" {
        val result = listOf<Validator<List<Int>>>(
            Predicate({ false }, "reason #1"),
            Predicate({ true }, "reason #2"),
            CollectionElementsValidator(listOf(
                Predicate({ false }, "reason #3"),
            )),
            Predicate({ false }, "reason #4"),
        ).validateAll(listOf(42), segment = null)

        result shouldBe mapOf(
            InvalidPath(listOf()) to listOf(
                InvalidCause(Predicate::class, "reason #1"),
                InvalidCause(Predicate::class, "reason #4"),
            ),
            InvalidPath(listOf(InvalidPath.Index(0))) to listOf(
                InvalidCause(Predicate::class, "reason #3"),
            )
        )
    }

    "List<Validator>.validateAll picks distinct validation errors per path" {
        val result = listOf<Validator<List<Int>>>(
            Predicate({ false }, "reason #1"),
            Predicate({ true }, "reason #1"),
            CollectionElementsValidator(listOf(
                Predicate({ false }, "reason #1"),
            )),
            Predicate({ false }, "reason #1"),
        ).validateAll(listOf(42), segment = null)

        result shouldBe mapOf(
            InvalidPath(listOf()) to listOf(
                InvalidCause(Predicate::class, "reason #1"),
            ),
            InvalidPath(listOf(InvalidPath.Index(0))) to listOf(
                InvalidCause(Predicate::class, "reason #1"),
            )
        )
    }

    "List<Validator>.validateAll prepends segment as parent one to each path" {
        data class Container(val numbers: List<Int>)
        val parentSegment = InvalidPath.Property(Container::numbers)

        val result = listOf<Validator<List<Int>>>(
            Predicate({ false }, "reason #1"),
            CollectionElementsValidator(listOf(
                Predicate({ false }, "reason #2"),
            )),
        ).validateAll(listOf(42), parentSegment)

        result shouldBe mapOf(
            InvalidPath(listOf(parentSegment)) to listOf(
                InvalidCause(Predicate::class, "reason #1"),
            ),
            InvalidPath(listOf(parentSegment, InvalidPath.Index(0))) to listOf(
                InvalidCause(Predicate::class, "reason #2"),
            )
        )
    }
})
