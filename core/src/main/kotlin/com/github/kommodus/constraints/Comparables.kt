package com.github.kommodus.constraints

import com.github.kommodus.descriptors.FieldDescriptor
import com.github.kommodus.descriptors.RepeatableDescriptor
import com.github.kommodus.consideringNullableInput
import com.github.kommodus.constraints.definitions.Maximum
import com.github.kommodus.constraints.definitions.Minimum

fun <T, C: Comparable<C>, A: C?> FieldDescriptor<T, A>.lessOrEqualsTo(
    limit: C
): FieldDescriptor.Terminal<T, A> =
    satisfies(Maximum(limit, inclusive = true).consideringNullableInput())

fun <T, C: Comparable<C>, A: C?> FieldDescriptor<T, A>.lessThan(
    limit: C
): FieldDescriptor.Terminal<T, A> =
    satisfies(Maximum(limit, inclusive = false).consideringNullableInput())

fun <A: Comparable<A>> RepeatableDescriptor<A>.lessOrEqualsTo(
    limit: A
): RepeatableDescriptor.Terminal<A> =
    satisfies(Maximum(limit, inclusive = true))

fun <A: Comparable<A>> RepeatableDescriptor<A>.lessThan(
    limit: A
): RepeatableDescriptor.Terminal<A> =
    satisfies(Maximum(limit, inclusive = false))

fun <T, C: Comparable<C>, A: C?> FieldDescriptor<T, A>.greaterOrEqualsTo(
    limit: C
): FieldDescriptor.Terminal<T, A> =
    satisfies(Minimum(limit, inclusive = true).consideringNullableInput())

fun <T, C: Comparable<C>, A: C?> FieldDescriptor<T, A>.greaterThan(
    limit: C
): FieldDescriptor.Terminal<T, A> =
    satisfies(Minimum(limit, inclusive = false).consideringNullableInput())

fun <A: Comparable<A>> RepeatableDescriptor<A>.greaterOrEqualsTo(
    limit: A
): RepeatableDescriptor.Terminal<A> =
    satisfies(Minimum(limit, inclusive = true))

fun <A: Comparable<A>> RepeatableDescriptor<A>.greaterThan(
    limit: A
): RepeatableDescriptor.Terminal<A> =
    satisfies(Minimum(limit, inclusive = false))