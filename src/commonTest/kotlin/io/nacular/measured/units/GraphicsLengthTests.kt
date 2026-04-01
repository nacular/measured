package io.nacular.measured.units

import io.nacular.measured.units.GraphicsLength.Companion.pixels
import kotlin.js.JsName
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.expect
import kotlin.test.assertTrue

class GraphicsLengthTests {
    // ─────────────────────────────────────────────────────────────────────────
    // Suffix
    // ─────────────────────────────────────────────────────────────────────────

    @Test @JsName("pixelsSuffixIsPx")
    fun `pixels suffix is px`() = assertEquals("px", pixels.suffix)

    // ─────────────────────────────────────────────────────────────────────────
    // Base ratio
    // ─────────────────────────────────────────────────────────────────────────

    @Test @JsName("pixelsRatioIsOne")
    fun `pixels ratio is 1`() = assertApprox(1.0, pixels.ratio)

    // ─────────────────────────────────────────────────────────────────────────
    // div operator
    // ─────────────────────────────────────────────────────────────────────────

    @Test @JsName("pixelsDivItselfIsOne")
    fun `pixels div itself is 1`() = assertApprox(1.0, pixels / pixels)

    // ─────────────────────────────────────────────────────────────────────────
    // Measure creation – numeric type overloads
    // ─────────────────────────────────────────────────────────────────────────

    @Test @JsName("pixelsConstantsAllTypes")
    fun `pixels measure can be created from Int or Double or Float and Long`() {
        expect (10  * pixels) { 10   * pixels }
        expect(10.0 * pixels) { 10.0 * pixels }
        expect(10f  * pixels) { 10f  * pixels }
        expect(10L  * pixels) { 10L  * pixels }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Measure conversions using `in`
    // ─────────────────────────────────────────────────────────────────────────

    @Test @JsName("tenPixelsInPixelsIsTen")
    fun `10 pixels in pixels is 10`() = assertApprox(10.0, (10 * pixels) `in` pixels)

    @Test @JsName("zeroPixelsInPixelsIsZero")
    fun `0 pixels in pixels is 0`() = assertApprox(0.0, (0 * pixels) `in` pixels)

    @Test @JsName("fractionalPixelsConvertCorrectly")
    fun `fractional pixels convert correctly`() = assertApprox(2.5, (2.5 * pixels) `in` pixels)

    @Test @JsName("largePixelCountConvertsCorrectly")
    fun `large pixel count converts correctly`() = assertApprox(1_920.0, (1_920 * pixels) `in` pixels)

    // ─────────────────────────────────────────────────────────────────────────
    // Arithmetic on Measure<GraphicsLength>
    // ─────────────────────────────────────────────────────────────────────────

    @Test @JsName("addTwoPixelMeasures")
    fun `adding two pixel measures gives correct sum`() = assertApprox(1_080.0, (720 * pixels + 360 * pixels) `in` pixels)

    @Test @JsName("subtractPixelMeasures")
    fun `subtracting pixel measures gives correct difference`() = assertApprox(80.0, (100 * pixels - 20 * pixels) `in` pixels)

    @Test @JsName("scalarMultiplicationScalesMeasure")
    fun `scalar multiplication scales a measure correctly`() = assertApprox(300.0, (3 * (100 * pixels)) `in` pixels)

    @Test @JsName("negativeScalarNegatesMeasure")
    fun `multiplying by a negative scalar negates the measure`() = assertApprox(-50.0, (-1 * (50 * pixels)) `in` pixels)

    @Test @JsName("additionIsCommutative")
    fun `measure addition is commutative`() = assertApprox((640 * pixels + 480 * pixels) `in` pixels, (480 * pixels + 640 * pixels) `in` pixels)

    // ─────────────────────────────────────────────────────────────────────────
    // Comparison of Measure<GraphicsLength>
    // ─────────────────────────────────────────────────────────────────────────

    @Test @JsName("_1920PixelsGreaterThan1080Pixels")
    fun `1920 pixels is greater than 1080 pixels`() = assertTrue((1_920 * pixels) > (1_080 * pixels))

    @Test @JsName("_100PixelsEquals100Pixels")
    fun `100 pixels equals 100 pixels`() = assertEquals(100 * pixels, 100 * pixels)

    @Test @JsName("_1PixelNotGreaterThanItself")
    fun `1 pixel is not greater than itself`() = assertTrue(!((1 * pixels) > (1 * pixels)))

    @Test @JsName("_1PixelNotLessThanItself")
    fun `1 pixel is not less than itself`() = assertTrue(!((1 * pixels) < (1 * pixels)))

    // ─────────────────────────────────────────────────────────────────────────
    // Custom GraphicsLength subunit
    // ─────────────────────────────────────────────────────────────────────────

    @Test @JsName("customSubunitDivPixelsIsCorrect")
    fun `custom half-pixel subunit div pixels is 0 point 5`() = assertApprox(0.5, GraphicsLength("hpx", 0.5) / pixels)

    @Test @JsName("customSuperunitDivPixelsIsCorrect")
    fun `custom double-pixel superunit div pixels is 2`() = assertApprox(2.0, GraphicsLength("dpx", 2.0) / pixels)

    @Test @JsName("pixelsDivCustomSubunitIsCorrect")
    fun `pixels div custom half-pixel subunit is 2`() = assertApprox(2.0, pixels / GraphicsLength("hpx", 0.5))

    @Test @JsName("customSubunitInPixelsIsCorrect")
    fun `10 of a custom double-pixel unit in pixels is 20`() = assertApprox(20.0, (10 * GraphicsLength("dpx", 2.0)) `in` pixels)

    // ─────────────────────────────────────────────────────────────────────────
    // Combinations matrix
    // ─────────────────────────────────────────────────────────────────────────

    @Test @JsName("combinationsWork")
    fun `combinations work`() {
        testUnit(mapOf(pixels to mapOf(1.0 to pixels)))
    }
}