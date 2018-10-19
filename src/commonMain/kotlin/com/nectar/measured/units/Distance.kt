package com.nectar.measured.units

/**
 * Created by Nicholas Eddy on 10/18/18.
 */

interface Distance

val meters = Unit<Distance>(" m")

val Int.   meters: Measure<Distance> get() = this * com.nectar.measured.units.meters
val Float. meters: Measure<Distance> get() = this * com.nectar.measured.units.meters
val Long.  meters: Measure<Distance> get() = this * com.nectar.measured.units.meters
val Double.meters: Measure<Distance> get() = this * com.nectar.measured.units.meters