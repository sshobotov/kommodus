package com.github.kommodus.constraints

import com.github.kommodus.*
import com.github.kommodus.NullableConstraint
import com.github.kommodus.ValidPropertiesConstraint

fun <T, C, A: C?> FieldDescriptor<T, A>.hasValidProperties(nested: Validation<C>): FieldDescriptor.Terminal<T, A> =
    includes(ValidPropertiesConstraint(nested).considerNullableInput())

fun <A> RepeatableDescriptor<A>.hasValidProperties(nested: Validation<A>): RepeatableDescriptor.Terminal<A> =
    includes(ValidPropertiesConstraint(nested).considerNullableInput())

fun <T, E, A: Collection<E>?> FieldDescriptor<T, A>.forEachElement(
    nested: (RepeatableDescriptor.Opened<E>) -> RepeatableDescriptor.Terminal<E>
): FieldDescriptor.Terminal<T, A> =
    includes(ValidElementsConstraint(
        nested(RepeatableDescriptor.Opened()).constraints
    ).considerNullableInput())

fun <T> Validation.Constraint<T>.considerNullableInput(): Validation.Constraint<T?> =
    NullableConstraint(this, ifNullResult = true)