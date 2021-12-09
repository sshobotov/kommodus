package com.github.kommodus.constraints.definitions

import io.kotest.core.spec.style.StringSpec
import io.kotest.property.Arb
import io.kotest.property.arbitrary.map
import io.kotest.property.arbitrary.string
import io.kotest.property.arbitrary.stringPattern
import io.kotest.property.forAll

class MatchesTest: StringSpec({
    "detects correctly whether string matches pattern" {
        val regexGenerator = Arb.stringPattern("^\\^?[a-z.]+[?*+|]?[a-z]*\\$?$")
            .map { Regex(it) }

        forAll(regexGenerator, Arb.string()) { regex, value ->
            Matches(regex, "test pattern").check(value) == regex.matches(value)
        }
    }
})