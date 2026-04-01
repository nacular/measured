package io.nacular.measured.units

import io.nacular.measured.units.Length.Companion.meters
import io.nacular.measured.units.Mass.Companion.grams
import io.nacular.measured.units.Mass.Companion.kilograms
import kotlin.js.JsName
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.expect
import kotlin.test.assertTrue

class MassTests {
    // ─────────────────────────────────────────────────────────────────────────
    // Suffixes
    // ─────────────────────────────────────────────────────────────────────────

    @Test @JsName("kilogramsSuffixIsKg")
    fun `kilograms suffix is kg`() = assertEquals("kg", kilograms.suffix)

    @Test @JsName("gramsSuffixIsG")
    fun `grams suffix is g`() = assertEquals("g", grams.suffix)

    // ─────────────────────────────────────────────────────────────────────────
    // Base ratios (base unit: kilograms, ratio = 1.0)
    // ─────────────────────────────────────────────────────────────────────────

    @Test @JsName("kilogramsRatioIsOne")
    fun `kilograms ratio is 1`() = assertApprox(1.0, kilograms.ratio)

    @Test @JsName("gramsRatioIsOneOverThousand")
    fun `grams ratio is 1 over 1000`() = assertApprox(1.0 / 1_000.0, grams.ratio)

    // ─────────────────────────────────────────────────────────────────────────
    // div operator
    // ─────────────────────────────────────────────────────────────────────────

    @Test @JsName("kilogramsDivGramsIs1000")
    fun `kilograms div grams equals 1000`() = assertApprox(1_000.0, kilograms / grams)

    @Test @JsName("gramsDivKilogramsIsOneOverThousand")
    fun `grams div kilograms equals 1 over 1000`() = assertApprox(1.0 / 1_000.0, grams / kilograms)

    @Test @JsName("kilogramsDivItselfIsOne")
    fun `kilograms div itself is 1`() = assertApprox(1.0, kilograms / kilograms)

    @Test @JsName("gramsDivItselfIsOne")
    fun `grams div itself is 1`() = assertApprox(1.0, grams / grams)

    // ─────────────────────────────────────────────────────────────────────────
    // Measure creation – numeric type overloads
    // ─────────────────────────────────────────────────────────────────────────

    @Test @JsName("kilogramsConstantsAllTypes")
    fun `kilograms measure can be created from Int or Double or Float and Long`() {
        expect(10   * kilograms) { 10   * kilograms }
        expect(10.0 * kilograms) { 10.0 * kilograms }
        expect(10f  * kilograms) { 10f  * kilograms }
        expect(10L  * kilograms) { 10L  * kilograms }
    }

    @Test @JsName("gramsConstantsAllTypes")
    fun `grams measure can be created from Int or Double or Float and Long`() {
        expect(10   * grams) { 10   * grams }
        expect(10.0 * grams) { 10.0 * grams }
        expect(10f  * grams) { 10f  * grams }
        expect(10L  * grams) { 10L  * grams }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Measure conversions using `in`
    // ─────────────────────────────────────────────────────────────────────────

    @Test @JsName("oneKilogramInGramsIs1000")
    fun `1 kilogram in grams is 1000`() = assertApprox(1_000.0, (1 * kilograms) `in` grams)

    @Test @JsName("_1000GramsInKilogramsIsOne")
    fun `1000 grams in kilograms is 1`() = assertApprox(1.0, (1_000 * grams) `in` kilograms)

    @Test @JsName("halfKilogramInGramsIs500")
    fun `0 point 5 kilograms in grams is 500`() = assertApprox(500.0, (0.5 * kilograms) `in` grams)

    @Test @JsName("_250GramsInKilogramsIs0point25")
    fun `250 grams in kilograms is 0 point 25`() = assertApprox(0.25, (250 * grams) `in` kilograms)

    @Test @JsName("zeroKilogramsInGramsIsZero")
    fun `0 kilograms in grams is 0`() = assertApprox(0.0, (0 * kilograms) `in` grams)

    @Test @JsName("roundTripGramsToKilogramsAndBack")
    fun `round-trip grams to kilograms and back`() = assertApprox(750.0, ((750 * grams) `in` kilograms) * 1_000.0, "round-trip g -> kg -> g")

    // ─────────────────────────────────────────────────────────────────────────
    // Combinations matrix
    // ─────────────────────────────────────────────────────────────────────────

    @Test @JsName("combinationsWork")
    fun `combinations work`() {
        testUnit(
            mapOf(
                kilograms to mapOf(1.0         to kilograms, 1_000.0 to grams),
                grams     to mapOf(1.0 / 1_000 to kilograms, 1.0     to grams)
            )
        )
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Arithmetic on Measure<Mass>
    // ─────────────────────────────────────────────────────────────────────────

    @Test @JsName("addTwoKilogramMeasures")
    fun `adding two kilogram measures gives correct sum`() = assertApprox(5.0, (2 * kilograms + 3 * kilograms) `in` kilograms)

    @Test @JsName("addKilogramsAndGrams")
    fun `adding kilograms and grams gives correct sum in grams`() = assertApprox(1_500.0, (1 * kilograms + 500 * grams) `in` grams)

    @Test @JsName("subtractGramsFromKilogram")
    fun `subtracting grams from a kilogram gives correct result`() = assertApprox(750.0, (1 * kilograms - 250 * grams) `in` grams)

    @Test @JsName("scalarMultiplicationScalesMeasure")
    fun `scalar multiplication scales a measure correctly`() = assertApprox(6.0, (3 * (2 * kilograms)) `in` kilograms)

    @Test @JsName("additionIsCommutative")
    fun `measure addition is commutative`() = assertApprox(
        (500 * grams + 1 * kilograms) `in` grams,
        (1 * kilograms + 500 * grams) `in` grams
    )

    // ─────────────────────────────────────────────────────────────────────────
    // Comparison of Measure<Mass>
    // ─────────────────────────────────────────────────────────────────────────

    @Test @JsName("oneKilogramGreaterThan999Grams")
    fun `1 kilogram is greater than 999 grams`() = assertTrue((1 * kilograms) > (999 * grams))

    @Test @JsName("oneKilogramEqualsThousandGrams")
    fun `1 kilogram equals 1000 grams`() = assertEquals(1 * kilograms, 1_000 * grams)

    @Test @JsName("oneKilogramNotGreaterThan1000Grams")
    fun `1 kilogram is not greater than 1000 grams`() = assertTrue(!((1 * kilograms) > (1_000 * grams)))

    @Test @JsName("oneKilogramNotLessThan1000Grams")
    fun `1 kilogram is not less than 1000 grams`() = assertTrue(!((1 * kilograms) < (1_000 * grams)))

    // ─────────────────────────────────────────────────────────────────────────
    // Length * Mass extension (commutativity operator)
    // ─────────────────────────────────────────────────────────────────────────

    @Test @JsName("lengthTimessMassEqualssMassTimesLength")
    fun `Length times Mass gives the same result as Mass times Length`() = assertEquals(meters * kilograms, kilograms * meters)

    @Test @JsName("measureLengthTimesMassEqualsMassTimesLength")
    fun `Measure of Length times Mass gives same result as Mass times Measure of Length`() = assertEquals(10 * meters * kilograms, 1 * kilograms * 10 * meters)
}