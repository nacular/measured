package io.nacular.measured.units

/**
 * Units to measure time durations.
 */
open class Time(suffix: String, ratio: Double = 1.0): Unit(suffix, ratio) {
    operator fun div(other: Time) = ratio / other.ratio

    companion object {
        val milliseconds = Time("ms"                     )
        val seconds      = Time("s",   1000.0            )
        val minutes      = Time("min", 60 * seconds.ratio)
        val hours        = Time("hr",  60 * minutes.ratio)
    }
}