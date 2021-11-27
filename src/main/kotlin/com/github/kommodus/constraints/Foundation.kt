package com.github.kommodus.constraints

import com.github.kommodus.*
import com.github.kommodus.ValidNullableConstraint
import com.github.kommodus.ValidIfAppliedConstraint

fun <T, C, A: C?> FieldDescriptor<T, A>.passes(nested: Validation<C>): FieldDescriptor.Terminal<T, A> =
    satisfies(ValidIfAppliedConstraint(nested).considerNullableInput())

fun <A> RepeatableDescriptor<A>.passes(nested: Validation<A>): RepeatableDescriptor.Terminal<A> =
    satisfies(ValidIfAppliedConstraint(nested))

fun <T, E, A: Collection<E>?> FieldDescriptor<T, A>.withElementsEachAdheres(
    nested: (RepeatableDescriptor.Opened<E>) -> RepeatableDescriptor.Terminal<E>
): FieldDescriptor.Terminal<T, A> =
    satisfies(ValidElementsConstraint(
        nested(RepeatableDescriptor.Opened()).constraints
    ).considerNullableInput())

fun <T> Validation.Constraint<T>.considerNullableInput(): Validation.Constraint<T?> =
    ValidNullableConstraint(this)