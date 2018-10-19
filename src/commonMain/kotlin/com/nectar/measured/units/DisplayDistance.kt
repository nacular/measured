package com.nectar.measured.units

/**
 * Created by Nicholas Eddy on 3/30/18.
 */

interface DisplayDistance

val pixels = Unit<DisplayDistance>(" px")

val Int.   pixels: Measure<DisplayDistance> get() = this * com.nectar.measured.units.pixels
val Float. pixels: Measure<DisplayDistance> get() = this * com.nectar.measured.units.pixels
val Long.  pixels: Measure<DisplayDistance> get() = this * com.nectar.measured.units.pixels
val Double.pixels: Measure<DisplayDistance> get() = this * com.nectar.measured.units.pixels