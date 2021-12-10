package com.github.kommodus.constraints

import com.github.kommodus.InstanceFieldsValidator
import com.github.kommodus.Validation
import com.github.kommodus.consideringNullableInput
import com.github.kommodus.constraints.definitions.AtLeastOnePropertyIsSet
import com.github.kommodus.descriptors.ClassDescriptor
import com.github.kommodus.descriptors.FieldDescriptor
import com.github.kommodus.descriptors.RepeatableDescriptor
import kotlin.reflect.KProperty1

fun <T, C, A: C?> FieldDescriptor<T, A>.passes(nested: Validation<C>): FieldDescriptor.Terminal<T, A> =
    satisfies(InstanceFieldsValidator(nested).consideringNullableInput())

fun <A> RepeatableDescriptor<A>.passes(nested: Validation<A>): RepeatableDescriptor.Terminal<A> =
    satisfies(InstanceFieldsValidator(nested))

fun <T, C, A: C?> FieldDescriptor<T, A>.withAtLeastOnePropertySet(
    fst: KProperty1<C, Any?>,
    snd: KProperty1<C, Any?>,
    vararg rest: KProperty1<C, Any?>
): FieldDescriptor.Terminal<T, A> =
    satisfies(AtLeastOnePropertyIsSet(fst, snd, *rest).consideringNullableInput())

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