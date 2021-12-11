package com.github.kommodus.constraints.definitions

import io.kotest.core.spec.style.StringSpec
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.forAll

class MaximumTest : StringSpec({
    "detects correctly whether number is less than expected" {
        forAll(Arb.int(), Arb.int()) { value, limit ->
            Maximum(limit, inclusive = false).check(value) == (value < limit)
        }
    }

    "detects correctly whether number is less or equal to expected" {
        forAll(Arb.int(), Arb.int()) { value, limit ->
            Maximum(limit, inclusive = true).check(value) == (value <= limit)
        }
    }
})