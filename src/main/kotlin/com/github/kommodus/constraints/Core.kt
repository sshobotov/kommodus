package com.github.kommodus.constraints

import com.github.kommodus.*
import com.github.kommodus.constraints.definitions.*
import kotlin.reflect.KProperty1

fun <T, A> FieldDescriptor<T, A?>.required(): FieldDescriptor.Terminal<T, A?> =
    demands(Required())

fun <T, C: Comparable<C>, A: C?> FieldDescriptor<T, A>.maximum(
    limit: C,
    inclusive: Boolean = true
): FieldDescriptor.Terminal<T, A> =
    demands(Maximum(limit, inclusive).considerNullableInput())

fun <A: Comparable<A>> RepeatableDescriptor<A>.maximum(
    limit: A,
    inclusive: Boolean = true
): RepeatableDescriptor.Terminal<A> =
    demands(Maximum(limit, inclusive))

fun <T, C: Comparable<C>, A: C?> FieldDescriptor<T, A>.minimum(
    limit: C,
    inclusive: Boolean = true
): FieldDescriptor.Terminal<T, A> =
    demands(Minimum(limit, inclusive).considerNullableInput())

fun <A: Comparable<A>> RepeatableDescriptor<A>.minimum(
    limit: A,
    inclusive: Boolean = true
): RepeatableDescriptor.Terminal<A> =
    demands(Minimum(limit, inclusive))

fun <T, A: String?> FieldDescriptor<T, A>.notBlank(): FieldDescriptor.Terminal<T, A> =
    demands(NotBlank.considerNullableInput())

fun RepeatableDescriptor<String>.notBlank(): RepeatableDescriptor.Terminal<String> =
    demands(NotBlank)

fun <T, A: String?> FieldDescriptor<T, A>.matches(pattern: Regex, description: String): FieldDescriptor.Terminal<T, A> =
    demands(Matches(pattern, description).considerNullableInput())

fun RepeatableDescriptor<String>.matches(pattern: Regex, description: String): RepeatableDescriptor.Terminal<String> =
    demands(Matches(pattern, description))

fun <T, E, A: Collection<E>?> FieldDescriptor<T, A>.notEmpty(): FieldDescriptor.Terminal<T, A> =
    demands(NotEmpty<Collection<E>>().considerNullableInput())

fun <T, C, A: C?> FieldDescriptor<T, A>.hasAtLeastOneNotNull(
    fst: KProperty1<C, Any?>,
    snd: KProperty1<C, Any?>,
    vararg rest: KProperty1<C, Any?>
): FieldDescriptor.Terminal<T, A> =
    demands(AtLeastOneNotNull(fst, snd, *rest).considerNullableInput())

fun <A> RepeatableDescriptor<A>.hasAtLeastOneNotNull(
    fst: KProperty1<A, Any?>,
    snd: KProperty1<A, Any?>,
    vararg rest: KProperty1<A, Any?>
): RepeatableDescriptor.Terminal<A> =
    demands(AtLeastOneNotNull(fst, snd, *rest))

fun <T> ClassDescriptor<T>.hasAtLeastOneNotNull(
    fst: KProperty1<T, Any?>,
    snd: KProperty1<T, Any?>,
    vararg rest: KProperty1<T, Any?>
): ClassDescriptor.Terminal<T> =
    demands(AtLeastOneNotNull(fst, snd, *rest))
