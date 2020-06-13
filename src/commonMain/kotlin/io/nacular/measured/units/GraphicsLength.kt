package io.nacular.measured.units

/**
 * Unit for measuring graphics display distances (i.e. distances on the surface of a display).
 */
open class GraphicsLength(suffix: String, ratio: Double = 1.0): Unit(suffix, ratio) {
    operator fun div(other: GraphicsLength) = ratio / other.ratio

    companion object {
        /** 1 * pixels is the size of a single pixel on a display. */
        val pixels = GraphicsLength("px")
    }
}