<div style="font-size:70px"><img src="measured.svg" alt="measured" style="height:70px">easured</div>
<div><h1>Type-safe, intuitive units of measure</h1></div>

[![Kotlin 1.3.72](https://img.shields.io/badge/Kotlin-1.3.72-blue.svg?style=flat&logo=kotlin)](http://kotlinlang.org)
[![License: MIT](https://img.shields.io/badge/License-MIT-green.svg)](https://github.com/nacular/measured/blob/master/LICENSE)

---

Measured provides a safe and simple way to work with units of measure. It uses the compiler to ensure correctness,
and provides intuitive, mathematical operations to work with any units.

This means you can write more robust code that avoids implicit units. Time handling for example, is often done with
implicit assumptions about milliseconds vs microseconds or seconds. Measured helps you avoid pitfalls like these.

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

## Complex units

Use division and multiplication to create compound measures. Convert between these safely and easily with the
`as` and `in` methods.

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

The `as` method converts a `Measure` from its current `Unit` to another. The result is another `Measure`. While `in`
returns the magnitude of a `Measure` in the given `Unit`.

## Avoid raw values

Measure's support of math operators helps you avoid working with raw values directly.

```kotlin
val marathon              = 26 * miles
val velocity              = 3 * kilometers / hours
val timeToRunHalfMarathon = velocity * marathon / 2
``` 

```kotlin
typealias Velocity = UnitRatio<Length, Time>

fun calculateTime(distance: Measure<Length>, velocity: Measure<Velocity>): Measure<Time> {
    return velocity * distance
}
```

## Extensible

You can easily add new units and have them work like those that ship with the library.

## Current Limitations

Measured uses Kotlin's type system to enable compile-time validation. This works really well in most cases, but there
are things the type system currently does not support. For example, `Units` and `Measures` are **order-sensitive**.

```kotlin
val a: UnitProduct<Angle, Time> = radians * seconds
val b: UnitProduct<Time, Angle> = seconds * radians
```

!> Notice the types for a and b are different

This can be mitigated on a case by case basis with explicit extension functions that help with order. For example,
you can ensure that `kg` is sorted before `m` by providing the following extension.

```kotlin
// ensure Mass comes before Length when Length * Mass
operator fun Length.times(mass: Mass) = mass * this
```
```kotlin
val f1 = 1 * (kilograms * meters) / (seconds * seconds)
val f2 = 1 * (meters * kilograms) / (seconds * seconds)

// f1 and f2 now have the same type
```

You can also define an extension on Measure to avoid needing parentheses around kilograms and meters.

```kotlin
// ensure Mass comes before Length when Measure<Length> multiplied by Mass
operator fun Measure<Length>.times(mass: Mass) = amount * (unit * mass)
```

## Installation

Measured uses [metadata publishing](https://kotlinlang.org/docs/reference/building-mpp-with-gradle.html#experimental-metadata-publishing-mode),
so you can simply include the dependency as follows if you build with Gradle 5.3+ or explicitly enabling it via
`enableFeaturePreview("GRADLE_METADATA")` in `settings.gradle`.


<div style="margin-top:3em;font-weight:Bold">build.gradle.kts</div>

```kotlin
kotlin {
    // ...
    dependencies {
        implementation ("io.nacular.measured:measured:$measuredVersion")
    }
}
```

## Contact

- Please see [issues](https://github.com/nacular/measured/issues) to share bugs you find, make feature requests, or just get help with your questions.
- Don't hesitate to ⭐️ [star](https://github.com/nacular/measured) if you find this project useful.