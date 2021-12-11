# Kommodus

Validation for Kotlin with a focus on readability and type-safety. Just start typying `Validation.` and it will guide
you!

```kotlin
import com.github.kommodus.constraints.*
import com.github.kommodus.Validation

data class Geometry(val width: Int?, val height: Int?)
data class Position(val x: Int?, val y: Int?)
data class Tag(val name: String, val color: String?)
data class Changes(val geometry: Geometry?, val position: Position?, val tags: Set<Tag>?)

val validation = Validation
    .whereInstanceOf<Changes>()
    .withAtLeastOnePropertySet(Changes::geometry, Changes::position, Changes::tags)
    .andProperty(Changes::geometry)
    .satisfies("Either height or width should be provided") {
        (it.height == null) xor (it.width == null)
    }
    .passes(
        Validation
            .whereProperty(Geometry::height).greaterThan(0)
            .andProperty(Geometry::width).greaterThan(0)
    )
    .andProperty(Changes::position).withAtLeastOnePropertySet(Position::x, Position::y)
    .andProperty(Changes::tags).withElementsEachAdheres {
        it.passes(
            Validation
                .whereProperty(Tag::name).notBlank()
                .andProperty(Tag::color).matches(Regex("^#[a-f0-9]{6}$", RegexOption.IGNORE_CASE), "color hex")
        )
    }

validation.applyTo(
    Changes(
        Geometry(null, null),
        Position(10, null),
        setOf(Tag("problem", "#FF0000"), Tag("solution", "blue"))
    )
)
```

<p align="center">
  <img src="https://allthingsd.com/files/2012/07/commodus_thumb.png" alt="he who decides"/>
</p>


```
{geometry=[Either height or width should be provided], tags.1.color=[Value doesn't look like a color hex]}
```

You may use simplified output like above, or you can customize and introspect it as you like.

## Motivation

- type-safe validation description - no runtime magic that may fail you in many unexpected ways
- readable validation definition - as if you explain it your colleague in words
- easy to discover API - no need to revisit documentation each time
