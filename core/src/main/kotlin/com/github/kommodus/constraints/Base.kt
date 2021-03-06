package com.github.kommodus.constraints

import com.github.kommodus.consideringNullableInput
import com.github.kommodus.constraints.definitions.Predicate
import com.github.kommodus.constraints.definitions.Required
import com.github.kommodus.descriptors.ClassDescriptor
import com.github.kommodus.descriptors.FieldDescriptor
import com.github.kommodus.descriptors.RepeatableDescriptor

fun <T, A> FieldDescriptor<T, A?>.required(): FieldDescriptor.Terminal<T, A?> =
    satisfies(Required())

fun <T, C, A : C?> FieldDescriptor<T, A>.satisfies(
    ifFailsMessage: String,
    predicate: (C) -> Boolean
): FieldDescriptor.Terminal<T, A> =
    satisfies(Predicate(predicate, ifFailsMessage).consideringNullableInput())

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
