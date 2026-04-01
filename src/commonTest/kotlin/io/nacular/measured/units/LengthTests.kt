package io.nacular.measured.units

import io.nacular.measured.units.GraphicsLength.Companion.pixels
import io.nacular.measured.units.Length.Companion.centimeters
import io.nacular.measured.units.Length.Companion.feet
import io.nacular.measured.units.Length.Companion.inches
import io.nacular.measured.units.Length.Companion.kilometers
import io.nacular.measured.units.Length.Companion.meters
import io.nacular.measured.units.Length.Companion.miles
import io.nacular.measured.units.Length.Companion.millimeters
import io.nacular.measured.units.Time.Companion.hours
import io.nacular.measured.units.Time.Companion.seconds
import io.nacular.measured.units.times
import kotlin.js.JsName
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.expect
import kotlin.test.assertTrue

class LengthTests {
    // ─────────────────────────────────────────────────────────────────────────
    // Suffixes
    // ─────────────────────────────────────────────────────────────────────────

    @Test @JsName("milesSuffixIsMi")
    fun `miles suffix is mi`() = assertEquals("mi", miles.suffix)

    @Test @JsName("millimetersSuffixIsMm")
    fun `millimeters suffix is mm`() = assertEquals("mm", millimeters.suffix)

    @Test @JsName("centimetersSuffixIsCm")
    fun `centimeters suffix is cm`() = assertEquals("cm", centimeters.suffix)

    @Test @JsName("metersSuffixIsM")
    fun `meters suffix is m`() = assertEquals("m", meters.suffix)

    @Test @JsName("inchesSuffixIsIn")
    fun `inches suffix is in`() = assertEquals("in", inches.suffix)

    @Test @JsName("feetSuffixIsFt")
    fun `feet suffix is ft`() = assertEquals("ft", feet.suffix)

    @Test @JsName("kilometersSuffixIsKm")
    fun `kilometers suffix is km`() = assertEquals("km", kilometers.suffix)

    // ─────────────────────────────────────────────────────────────────────────
    // Base ratios (all stored in meters)
    // ─────────────────────────────────────────────────────────────────────────

    @Test @JsName("metersRatioIsOne")
    fun `meters ratio is 1`() = assertApprox(1.0, meters.ratio)

    @Test @JsName("kilometersRatioIs1000")
    fun `kilometers ratio is 1000 meters`() = assertApprox(1_000.0, kilometers.ratio)

    @Test @JsName("centimetersRatioIsPointZeroOne")
    fun `centimeters ratio is 0 point 01 meters`() = assertApprox(0.01, centimeters.ratio)

    @Test @JsName("millimetersRatioIsPointZeroZeroOne")
    fun `millimeters ratio is 0 point 001 meters`() = assertApprox(0.001, millimeters.ratio)

    @Test @JsName("inchesRatioIs0point0254")
    fun `inches ratio is 0 point 0254 meters`() = assertApprox(0.0254, inches.ratio)

    @Test @JsName("feetRatioIs12Inches")
    fun `feet ratio is 12 inches in meters`() = assertApprox(12 * inches.ratio, feet.ratio)

    @Test @JsName("milesRatioIs1609point344")
    fun `miles ratio is 1609 point 344 meters`() = assertApprox(1609.344, miles.ratio)

    // ─────────────────────────────────────────────────────────────────────────
    // div operator – unit-to-unit ratio
    // ─────────────────────────────────────────────────────────────────────────

    @Test @JsName("kilometersDivMetersIs1000")
    fun `kilometers div meters equals 1000`() = assertApprox(1_000.0, kilometers / meters)

    @Test @JsName("metersDivKilometersIsReciprocal")
    fun `meters div kilometers equals 1 over 1000`() = assertApprox(0.001, meters / kilometers)

    @Test @JsName("metersDivCentimetersIs100")
    fun `meters div centimeters equals 100`() = assertApprox(100.0, meters / centimeters)

    @Test @JsName("metersDivMillimetersIs1000")
    fun `meters div millimeters equals 1000`() = assertApprox(1_000.0, meters / millimeters)

    @Test @JsName("centimetersDivMillimetersIs10")
    fun `centimeters div millimeters equals 10`() = assertApprox(10.0, centimeters / millimeters)

    @Test @JsName("feetDivInchesIs12")
    fun `feet div inches equals 12`() = assertApprox(12.0, feet / inches)

    @Test @JsName("inchesDivFeetIsOneOver12")
    fun `inches div feet equals 1 over 12`() = assertApprox(1.0 / 12.0, inches / feet)

