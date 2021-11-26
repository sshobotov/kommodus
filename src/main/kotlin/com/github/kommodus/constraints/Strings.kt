package com.github.kommodus.constraints

import com.github.kommodus.FieldDescriptor
import com.github.kommodus.RepeatableDescriptor
import com.github.kommodus.constraints.definitions.Matches
import com.github.kommodus.constraints.definitions.NotBlank

fun <T, A: String?> FieldDescriptor<T, A>.notBlank(): FieldDescriptor.Terminal<T, A> =
    demands(NotBlank.considerNullableInput())

fun RepeatableDescriptor<String>.notBlank(): RepeatableDescriptor.Terminal<String> =
    demands(NotBlank)

fun <T, A: String?> FieldDescriptor<T, A>.matches(pattern: Regex, description: String): FieldDescriptor.Terminal<T, A> =
    demands(Matches(pattern, description).considerNullableInput())

fun RepeatableDescriptor<String>.matches(pattern: Regex, description: String): RepeatableDescriptor.Terminal<String> =
    demands(Matches(pattern, description))