package com.github.kommodus.constraints.definitions

import io.kotest.core.spec.style.StringSpec
import io.kotest.property.exhaustive.exhaustive
import io.kotest.property.forAll

class AtLeastOnePropertyIsSetTest: StringSpec({
    "detects correctly whether object has at least one of the properties set" {
        data class EmailMessage(
            val subject: String?,
            val to: String?,
            val cc: String?
        )
        val validator = AtLeastOnePropertyIsSet(EmailMessage::subject, EmailMessage::to)
        val generator = listOf(null, "yolo").exhaustive()

        forAll(generator, generator, generator) { subject, to, cc ->
            EmailMessage(subject, to, cc).let { obj ->
                validator.check(obj) == (obj.subject != null || obj.to != null)
            }
        }
    }
})