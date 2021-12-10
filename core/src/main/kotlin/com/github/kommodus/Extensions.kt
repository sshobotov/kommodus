package com.github.kommodus

fun <T> Validation.Validator<T>.consideringNullableInput(): Validation.Validator<T?> =
    NullableValueValidator(this)

internal fun <T> List<Validation.Validator<T>>.validateAll(
    value: T,
    segment: Validation.InvalidPath.Segment?
): ValidationErrors =
    this
        .flatMap { it.validate(value).toList() }
        // group causes by path
        .groupBy({ it.first }, { it.second })
        .entries
        .associateBy(
            { segment?.let(it.key::prepend) ?: it.key },
            { it.value.flatten() }
        )