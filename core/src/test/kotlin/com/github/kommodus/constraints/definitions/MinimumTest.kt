package com.github.kommodus.constraints.definitions

import io.kotest.core.spec.style.StringSpec
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.forAll

class MinimumTest : StringSpec({
    "detects correctly whether number is greater than expected" {
        forAll(Arb.int(), Arb.int()) { value, limit ->
            Minimum(limit, inclusive = false).check(value) == (value > limit)
        }
    }

    "detects correctly whether number is greater or equal to expected" {
        forAll(Arb.int(), Arb.int()) { value, limit ->
            Minimum(limit, inclusive = true).check(value) == (value >= limit)
        }
    }
})