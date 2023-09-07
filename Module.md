# Module Measured

Measured provides a safe and simple way to work with units of measure. It uses the compiler to ensure correctness, and provides intuitive, mathematical operations to work with any units. This means you can write more robust code that avoids implicit units. Time handling for example, is often done with implicit assumptions about milliseconds vs microseconds or seconds. Measured helps you avoid pitfalls like these.

```kotlin
interface Clock {
    fun now(): Measure<Time>
}

fun handleUpdate(duration: Measure<Time>) {
    // ...
    reportTimeInMillis(duration `in` milliseconds)
}

val startTime = clock.now()

//...

handleUpdate(clock.now() - startTime)
```

### Complex Units

Use division and multiplication to create compound measures. Convert between these safely and easily with the `as` and `in` methods.

```kotlin
val velocity     = 5 * meters / seconds
val acceleration = 9 * meters / (seconds * seconds)
val time         = 1 * minutes

//  d = vt + ½at²
val distance     = velocity * time + 1.0/2 * acceleration * time * time

println(distance                ) // 16500 m
println(distance `as` kilometers) // 16.5 km
println(distance `as` miles     ) // 10.25262467191601 mi

println(5 * miles / hours `as` meters / seconds) // 2.2352 m/s
```

The `as` method converts a `Measure` from its current `Unit` to another. The result is another `Measure`. While `in` returns the magnitude of a `Measure` in the given `Unit`.

### Avoid Raw Values

Measure's support of math operators helps you avoid working with raw values directly.

```kotlin
val marathon              = 26 * miles
val velocity              = 3 * kilometers / hours
val timeToRunHalfMarathon = (marathon / 2) / velocity // 6.973824 hr

typealias Velocity = UnitRatio<Length, Time>

fun calculateTime(distance: Measure<Length>, velocity: Measure<Velocity>): Measure<Time> {
    return velocity * distance
}
```

### Extensible

You can easily add new units and have them work like those that ship with the library.