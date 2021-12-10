package com.github.kommodus.constraints.definitions

import io.kotest.core.spec.style.StringSpec
import io.kotest.property.Arb
import io.kotest.property.arbitrary.stringPattern
import io.kotest.property.forAll

class MatchesTest: StringSpec({
    "detects correctly when string matches pattern" {
        val pattern = "^[a-z ]+!$"
        val matcher = Matches(Regex(pattern), "test pattern")

        forAll(Arb.stringPattern(pattern)) { value ->
            matcher.check(value)
        }
    }
    "detects correctly when string doesn't match pattern" {
        val pattern = "^[a-z ]+!$"
        val matcher = Matches(Regex(pattern), "test pattern")

        forAll(Arb.stringPattern("^[^!]*$")) { value ->
            !matcher.check(value)
        }
    }
})