package com.github.kommodus.constraints

import com.github.kommodus.descriptors.*
import com.github.kommodus.constraints.definitions.*
import kotlin.reflect.KProperty1

fun <T, A> FieldDescriptor<T, A?>.required(): FieldDescriptor.Terminal<T, A?> =
    satisfies(Required())

fun <T, C, A: C?> FieldDescriptor<T, A>.satisfies(
    ifFailsMessage: String,
    predicate: (C) -> Boolean
): FieldDescriptor.Terminal<T, A> =
    satisfies(Predicate(predicate, ifFailsMessage).considerNullableInput())

fun <A> RepeatableDescriptor<A>.satisfies(
    ifFailsMessage: String,
    predicate: (A) -> Boolean
): RepeatableDescriptor.Terminal<A> =
    satisfies(Predicate(predicate, ifFailsMessage))

fun <T> ClassDescriptor<T>.satisfies(
    ifFailsMessage: String,
    predicate: (T) -> Boolean
): ClassDescriptor.Terminal<T> =
    satisfies(Predicate(predicate, ifFailsMessage))

fun <T, E, A: Collection<E>?> FieldDescriptor<T, A>.notEmpty(): FieldDescriptor.Terminal<T, A> =
    satisfies(NotEmpty<Collection<E>>().considerNullableInput())

fun <T, C, A: C?> FieldDescriptor<T, A>.withAtLeastOnePropertySet(
    fst: KProperty1<C, Any?>,
    snd: KProperty1<C, Any?>,
    vararg rest: KProperty1<C, Any?>
): FieldDescriptor.Terminal<T, A> =
    satisfies(AtLeastOnePropertyIsSet(fst, snd, *rest).considerNullableInput())

fun <A> RepeatableDescriptor<A>.withAtLeastOnePropertySet(
    fst: KProperty1<A, Any?>,
    snd: KProperty1<A, Any?>,
    vararg rest: KProperty1<A, Any?>
): RepeatableDescriptor.Terminal<A> =
    satisfies(AtLeastOnePropertyIsSet(fst, snd, *rest))

fun <T> ClassDescriptor<T>.withAtLeastOnePropertySet(
    fst: KProperty1<T, Any?>,
    snd: KProperty1<T, Any?>,
    vararg rest: KProperty1<T, Any?>
): ClassDescriptor.Terminal<T> =
    satisfies(AtLeastOnePropertyIsSet(fst, snd, *rest))
