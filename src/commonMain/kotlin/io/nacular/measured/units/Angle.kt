@file:Suppress("NOTHING_TO_INLINE")

package io.nacular.measured.units

import kotlin.math.PI

/**
 * Units to measure geometric angles.
 */
open class Angle(suffix: String, ratio: Double = 1.0): Units(suffix, ratio) {
    operator fun div(other: Angle) = ratio / other.ratio

    companion object {
        /** 2π * radians is the circumference of a circle */
        val radians = Angle("rad")

        /** 360 * degrees is the circumference of a circle */
        val degrees = object: Angle("°", PI / 180) { override val spaceBetweenMagnitude = false }

        inline fun sin  (angle : Measure<Angle>        ) = kotlin.math.sin  (angle  `in` radians)
        inline fun cos  (angle : Measure<Angle>        ) = kotlin.math.cos  (angle  `in` radians)
        inline fun tan  (angle : Measure<Angle>        ) = kotlin.math.tan  (angle  `in` radians)
        inline fun asin (value : Double                ) = kotlin.math.asin (value              ) * radians
        inline fun acos (value : Double                ) = kotlin.math.acos (value              ) * radians
        inline fun atan (value : Double                ) = kotlin.math.atan (value              ) * radians
        inline fun atan2(value1: Double, value2: Double) = kotlin.math.atan2(value1, value2     ) * radians
        inline fun sinh (angle : Measure<Angle>        ) = kotlin.math.sinh (angle  `in` radians)
        inline fun cosh (angle : Measure<Angle>        ) = kotlin.math.cosh (angle  `in` radians)
        inline fun tanh (angle : Measure<Angle>        ) = kotlin.math.tanh (angle  `in` radians)
        inline fun asinh(value : Double                ) = kotlin.math.asinh(value              ) * radians
        inline fun acosh(value : Double                ) = kotlin.math.acosh(value              ) * radians
        inline fun atanh(value : Double                ) = kotlin.math.atanh(value              ) * radians
    }
}

/**
 * @returns a measure that is within [0°, 360°)
 */
fun Measure<Angle>.normalize(): Measure<Angle> {
    var result = (this `in` Angle.degrees) % 360

    if (result < 0) {
        result += 360
    }

    return result * Angle.degrees
}