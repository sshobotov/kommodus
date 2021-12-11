package com.github.kommodus.constraints

import com.github.kommodus.CollectionElementsValidator
import com.github.kommodus.consideringNullableInput
import com.github.kommodus.constraints.definitions.NotEmpty
import com.github.kommodus.descriptors.FieldDescriptor
import com.github.kommodus.descriptors.RepeatableDescriptor

fun <T, E, A : Collection<E>?> FieldDescriptor<T, A>.notEmpty(): FieldDescriptor.Terminal<T, A> =
    satisfies(NotEmpty<Collection<E>>().consideringNullableInput())

fun <T, E, A : Collection<E>?> FieldDescriptor<T, A>.withElementsEachAdheres(
    nested: (RepeatableDescriptor.Opened<E>) -> RepeatableDescriptor.Terminal<E>
): FieldDescriptor.Terminal<T, A> =
    satisfies(
        CollectionElementsValidator(
            nested(RepeatableDescriptor.Opened()).constraints
        ).consideringNullableInput()
    )
