# kommodus
Validation for Kotlin with a focus on readability and type-safety

```kotlin
import com.github.kommodus.constraints.*
import com.github.kommodus.Validation

data class Geometry(val width: Int?, val height: Int?)
data class Position(val x: Int?, val y: Int?)
data class Changes(val geometry: Geometry?, val position: Position?)

val validation = Validation
    .whereInstanceOf<Changes>().withAtLeastOnePropertySet(Changes::geometry, Changes::position)
    .andProperty(Changes::geometry)
    .satisfies("Either height or width should be provided") {
        (it.height == null) xor (it.width == null)
    }
    .passes(Validation
        .whereProperty(Geometry::height).greaterThan(0)
        .andProperty(Geometry::width).greaterThan(0)
    )
    .andProperty(Changes::position).withAtLeastOnePropertySet(Position::x, Position::y)

validation.applyTo(Changes(Geometry(null, null), Position(10, null)))
```

<p align="center">
  <img src="https://allthingsd.com/files/2012/07/commodus_thumb.png" alt="he who decides"/>
</p>
