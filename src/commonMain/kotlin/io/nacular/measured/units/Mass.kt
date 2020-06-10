package io.nacular.measured.units

/**
 * Created by Nicholas Eddy on 10/18/18.
 */

open class Mass(suffix: String, ratio: Double = 1.0): Unit(suffix, ratio) {
    operator fun div(other: Mass) = ratio / other.ratio

    companion object {
        val kilograms = Mass("kg")
        val grams     = Mass("g", 1.0 / 1000)
    }
}