package com.github.kommodus.constraints

import com.github.kommodus.FieldDescriptor
import com.github.kommodus.RepeatableDescriptor
import com.github.kommodus.constraints.definitions.Maximum
import com.github.kommodus.constraints.definitions.NotBlank
import com.github.kommodus.constraints.definitions.NotEmpty
import com.github.kommodus.constraints.definitions.Required

fun <T, A> FieldDescriptor<T, A?>.required(): FieldDescriptor.Terminal<T, A?> =
    includes(Required())

fun <T, C: Comparable<C>, A: C?> FieldDescriptor<T, A>.maximum(limit: C): FieldDescriptor.Terminal<T, A> =
    includes(Maximum(limit).considerNullableInput())

fun <A: Comparable<A>> RepeatableDescriptor<A>.maximum(limit: A): RepeatableDescriptor.Terminal<A> =
    includes(Maximum(limit))

fun <T, A: String?> FieldDescriptor<T, A>.notBlank(): FieldDescriptor.Terminal<T, A> =
    includes(NotBlank.considerNullableInput())

fun RepeatableDescriptor<String>.notBlank(): RepeatableDescriptor.Terminal<String> =
    includes(NotBlank)

fun <T, E, A: Collection<E>?> FieldDescriptor<T, A>.notEmpty(): FieldDescriptor.Terminal<T, A> =
    includes(NotEmpty<Collection<E>>().considerNullableInput())
