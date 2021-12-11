package com.github.kommodus.constraints.definitions

import io.kotest.core.spec.style.StringSpec
import io.kotest.property.forAll

class NotBlankTest : StringSpec({
    "detects correctly whether arbitrary string is blank" {
        forAll<String> {
            NotBlank.check(it) == it.isNotBlank()
        }
    }
})