    @Test @JsName("selfDivIsAlwaysOne")
    fun `any unit divided by itself is 1`() {
        assertApprox(1.0, meters       / meters,       "m / m"  )
        assertApprox(1.0, kilometers   / kilometers,   "km / km")
        assertApprox(1.0, centimeters  / centimeters,  "cm / cm")
        assertApprox(1.0, millimeters  / millimeters,  "mm / mm")
        assertApprox(1.0, inches       / inches,       "in / in")
        assertApprox(1.0, feet         / feet,         "ft / ft")
        assertApprox(1.0, miles        / miles,        "mi / mi")
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Measure creation – numeric type overloads
    // ─────────────────────────────────────────────────────────────────────────

    @Test @JsName("metersConstantsAllTypes")
    fun `meters measure can be created from Int or Double or Float and Long`() {
        expect(10   * meters) { 10   * meters }
        expect(10.0 * meters) { 10.0 * meters }
        expect(10f  * meters) { 10f  * meters }
        expect(10L  * meters) { 10L  * meters }
    }

    @Test @JsName("kilometersConstantsAllTypes")
    fun `kilometers measure can be created from Int or Double or Float and Long`() {
        expect(5   * kilometers) { 5   * kilometers }
        expect(5.0 * kilometers) { 5.0 * kilometers }
        expect(5f  * kilometers) { 5f  * kilometers }
        expect(5L  * kilometers) { 5L  * kilometers }
    }

    @Test @JsName("inchesConstantsAllTypes")
    fun `inches measure can be created from Int or Double or Float and Long`() {
        expect(3   * inches) { 3   * inches }
        expect(3.0 * inches) { 3.0 * inches }
        expect(3f  * inches) { 3f  * inches }
        expect(3L  * inches) { 3L  * inches }
    }

    @Test @JsName("feetConstantsAllTypes")
    fun `feet measure can be created from Int or Double or Float and Long`() {
        expect(6   * feet) { 6   * feet }
        expect(6.0 * feet) { 6.0 * feet }
        expect(6f  * feet) { 6f  * feet }
        expect(6L  * feet) { 6L  * feet }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Measure conversions using `in`
    // ─────────────────────────────────────────────────────────────────────────

    @Test @JsName("oneKilometerInMetersIs1000")
    fun `1 kilometer in meters is 1000`() = assertApprox(1_000.0, (1 * kilometers) `in` meters)

    @Test @JsName("oneMeterInCentimetersIs100")
    fun `1 meter in centimeters is 100`() = assertApprox(100.0, (1 * meters) `in` centimeters)

    @Test @JsName("oneMeterInMillimetersIs1000")
    fun `1 meter in millimeters is 1000`() = assertApprox(1_000.0, (1 * meters) `in` millimeters)

    @Test @JsName("oneCentimeterInMillimetersIs10")
    fun `1 centimeter in millimeters is 10`() = assertApprox(10.0, (1 * centimeters) `in` millimeters)

    @Test @JsName("oneFeetInInchesIs12")
    fun `1 foot in inches is 12`() = assertApprox(12.0, (1 * feet) `in` inches)

    @Test @JsName("oneInchInFeetIsOneOver12")
    fun `1 inch in feet is 1 over 12`() = assertApprox(1.0 / 12.0, (1 * inches) `in` feet)

    @Test @JsName("oneMileInMetersIs1609point344")
    fun `1 mile in meters is 1609 point 344`() = assertApprox(1_609.344, (1 * miles) `in` meters)

    @Test @JsName("oneMileInKilometersIs1point609344")
    fun `1 mile in kilometers is 1 point 609344`() = assertApprox(1.609344, (1 * miles) `in` kilometers)

    @Test @JsName("oneFootInMetersIs0point3048")
    fun `1 foot in meters is 0 point 3048`() = assertApprox(0.3048, (1 * feet) `in` meters)

    @Test @JsName("oneInchInMetersIs0point0254")
    fun `1 inch in meters is 0 point 0254`() = assertApprox(0.0254, (1 * inches) `in` meters)

    @Test @JsName("roundTripMetersToMilesAndBack")
    fun `round-trip meters to miles and back`() {
        val original = 5_000.0
        val inMiles  = (original * meters) `in` miles
        val backToMeters = (inMiles * miles) `in` meters
        assertApprox(original, backToMeters, "round-trip m -> mi -> m")
    }

    @Test @JsName("roundTripKilometersToFeetAndBack")
    fun `round-trip kilometers to feet and back`() {
        val original = 2.0
        val inFeet   = (original * kilometers) `in` feet
        val backToKm = (inFeet * feet) `in` kilometers
        assertApprox(original, backToKm, "round-trip km -> ft -> km")
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Combinations matrix
    // ─────────────────────────────────────────────────────────────────────────

    @Test @JsName("combinationsWork")
    fun `combinations work`() {
        testUnit(
            mapOf(
                meters      to mapOf(1.0     to meters, 0.001    to kilometers, 100.0     to centimeters, 1_000.0     to millimeters),
                kilometers  to mapOf(1_000.0 to meters, 1.0      to kilometers, 100_000.0 to centimeters, 1_000_000.0 to millimeters),
                centimeters to mapOf(0.01    to meters, 0.00001  to kilometers, 1.0       to centimeters, 10.0        to millimeters),
                millimeters to mapOf(0.001   to meters, 0.000001 to kilometers, 0.1       to centimeters, 1.0         to millimeters)
            )
        )
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Arithmetic on Measure<Length>
    // ─────────────────────────────────────────────────────────────────────────

    @Test @JsName("addTwoMeterMeasures")
    fun `adding two meter measures gives correct sum`() = assertApprox(10.0, 3 * meters + 7 * meters `in` meters)

    @Test @JsName("addMetersAndCentimeters")
    fun `adding meters and centimeters gives correct sum in centimeters`() = assertApprox(150.0, 1 * meters + 50 * centimeters `in` centimeters)

    @Test @JsName("addKilometersAndMeters")
    fun `adding kilometers and meters gives correct sum in meters`() = assertApprox(1_500.0, 1 * kilometers + 500 * meters `in` meters)

    @Test @JsName("subtractCentimetersFromMeter")
    fun `subtracting centimeters from a meter gives correct result`() = assertApprox(75.0, 1 * meters - 25 * centimeters `in` centimeters)

    @Test @JsName("scalarMultiplicationScalesMeasure")
    fun `scalar multiplication scales a measure correctly`() = assertApprox(1_000.0, 4 * (250 * meters) `in` meters)

    @Test @JsName("negativeScalarNegatesMeasure")
    fun `multiplying by a negative scalar negates the measure`() = assertApprox(-5.0, -1 * (5 * kilometers) `in` kilometers)

    @Test @JsName("additionIsCommutative")
    fun `measure addition is commutative`() {
        val a = (500 * meters + 1 * kilometers) `in` meters
        val b = (1 * kilometers + 500 * meters) `in` meters
        assertApprox(a, b)
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Comparison of Measure<Length>
    // ─────────────────────────────────────────────────────────────────────────

    @Test @JsName("oneKilometerGreaterThan999Meters")
    fun `1 kilometer is greater than 999 meters`() = assertTrue((1 * kilometers) > (999 * meters))

    @Test @JsName("_1KilometerEqualsThousandMeters")
    fun `1 kilometer equals 1000 meters`() = assertEquals(1 * kilometers, 1_000 * meters)

    @Test @JsName("_1FootGreaterThan11Inches")
    fun `1 foot is greater than 11 inches`() = assertTrue((1 * feet) > (11 * inches))

    @Test @JsName("_12InchesEqualsOneFoot")
    fun `12 inches equals 1 foot`() = assertApprox(12 * inches, 1 * feet)

    @Test @JsName("oneMileGreaterThan1609Meters")
    fun `1 mile is greater than 1609 meters`() = assertTrue((1 * miles) > (1_609 * meters))

    @Test @JsName("_1MeterNotGreaterThanItself")
    fun `1 meter is not greater than itself`() = assertTrue(!((1 * meters) > (1 * meters)))

    @Test @JsName("_1MeterNotLessThanItself")
    fun `1 meter is not less than itself`() = assertTrue(!((1 * meters) < (1 * meters)))

    // ─────────────────────────────────────────────────────────────────────────
    // Distance typealias
    // ─────────────────────────────────────────────────────────────────────────

    @Test @JsName("distanceAliasIsInterchangeableWithLength")
    fun `Distance typealias is interchangeable with Length`() = assertApprox(100.0, 100 * meters `in` meters)

    // ─────────────────────────────────────────────────────────────────────────
    // Time * Length extension (sorting operator)
    // ─────────────────────────────────────────────────────────────────────────

    @Test @JsName("timesLengthExtensionIsCommutative")
    fun `Time times Length gives the same result as Length times Time`() {
        val byLength = meters  * seconds
        val byTime   = seconds * meters
        assertEquals(byLength, byTime)
    }

    @Test @JsName("measureTimeTimesLengthIsCommutative")
    fun `Measure of Time times Length gives the same result as Length times Measure of Time`() {
        val speed1 = 10 * seconds * meters
        val speed2 = 1  * meters  * 10 * seconds

        assertEquals(speed1, speed2)
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Edge cases
    // ─────────────────────────────────────────────────────────────────────────

    @Test @JsName("zeroMetersInKilometersIsZero")
    fun `0 meters in kilometers is 0`() = assertApprox(0.0, (0 * meters) `in` kilometers)

    @Test @JsName("fractionalKilometersConvertCorrectly")
    fun `fractional kilometers convert to correct meters`() = assertApprox(1_500.0, (1.5 * kilometers) `in` meters)

    @Test @JsName("fractionalInchesConvertCorrectly")
    fun `fractional inches convert to correct feet`() = assertApprox(0.5, (6.0 * inches) `in` feet)

    @Test @JsName("verySmallMillimetersConvertToMeters")
    fun `1 millimeter in meters is 0 point 001`() = assertApprox(0.001, (1 * millimeters) `in` meters)

    @Test @JsName("marathonDistanceInKilometers")
    fun `marathon distance of 42195 meters is approximately 42 point 195 kilometers`() = assertApprox(42.195, (42_195 * meters) `in` kilometers)
}