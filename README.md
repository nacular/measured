<div style="text-align:center"><img src="docs/img/measured.png" alt="measured" style="height:200px;margin-bottom:50px"></div>
<div style="text-align:center"><h1>Measured: intuitive, type-safe units</h1></div>

[![Kotlin 1.9.23](https://img.shields.io/badge/Kotlin_1.9.23-blue.svg?style=for-the-badge&logo=kotlin&logoColor=white)](http://kotlinlang.org)
[![JS, Wasm, JVM, iOS, Mac](https://img.shields.io/badge/JS%2C_Wasm%2C_JVM%2C_iOS%2C_Mac-purple?style=for-the-badge&logo=kotlin&logoColor=white)](https://kotlinlang.org/docs/js-overview.html)
[![License: MIT](https://img.shields.io/badge/MIT_License-green.svg?style=for-the-badge)](https://github.com/nacular/measured/blob/master/LICENSE)

Measured provides a safe and simple way to work with units of measure. It uses the compiler to ensure correctness, and provides intuitive, mathematical operations to work with any units. This means you can write more robust code that avoids implicit units. Time handling for example, is often done with implicit assumptions about milliseconds vs microseconds or seconds. Measured helps you avoid pitfalls like these.

```kotlin
interface Clock {
    fun now(): Measure<Time>
}

fun handleUpdate(duration: Measure<Time>) {
    // ...
    reportTimeInMillis(duration `in` milliseconds)
}

fun update(clock: Clock) {
    val startTime = clock.now()

//...

    handleUpdate(clock.now() - startTime)
}

fun reportTimeInMillis(time: Double) {}
```

## Complex Units

Use division and multiplication to create compound measures. Convert between these safely and easily with the `as` and `in` methods.

```kotlin
val velocity     = 5 * meters / seconds
val acceleration = 9 * meters / (seconds * seconds)
val time         = 1 * minutes

//  d            = vt + ½at²
val distance     = velocity * time + 1.0 / 2 * acceleration * time * time

println(distance                ) // 16500 m
println(distance `as` kilometers) // 16.5 km
println(distance `as` miles     ) // 10.25262467191601 mi

println(5 * miles / hours `as` meters / seconds) // 2.2352 m/s
```

The `as` method converts a `Measure` from its current `Unit` to another. The result is another `Measure`. While `in` returns the magnitude of a `Measure` in the given `Unit`.

## Avoid Raw Values

Measure's support of math operators helps you avoid working with raw values directly.

```kotlin
// typealias Velocity = UnitRatio<Length, Time> defined in the library

val marathon              = 26 * miles
val velocity              = 3 * kilometers / hours
val timeToRunHalfMarathon = (marathon / 2) / velocity // 6.973824 hr

fun calculateTime(distance: Measure<Length>, velocity: Measure<Velocity>): Measure<Time> {
    return distance / velocity
}
```

## Extensible

You can easily add new conversions to existing units and they will work as expected.

```kotlin
val hands = Length("hands", 0.1016)                 // define new Length unit

val l1 = 5 * hands
val l2 = l1 `as` meters                             // convert to Measure with new unit

val v: Measure<Velocity> = 100_000 * hands / hours

println("$l1 == $l2 or ${l1 `in` meters}")          // 5.0 hands == 0.508 m or 0.508

println(v `as` hands / seconds)                     // 27.77777777777778 hands/s
println(v `as` miles / hours  )                     // 6.313131313131313 mi/hr
```

You can also define entirely new units with a set of conversions and have them interact with other units.

```kotlin
// Define a custom Units type
class Blits(suffix: String, ratio: Double = 1.0): Units(suffix, ratio) {
    operator fun div(other: Blits) = ratio / other.ratio

    companion object {
        // Various conversions

        val bloop = Blits("bp"        ) // the base unit
        val blick = Blits("bk",   10.0)
        val blat  = Blits("cbt", 100.0)
    }
}

// Some typealiases to help with readability

typealias BlitVelocity     = UnitsRatio<Blits, Time>
typealias BlitAcceleration = UnitsRatio<Blits, UnitsProduct<Time, Time>>

val m1: Measure<BlitAcceleration>   = 5 * blat / (seconds * seconds)
val m2: Measure<BlitVelocity>       = m1 * 10 * minutes
val m3: Measure<InverseUnits<Time>> = m2 / (5 * blick)
```

## Current Limitations

Measured uses Kotlin's type system to enable compile-time validation. This works really well in most cases, but there
are things the type system currently does not support. For example, `Units` and `Measures` are **order-sensitive**.

```kotlin
val a: UnitsProduct<Angle, Time> = radians * seconds
val b: UnitsProduct<Time, Angle> = seconds * radians
```

Notice the types for a and b are different.

This can be mitigated on a case by case basis with explicit extension functions that help with order. For example,
you can ensure that `kg` is sorted before `m` by providing the following extension.

```kotlin
// ensure Mass comes before Length when Length * Mass
operator fun Length.times(mass: Mass) = mass * this

val f1 = 1 * (kilograms * meters) / (seconds * seconds)
val f2 = 1 * (meters * kilograms) / (seconds * seconds)

// f1 and f2 now have the same type
```

You can also define an extension on Measure to avoid needing parentheses around kilograms and meters.

```kotlin
// ensure Mass comes before Length when Measure<Length> multiplied by Mass
operator fun Measure<Length>.times(mass: Mass) = amount * (units * mass)
```

Measured currently only supports linear units where all members of a given unit are related by a single magnitude. This
applies to many units, but Fahrenheit and Celsius are examples of temperature units that requires more than a multiplier
for conversion.


## Installation

Measured is a Kotlin [Multi-platform](https://kotlinlang.org/docs/multiplatform-get-started.html) library that targets a wide range of platforms. Simply add a dependency to your app's Gradle build file as follows to start using it.

```kotlin
repositories {
    mavenCentral()
}

dependencies {
    implementation("io.nacular.measured:measured:$VERSION")
}
```

## Contact

- Please see [issues](https://github.com/nacular/measured/issues) to share bugs you find, make feature requests, or just get help with your questions.
- Let us know what you think by leaving a comment or a star ⭐️.