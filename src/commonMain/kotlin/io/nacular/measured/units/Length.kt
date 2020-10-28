package io.nacular.measured.units

/**
 * Units for length or distance.
 */
open class Length(suffix: String, ratio: Double = 1.0): Units(suffix, ratio) {
    operator fun div(other: Length) = ratio / other.ratio

    companion object {
        val miles       = Length("mi", 1609.3440)
        val millimeters = Length("mm",    0.0010)
        val centimeters = Length("cm",    0.0100)
        val meters      = Length("m"            )
        val inches      = Length("in",    0.0254)
        val feet        = Length("ft", 12 * inches `in` meters)
        val kilometers  = Length("km", 1000.0000)
    }
}

/**
 * Sort Length before Time which is conventional.
 */
operator fun Time.times(other: Length) = other * this
operator fun Measure<Time>.times(other: Length) = amount * other * units

typealias Distance = Length