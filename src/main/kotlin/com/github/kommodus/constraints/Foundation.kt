package com.github.kommodus.constraints

import com.github.kommodus.Validation
import com.github.kommodus.descriptors.FieldDescriptor
import com.github.kommodus.descriptors.RepeatableDescriptor
import com.github.kommodus.CollectionElementsValidator
import com.github.kommodus.InstanceFieldsValidator
import com.github.kommodus.NullableValueValidator

fun <T, C, A: C?> FieldDescriptor<T, A>.passes(nested: Validation<C>): FieldDescriptor.Terminal<T, A> =
    satisfies(InstanceFieldsValidator(nested).considerNullableInput())

fun <A> RepeatableDescriptor<A>.passes(nested: Validation<A>): RepeatableDescriptor.Terminal<A> =
    satisfies(InstanceFieldsValidator(nested))

fun <T, E, A: Collection<E>?> FieldDescriptor<T, A>.withElementsEachAdheres(
    nested: (RepeatableDescriptor.Opened<E>) -> RepeatableDescriptor.Terminal<E>
): FieldDescriptor.Terminal<T, A> =
    satisfies(CollectionElementsValidator(
        nested(RepeatableDescriptor.Opened()).constraints
    ).considerNullableInput())

fun <T> Validation.Validator<T>.considerNullableInput(): Validation.Validator<T?> =
    NullableValueValidator(this)