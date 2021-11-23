package com.github.kommodus.constraints

import com.github.kommodus.FieldDescriptor
import com.github.kommodus.Validation
import com.github.kommodus.constraints.definitions.Maximum
import com.github.kommodus.constraints.definitions.NotBlank
import com.github.kommodus.constraints.definitions.NullableConstraint

fun <T, C: Comparable<C>, A: C?> FieldDescriptor<T, A>.maximum(limit: C): FieldDescriptor.Terminal<T, A> =
    add(Maximum(limit).includeNullableInput())

fun <T, A: String?> FieldDescriptor<T, A>.notBlank(): FieldDescriptor.Terminal<T, A> =
    add(NotBlank.includeNullableInput())

fun <T> Validation.Constraint<T>.includeNullableInput(): Validation.Constraint<T?> =
    NullableConstraint(this, ifNullResult = true)