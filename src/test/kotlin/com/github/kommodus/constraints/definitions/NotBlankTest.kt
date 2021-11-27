package com.github.kommodus.constraints.definitions

import io.kotest.core.spec.style.StringSpec
import io.kotest.property.forAll

class NotBlankTest: StringSpec({
    "checks whether string is not blank (all or empty)" {
        forAll<String> {
            NotBlank.check(it) == it.isNotBlank()
        }
    }
})
