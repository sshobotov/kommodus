package com.github.kommodus.constraints

import com.github.kommodus.consideringNullableInput
import com.github.kommodus.constraints.definitions.Matches
import com.github.kommodus.constraints.definitions.NotBlank
import com.github.kommodus.descriptors.FieldDescriptor
import com.github.kommodus.descriptors.RepeatableDescriptor

fun <T, A : String?> FieldDescriptor<T, A>.notBlank(): FieldDescriptor.Terminal<T, A> =
    satisfies(NotBlank.consideringNullableInput())

fun RepeatableDescriptor<String>.notBlank(): RepeatableDescriptor.Terminal<String> =
    satisfies(NotBlank)

fun <T, A : String?> FieldDescriptor<T, A>.matches(
    pattern: Regex,
    description: String
): FieldDescriptor.Terminal<T, A> =
    satisfies(Matches(pattern, description).consideringNullableInput())

fun RepeatableDescriptor<String>.matches(pattern: Regex, description: String): RepeatableDescriptor.Terminal<String> =
    satisfies(Matches(pattern, description))