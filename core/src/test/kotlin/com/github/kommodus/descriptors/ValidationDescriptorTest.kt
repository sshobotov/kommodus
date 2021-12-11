package com.github.kommodus.descriptors

import com.github.kommodus.Validation
import io.kotest.core.spec.style.StringSpec
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe

class ValidationDescriptorTest : StringSpec({
    data class Video(val url: String, val author: String)

    fun <T> validator(reason: String, result: () -> Boolean = { false }) =
        object : Validation.Constraint<T>() {
            override fun check(value: T): Boolean = result()

            override fun message(): String = reason
        }

    "given set of validators should apply it per property" {
        val instance = ValidationDescriptor<Video>()
            .put(listOf(validator("root reason")))
            .put(Video::url, listOf(validator("url reason")))
            .put(Video::author, listOf(validator("author reason")))

        val result = instance.applyTo(Video("test.co/video", "Maximus"))

        (result as Validation.Result.Invalid<Video>).asStringsMap() shouldBe mapOf(
            "" to listOf("root reason"),
            "url" to listOf("url reason"),
            "author" to listOf("author reason")
        )
    }

    "re-putting validators for the same property should use latest only" {
        val instance = ValidationDescriptor<Video>()
            .put(Video::url, listOf(validator("url reason #1") { throw Exception("Oops") }))
            .put(Video::url, listOf(validator("url reason #2")))
            .put(Video::url, listOf(validator("url reason #3") { true }))
            .put(Video::url, listOf(validator("url reason #4")))

        val result = instance.applyTo(Video("test.co/video", "Maximus"))

        (result as Validation.Result.Invalid<Video>).asStringsMap() shouldBe mapOf(
            "url" to listOf("url reason #4"),
        )
    }

    "given multiple validators should report only failed" {
        val instance = ValidationDescriptor<Video>()
            .put(
                Video::url, listOf(
                    validator("url validation #1"),
                    validator("url validation #2") { true },
                    validator("url validation #3"),
                )
            )

        val result = instance.applyTo(Video("test.co/video", "Maximus"))

        (result as Validation.Result.Invalid<Video>).asStringsMap() shouldBe mapOf(
            "url" to listOf("url validation #1", "url validation #3"),
        )
    }

    "given multiple validators should not report errors if all are valid" {
        val instance = ValidationDescriptor<Video>()
            .put(
                Video::url, listOf(
                    validator("url validation #1") { true },
                    validator("url validation #2") { true },
                    validator("url validation #3") { true },
                )
            )
        val verified = Video("test.co/video", "Maximus")

        val result = instance.applyTo(verified)

        result shouldBe Validation.Result.Valid(verified)
    }

    "given no validators should not report errors" {
        withData(
            listOf(
                ValidationDescriptor(),
                ValidationDescriptor<Video>().put(listOf()),
                ValidationDescriptor<Video>().put(Video::url, listOf()),
            )
        ) { instance ->
            val verified = Video("test.co/video", "Maximus")

            val result = instance.applyTo(verified)

            result shouldBe Validation.Result.Valid(verified)
        }
    }
})