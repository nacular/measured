package com.nectar.measured.units

/**
 * Created by Nicholas Eddy on 3/30/18.
 */

open class GraphicsLength(suffix: String, ratio: Double = 1.0): Unit(suffix, ratio) {
    operator fun div(other: Length) = ratio / other.ratio
}

val pixels = GraphicsLength("px")
