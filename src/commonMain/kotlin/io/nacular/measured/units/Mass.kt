package io.nacular.measured.units

/**
 * Units to measure how much matter is in an object.
 */
open class Mass(suffix: String, ratio: Double = 1.0): Units(suffix, ratio) {
    operator fun div(other: Mass) = ratio / other.ratio

    companion object {
        val kilograms = Mass("kg")
        val grams     = Mass("g", 1.0 / 1000)
    }
}

operator fun Length.times(mass: Mass) = mass * this
operator fun Measure<Length>.times(mass: Mass) = amount * (units * mass)