package com.github.kommodus.constraints.definitions

import io.kotest.core.spec.style.StringSpec
import io.kotest.property.Arb
import io.kotest.property.arbitrary.*
import io.kotest.property.forAll

class NotEmptyTest: StringSpec({
    "detects correctly whether arbitrary collection is empty" {
        forAll(Arb.choice<Collection<Any>>(
            Arb.list(Arb.boolean()),
            Arb.set(Arb.string()),
        )) { collection ->
            NotEmpty<Collection<Any>>().check(collection) == collection.isNotEmpty()
        }
    }
})