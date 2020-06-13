package io.nacular.measured.units

/**
 * Units for length or distance.
 */
open class Length(suffix: String, ratio: Double = 1.0): Unit(suffix, ratio) {
    operator fun div(other: Length) = ratio / other.ratio

    companion object {
        val miles       = Length("mi", 1609.344)
        val millimeters = Length("mm",    0.010)
        val centimeters = Length("cm",    0.100)
        val meters      = Length("m"           )
        val kilometers  = Length("km", 1000.000)
    }
}

/**
 * Sort Length before Time which is conventional.
 */
operator fun Time.times(other: Length) = other * this
operator fun Measure<Time>.times(other: Length) = amount * other * unit

typealias Distance = Length