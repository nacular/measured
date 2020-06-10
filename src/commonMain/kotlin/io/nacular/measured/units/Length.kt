package io.nacular.measured.units

/**
 * Created by Nicholas Eddy on 10/18/18.
 */

open class Length(suffix: String, ratio: Double = 1.0): Unit(suffix, ratio) {
    operator fun div(other: Length) = ratio / other.ratio

    companion object {
        val miles       = Length("mi", 1609.34)
        val millimeters = Length("mm", 0.01)
        val centimeters = Length("cm", 0.10)
        val meters      = Length("m")
        val kilometers  = Length("km", 1000.00)

    }
}

typealias Distance = Length