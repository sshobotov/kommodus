# kommodus
Kotlin validation with a focus on readability

```kotlin
data class Test(val one: Int, val two: String?, val three: String, val four: Double?)
data class Test2(val head: Test, val rest: List<Test>, val some: List<Long>)

val instance1 = Test(1, "ha!", "", 45.0)
val instance2 = Test2(instance1, listOf(instance1), listOf())

Validation
    .where(Test2::head).hasValidProperties(
        Validation
            .where(Test::one).minimum(5)
            .where(Test::two).required().notBlank()
            .where(Test::three).notBlank()
    )
    .where(Test2::rest).notEmpty().forEachElement {
        it.hasValidProperties(
            Validation
                .where(Test::one).maximum(20)
        )
    }
    .where(Test2::some).forEachElement { it.maximum(10) }
    .invoke(instance2)

```

<p align="center">
  <img src="https://allthingsd.com/files/2012/07/commodus_thumb.png" alt="he who decides"/>
</p>
