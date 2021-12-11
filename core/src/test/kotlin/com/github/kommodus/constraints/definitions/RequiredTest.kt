package com.github.kommodus.constraints.definitions

import io.kotest.core.spec.style.StringSpec
import io.kotest.property.Arb
import io.kotest.property.arbitrary.choice
import io.kotest.property.arbitrary.long
import io.kotest.property.arbitrary.orNull
import io.kotest.property.arbitrary.string
import io.kotest.property.forAll

class RequiredTest : StringSpec({
    "detects correctly whether arbitrary value is not null" {
        forAll(Arb.choice(Arb.string().orNull(0.5), Arb.long().orNull(0.5))) { value ->
            Required<Any>().check(value) == (value != null)
        }
    }
})