@file:Suppress("FunctionName", "NonAsciiCharacters")

package io.nacular.measured.units

import kotlin.js.JsName
import kotlin.math.round
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.expect
import kotlin.test.assertTrue
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals

/**
 * Created by Nicholas Eddy on 2/18/18.
 */

private class UnitsA: Units {
    constructor(suffix: String               ): super(suffix       )
    constructor(suffix: String, ratio: Double): super(suffix, ratio)

    operator fun div(other: UnitsA) = ratio / other.ratio
}

private class UnitsB: Units {
    constructor(suffix: String               ): super(suffix       )
    constructor(suffix: String, ratio: Double): super(suffix, ratio)

    operator fun div(other: UnitsA) = ratio / other.ratio
}

// ─────────────────────────────────────────────────────────────────────────────
// Units
// ─────────────────────────────────────────────────────────────────────────────

class UnitTests {

    @Test @JsName("defaultRatioIs1")
    fun `default ratio is 1`() {
        val a = object: Units("a") {}

        expect(1.0, "$a.ratio") { a.ratio }
    }

    @Test @JsName("divWorks")
    fun `div works`() {
        val a = UnitsA("a", 10.0)
        val b = UnitsA("b",  1.0)

        expect(10.0, "$a / $b") { a / b }
    }

    @Test @JsName("toStringWorks")
    fun `toString works`() {
        val a = object: Units("description", 10.0) {}

        expect("description", "$a.toString()") { a.toString() }
    }

    @Test @JsName("comparisonsWork")
    fun `comparisons work`() {
        val a = UnitsA("a", 10.0)
        val b = UnitsA("b",  1.0)
        val c = UnitsA("a", 10.0)

        expect(true,  "$a > $b" ) { a  > b }
        expect(false, "$a < $b" ) { a  < b }
        expect(true,  "$a == $a") { a == a }
        expect(false, "$a == $b") { a == b }
        expect(true,  "$a == $c") { a == c }
    }

    @Test @JsName("suffixStoredCorrectly")
    fun `suffix is stored and returned correctly`() = expect("km") { object : Units("km", 1000.0) {}.toString() }

    @Test @JsName("ratioStoredCorrectly")
    fun `explicit ratio is stored correctly`() = expect(42.0) { UnitsA("x", 42.0).ratio }

    @Test @JsName("unitWithSameSuffixAndRatioAreEqual")
    fun `two units with same suffix and ratio are equal`() = expect(true) { UnitsA("m", 1.0) == UnitsA("m", 1.0) }

    @Test @JsName("unitWithSameRatioDifferentSuffixNotEqual")
    fun `two units with same ratio but different suffix are not equal`() = expect(false) { UnitsA("m", 1.0) == UnitsA("km", 1.0) }

    @Test @JsName("unitWithSameSuffixDifferentRatioNotEqual")
    fun `two units with same suffix but different ratio are not equal`() = expect(false) { UnitsA("m", 1.0) == UnitsA("m", 1000.0) }

    @Test @JsName("unitLessThanComparison")
    fun `smaller ratio unit is less than larger ratio unit`() = assertTrue(UnitsA("small", 1.0) < UnitsA("large", 100.0))

    @Test @JsName("unitGreaterThanOrEqualToItself")
    fun `unit compared to itself is not greater than`() = assertFalse(UnitsA("a", 5.0) > UnitsA("a", 5.0))
}

// ─────────────────────────────────────────────────────────────────────────────
// UnitsRatio
// ─────────────────────────────────────────────────────────────────────────────

class UnitRatioTests {
    @Test @JsName("defaultMultiplierIs1")
    fun `default ratio is 1`() {
        val a = UnitsRatio(UnitsA("a"), UnitsB("b"))

        expect(1.0, "$a.ratio") { a.ratio }
    }

    @Test @JsName("doubleReciprocal")
    fun `1 ÷﹙1 ÷ a﹚== a`() {
        val a = UnitsRatio(UnitsA("a"), UnitsB("b"))

        expect(a, "$a.reciprocal.reciprocal") { a.reciprocal.reciprocal }
    }

    @Test @Ignore @JsName("divAWorks")
    fun `﹙A÷B﹚÷ A = 1÷B`() {
        val a = UnitsRatio(UnitsA("a", 10.0), UnitsB("b"))
        val b = UnitsB("a", 10.0)

        expect<Units>(InverseUnits(UnitsB("b")), "$a / $b") { a / b }
    }

    @Test @JsName("timesBWorks")
    fun `﹙A÷B﹚＊ B = A`() {
        val a = UnitsRatio(UnitsA("a", 10.0), UnitsB("b"))
        val b = UnitsB("b", 1.0)

        expect(1 * UnitsA("a", 10.0), "$a * $b") { a * b }
    }

    @Test @JsName("timesInverseWorks")
    fun `﹙A÷B﹚＊﹙B÷A﹚= 1`() {
        val a = UnitsRatio(UnitsA("a", 10.0), UnitsB("b"))

        expect(1.0, "$a * 1/$a") { a * a.reciprocal }
    }

    @Test @JsName("divSelfWorks")
    fun `﹙A÷B﹚÷﹙A÷B﹚= 1`() {
        val a = UnitsRatio(UnitsA("a", 10.0), UnitsB("b"))

        expect(1.0, "$a / $a") { a / a }
    }

    @Test @JsName("toStringWorks")
    fun `toString works`() {
        val a     = UnitsA("a", 10.0)
        val b     = UnitsB("b"      )
        val ratio = UnitsRatio(a, b)

        ratio.let            { expect("$a/$b", "$it.toString()") { it.toString() } }
        ratio.reciprocal.let { expect("$b/$a", "$it.toString()") { it.toString() } }
    }

    @Test @JsName("comparisonsWork")
    fun `comparisons work`() {
        val a = UnitsRatio(UnitsA("a1", 10.0), UnitsB("b"))
        val b = UnitsRatio(UnitsA("a2",  1.0), UnitsB("b"))
        val c = UnitsRatio(UnitsA("a1", 10.0), UnitsB("b"))

        expect(true,  "$a > $b" ) { a  > b }
        expect(false, "$a < $b" ) { a  < b }
        expect(true,  "$a == $a") { a == a }
        expect(false, "$a == $b") { a == b }
        expect(true,  "$a == $c") { a == c }
    }

    @Test @JsName("ratioReflectsNumeratorOverDenominator")
    fun `ratio of A÷B equals numerator ratio divided by denominator ratio`() = expect(10.0) { UnitsRatio(UnitsA("a", 10.0), UnitsB("b", 1.0)).ratio }

    @Test @JsName("reciprocalRatioIsInverse")
    fun `reciprocal has the inverse ratio`() {
        val r = UnitsRatio(UnitsA("a", 10.0), UnitsB("b", 1.0))
        expect(0.1) { r.reciprocal.ratio }
    }

    @Test @JsName("ratioWithNonUnitDenominator")
    fun `ratio correctly divides non-unit denominator`() = expect(5.0) { UnitsRatio(UnitsA("a", 10.0), UnitsB("b", 2.0)).ratio }
}

// ─────────────────────────────────────────────────────────────────────────────
// UnitsProduct
// ─────────────────────────────────────────────────────────────────────────────

class UnitsProductTests {

    @Test @JsName("productRatioIsProductOfComponentRatios")
    fun `UnitsProduct ratio is the product of its two component ratios`() =
        expect(6.0) { (UnitsA("a", 2.0) * UnitsB("b", 3.0)).ratio }

    @Test @JsName("productSuffixConcatenatesComponents")
    fun `UnitsProduct suffix concatenates the two component suffixes`() =
        expect("ab") { (UnitsA("a") * UnitsB("b")).toString() }

    @Test @JsName("squareSuffixUsesExponentNotation")
    fun `Square suffix uses parenthesised exponent notation`() =
        expect("(a)^2") { (UnitsA("a") * UnitsA("a")).toString() }

    @Test @JsName("productStoresFirstAndSecondComponents")
    fun `UnitsProduct stores first and second components`() {
        val a = UnitsA("a", 2.0)
        val b = UnitsB("b", 3.0)
        val p = a * b
        expect(a) { p.first  }
        expect(b) { p.second }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// InverseUnits
// ─────────────────────────────────────────────────────────────────────────────

class InverseUnitsTests {

    @Test @JsName("inverseRatioIsReciprocalOfOriginal")
    fun `InverseUnits ratio is the reciprocal of the original unit ratio`() =
        expect(0.1) { InverseUnits(UnitsA("a", 10.0)).ratio }

    @Test @JsName("inverseSuffixIs1Over")
    fun `InverseUnits suffix is 1 slash original suffix`() =
        expect("1/hz") { InverseUnits(UnitsA("hz")).toString() }

    @Test @JsName("inverseUnitReferenceStored")
    fun `InverseUnits stores a reference to the original unit`() {
        val a = UnitsA("a", 5.0)
        expect(a) { InverseUnits(a).unit }
    }

    @Test @JsName("numberDivUnitProducesInverseUnit")
    fun `Number div Unit produces a Measure of InverseUnits`() =
        expect(Measure(2.0, InverseUnits(UnitsA("a")))) { 2 / UnitsA("a") }

    @Test @JsName("inverseTimesOriginalScalarIsRatioProduct")
    fun `InverseUnits times original unit gives scalar Double`() =
        expect(1.0) { InverseUnits(UnitsA("a", 1.0)) * UnitsA("a", 1.0) }

    @Test @JsName("unitTimesInverseOfItselfIs1")
    fun `Unit times InverseUnits of itself gives 1`() =
        expect(1.0) { UnitsA("a", 1.0) * InverseUnits(UnitsA("a", 1.0)) }
}

// ─────────────────────────────────────────────────────────────────────────────
// minOf
// ─────────────────────────────────────────────────────────────────────────────

class MinOfTests {

    @Test @JsName("minOfReturnsSmallerUnit")
    fun `minOf returns the unit with the smaller ratio`() = expect(UnitsA("small", 1.0)) { minOf(UnitsA("small", 1.0), UnitsA("large", 10.0)) }

    @Test @JsName("minOfReturnsSmallerUnitReversed")
    fun `minOf returns the smaller unit regardless of argument order`() = expect(UnitsA("small", 1.0)) { minOf(UnitsA("large", 10.0), UnitsA("small", 1.0)) }

    @Test @JsName("minOfEqualUnitsReturnsFirst")
    fun `minOf with equal ratios returns the first argument`() {
        val a = UnitsA("a", 5.0)
        val b = UnitsA("b", 5.0)
        expect(a) { minOf(a, b) }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Number extension operators
// ─────────────────────────────────────────────────────────────────────────────

class NumberExtensionTests {

    @Test @JsName("intTimesUnitCreatesMeasure")
    fun `Int times Unit creates a Measure with that amount`() =
        expect(Measure(5.0, UnitsA("a"))) { 5 * UnitsA("a") }

    @Test @JsName("doubleTimesUnitCreatesMeasure")
    fun `Double times Unit creates a Measure with that amount`() =
        expect(Measure(2.5, UnitsA("a"))) { 2.5 * UnitsA("a") }

    @Test @JsName("floatTimesUnitCreatesMeasure")
    fun `Float times Unit creates a Measure with correct amount`() =
        expect(Measure(3.0, UnitsA("a"))) { 3f * UnitsA("a") }

    @Test @JsName("longTimesUnitCreatesMeasure")
    fun `Long times Unit creates a Measure with correct amount`() =
        expect(Measure(7.0, UnitsA("a"))) { 7L * UnitsA("a") }

    @Test @JsName("intDivUnitCreatesMeasureOfInverseUnit")
    fun `Int div Unit creates a Measure of InverseUnits`() =
        expect(Measure(4.0, InverseUnits(UnitsA("a")))) { 4 / UnitsA("a") }

    @Test @JsName("numberTimesMeasureScalesMeasure")
    fun `Number times Measure scales the measure`() =
        expect(Measure(6.0, UnitsA("a"))) { 2 * Measure(3.0, UnitsA("a")) }

    @Test @JsName("numberDivMeasureProducesInverseMeasure")
    fun `Number div Measure produces Measure of InverseUnits`() =
        expect(Measure(2.0, InverseUnits(UnitsA("a")))) { 6 / Measure(3.0, UnitsA("a")) }

    @Test @JsName("numberDivInverseMeasureProducesForwardMeasure")
    fun `Number div Measure of InverseUnits produces forward Measure`() {
        val unit = UnitsA("a", 1.0)
        expect(Measure(3.0, unit)) { 6 / Measure(2.0, InverseUnits(unit)) }
    }

    @Test @JsName("unitTimesNumberCreatesMeasure")
    fun `Unit times Number creates a Measure`() =
        expect(Measure(9.0, UnitsA("a"))) { UnitsA("a") * 9 }

    @Test @JsName("unitInvokeCreatesFromNumber")
    fun `Unit invoke operator creates a Measure`() =
        expect(Measure(4.0, UnitsA("a"))) { UnitsA("a")(4) }
}

// ─────────────────────────────────────────────────────────────────────────────
// Units * Units operators
// ─────────────────────────────────────────────────────────────────────────────

class UnitsTimesUnitsTests {

    @Test @JsName("aTimesBABproduct")
    fun `A times B produces a UnitsProduct with ratio A x B`() = expect(6.0) { (UnitsA("a", 2.0) * UnitsB("b", 3.0)).ratio }

    @Test @JsName("aTimesInverseOfAIsScalar1")
    fun `A times InverseUnits of A gives scalar 1 when ratios match`() = expect(1.0) { UnitsA("a", 4.0) * InverseUnits(UnitsA("a", 4.0)) }

//    @Test @JsName("aTimesInverseOfBIsRatio")
//    fun `A times InverseUnits of B produces UnitsRatio A over B`() {
//        expect(UnitsRatio(UnitsA("a", 2.0), UnitsB("b", 1.0))) { UnitsA("a", 2.0) * InverseUnits(UnitsB("b", 1.0)) }
//    }

    @Test @JsName("ratioTimesBProducesAMeasure")
    fun `UnitsRatio A÷B times B produces Measure of A`() = expect(1 * UnitsA("a", 6.0)) { UnitsRatio(UnitsA("a", 6.0), UnitsB("b", 2.0)) * UnitsB("b", 2.0) }

    @Test @JsName("ratioTimesReciprocalIsOne")
    fun `UnitsRatio times its reciprocal is 1`() = expect(1.0) { UnitsRatio(UnitsA("a", 4.0), UnitsB("b", 2.0)).let { it * it.reciprocal } }

    @Test @JsName("inverseTimesOriginalIsScalar")
    fun `InverseUnits times original Unit is scalar`() = expect(1.0) { InverseUnits(UnitsA("a", 1.0)) * UnitsA("a", 1.0) }

    @Test @JsName("inverseTimesBIsRatioBOverA")
    fun `InverseUnits of A times B gives UnitsRatio B÷A`() = expect(UnitsRatio(UnitsB("b"), UnitsA("a"))) { InverseUnits(UnitsA("a")) * UnitsB("b") }
}

// ─────────────────────────────────────────────────────────────────────────────
// Units / Units operators
// ─────────────────────────────────────────────────────────────────────────────

class UnitsDivUnitsTests {

    @Test @JsName("aDivBIsUnitsRatio")
    fun `A div B produces UnitsRatio with ratio A-ratio over B-ratio`() = expect(2.0) { (UnitsA("a", 6.0) / UnitsB("b", 3.0)).ratio }

    @Test @JsName("aDivItsRatioReciprocal")
    fun `A div ﹙A÷B﹚ produces Measure of B`() {
        val a = UnitsA("a", 4.0)
        val b = UnitsB("b", 2.0)
        expect(Measure(1.0, b)) { a / UnitsRatio(a, b) }
    }

    @Test @JsName("productDivFirstComponentGivesSecond")
    fun `UnitsProduct﹙A＊B﹚ div A gives Measure of B`() {
        val a = UnitsA("a", 3.0)
        val b = UnitsB("b", 2.0)
        expect(Measure(1.0, b)) { (a * b) / a }
    }

    @Test @JsName("productDivSecondComponentGivesFirst")
    fun `UnitsProduct﹙A＊B﹚ div B gives Measure of A`() {
        val a = UnitsA("a", 3.0)
        val b = UnitsB("b", 2.0)
        expect(Measure(1.0, a)) { (a * b) / b }
    }

    @Test @JsName("ratioTimesReciprocalViaDiv")
    fun `UnitsRatio div itself is 1`() = expect(1.0) { UnitsRatio(UnitsA("a", 4.0), UnitsB("b", 2.0)).let { it / it } }

    @Test @JsName("inverseDivOriginalIsInverseSquare")
    fun `InverseUnits div Unit gives Measure of InverseUnits of Square`() {
        val a   = UnitsA("a", 1.0)
        val inv = InverseUnits(a)
        expect(1.0) { (inv / a).amount }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Measure * Measure and Measure / Measure operators
// ─────────────────────────────────────────────────────────────────────────────

class MeasureCompoundMathTests {

    @Test @JsName("measureTimesMeasureGivesProduct")
    fun `Measure A times Measure B gives Measure of UnitsProduct﹙A＊B﹚`() = expect(Measure(6.0, UnitsA("a") * UnitsB("b"))) { Measure(2.0, UnitsA("a")) * Measure(3.0, UnitsB("b")) }

    @Test @JsName("measureTimesMeasureRatioCollapses")
    fun `Measure A times Measure﹙B÷A﹚ gives Measure of B`() {
        val a  = UnitsA("a", 1.0)
        val b  = UnitsB("b", 1.0)
        expect(Measure(12.0, b)) { Measure(3.0, a) * Measure(4.0, UnitsRatio(b, a)) }
    }

    @Test @JsName("measureTimesInverseMeasureGivesScalar")
    fun `Measure A times Measure﹙1÷A﹚ gives scalar Double`() {
        val a = UnitsA("a", 1.0)
        expect(6.0) { Measure(2.0, a) * Measure(3.0, InverseUnits(a)) }
    }

    @Test @JsName("measureRatioTimesDenominatorCollapses")
    fun `Measure﹙A÷B﹚ times Measure B gives Measure of A`() {
        val a  = UnitsA("a", 1.0)
        val b  = UnitsB("b", 1.0)
        expect(Measure(10.0, a)) { Measure(5.0, UnitsRatio(a, b)) * Measure(2.0, b) }
    }

    @Test @JsName("measureDivSameUnitGivesScalar")
    fun `Measure A div Measure A gives scalar Double`() {
        val a = UnitsA("a", 1.0)
        expect(2.0) { Measure(6.0, a) / Measure(3.0, a) }
    }

    @Test @JsName("measureDivDifferentUnitGivesRatio")
    fun `Measure A div Measure B gives Measure of UnitsRatio﹙A÷B﹚`() {
        val a  = UnitsA("a", 1.0)
        val b  = UnitsB("b", 1.0)
        expect(Measure(2.0, a / b)) { Measure(6.0, a) / Measure(3.0, b) }
    }

    @Test @JsName("measureProductDivFirstGivesSecond")
    fun `Measure﹙A＊B﹚ div Measure A gives Measure B`() {
        val a  = UnitsA("a", 1.0)
        val b  = UnitsB("b", 1.0)
        expect(Measure(3.0, b)) { Measure(6.0, a * b) / Measure(2.0, a) }
    }

    @Test @JsName("measureDivRatioGivesDenominator")
    fun `Measure A div Measure﹙A÷B﹚ gives Measure B`() {
        val a  = UnitsA("a", 1.0)
        val b  = UnitsB("b", 1.0)
        expect(Measure(3.0, b)) { Measure(6.0, a) / Measure(2.0, UnitsRatio(a, b)) }
    }

    @Test @JsName("measureRatioDivNumeratorGivesInverseB")
    fun `Measure﹙A÷B﹚ div Measure A gives Measure of InverseUnits﹙B﹚`() {
        val a  = UnitsA("a", 1.0)
        val b  = UnitsB("b", 1.0)
        expect(Measure(3.0, InverseUnits(b))) { Measure(6.0, UnitsRatio(a, b)) / Measure(2.0, a) }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Measure * Units and Measure / Units operators
// ─────────────────────────────────────────────────────────────────────────────

class MeasureUnitsOperatorTests {

    @Test @JsName("measureTimesUnitGivesProduct")
    fun `Measure A times Unit B gives Measure of UnitsProduct﹙A＊B﹚`() {
        val a = UnitsA("a", 1.0)
        val b = UnitsB("b", 1.0)
        expect(Measure(3.0, a * b)) { 3 * a * b }
    }

    @Test @JsName("measureTimesRatioCollapsesDenominator")
    fun `Measure A times UnitsRatio﹙B÷A﹚ gives Measure of B`() {
        val a = UnitsA("a", 1.0)
        val b = UnitsB("b", 1.0)
        expect(3 * b) { 3 * a * b / a }
    }

    @Test @JsName("measureTimesInverseUnitGivesRatio")
    fun `Measure A times InverseUnits B gives Measure of UnitsRatio﹙A÷B﹚`() {
        val a = UnitsA("a", 1.0)
        val b = UnitsB("b", 1.0)
        expect(Measure(4.0, a / b)) { 4 * a / b }
    }

    @Test @JsName("measureTimesInverseOfItsOwnUnitGivesScalar")
    fun `Measure A times InverseUnits of A gives scalar Double`() {
        val a = UnitsA("a", 1.0)
        expect(5.0) { 5 * a / a }
    }

    @Test @JsName("measureRatioTimesDenominatorUnitCollapses")
    fun `Measure﹙A÷B﹚ times Unit B gives Measure of A`() {
        val a = UnitsA("a", 1.0)
        val b = UnitsB("b", 1.0)
        expect(Measure(7.0, a)) { Measure(7.0, UnitsRatio(a, b)) * b }
    }

    @Test @JsName("measureDivSameUnitGivesScalar")
    fun `Measure A div Unit A gives scalar Double`() {
        val a = UnitsA("a", 1.0)
        expect(4.0) { Measure(4.0, a) / a }
    }

    @Test @JsName("measureDivDifferentUnitGivesRatioMeasure")
    fun `Measure A div Unit B gives Measure of UnitsRatio﹙A÷B﹚`() {
        val a = UnitsA("a", 1.0)
        val b = UnitsB("b", 1.0)
        expect(Measure(3.0, a / b)) { Measure(3.0, a) / b }
    }

    @Test @JsName("measureProductDivFirstUnitGivesSecond")
    fun `Measure﹙A＊B﹚ div Unit A gives Measure of B`() {
        val a = UnitsA("a", 1.0)
        val b = UnitsB("b", 1.0)
        expect(Measure(5.0, b)) { Measure(5.0, a * b) / a }
    }

    @Test @JsName("measureProductDivSecondUnitGivesFirst")
    fun `Measure﹙A＊B﹚ div Unit B gives Measure of A`() {
        val a = UnitsA("a", 1.0)
        val b = UnitsB("b", 1.0)
        expect(Measure(5.0, a)) { Measure(5.0, a * b) / b }
    }

    @Test @JsName("measureDivRatioUnitGivesDenominator")
    fun `Measure A div UnitsRatio﹙A÷B﹚ gives Measure of B`() {
        val a = UnitsA("a", 1.0)
        val b = UnitsB("b", 1.0)
        expect(Measure(3.0, b)) { Measure(3.0, a) / UnitsRatio(a, b) }
    }

    @Test @JsName("measureInverseTimesUnitGivesScalar")
    fun `Measure of InverseUnits﹙A﹚ times Unit A gives scalar`() {
        val a = UnitsA("a", 1.0)
        expect(6.0) { Measure(6.0, InverseUnits(a)) * a }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Top-level Measure math functions: abs, round, ceil, floor
// ─────────────────────────────────────────────────────────────────────────────

class MeasureMathFunctionTests {

    @Test @JsName("absPositiveIsUnchanged")
    fun `abs of a positive measure is unchanged`() = expect(Measure(3.0, UnitsA("a"))) { abs(Measure(3.0, UnitsA("a"))) }

    @Test @JsName("absNegativeFlipsSign")
    fun `abs of a negative measure flips sign`() = expect(Measure(3.0, UnitsA("a"))) { abs(Measure(-3.0, UnitsA("a"))) }

    @Test @JsName("absZeroIsZero")
    fun `abs of zero measure is zero`() = expect(Measure(0.0, UnitsA("a"))) { abs(Measure(0.0, UnitsA("a"))) }

    @Test @JsName("roundHalfUp")
    fun `round rounds 2 point 5 up to 3`() = expect(Measure(3.0, UnitsA("a"))) { round(Measure(2.5, UnitsA("a"))) }

    @Test @JsName("roundDown")
    fun `round rounds 2 point 3 down to 2`() = expect(Measure(2.0, UnitsA("a"))) { round(Measure(2.3, UnitsA("a"))) }

    @Test @JsName("ceilRoundsUp")
    fun `ceil rounds 2 point 1 up to 3`() = expect(Measure(3.0, UnitsA("a"))) { ceil(Measure(2.1, UnitsA("a"))) }

    @Test @JsName("ceilOnWholeNumberIsUnchanged")
    fun `ceil on a whole number is unchanged`() = expect(Measure(4.0, UnitsA("a"))) { ceil(Measure(4.0, UnitsA("a"))) }

    @Test @JsName("floorRoundsDown")
    fun `floor rounds 2 point 9 down to 2`() = expect(Measure(2.0, UnitsA("a"))) { floor(Measure(2.9, UnitsA("a"))) }

    @Test @JsName("floorOnWholeNumberIsUnchanged")
    fun `floor on a whole number is unchanged`() = expect(Measure(5.0, UnitsA("a"))) { floor(Measure(5.0, UnitsA("a"))) }

    @Test @JsName("roundWithToNearestZeroReturnsOriginal")
    fun `round with toNearest of zero returns original measure`() = expect(Measure(3.7, UnitsA("a"))) { round(Measure(3.7, UnitsA("a")), toNearest = Measure(0.0, UnitsA("a"))) }

    @Test @JsName("roundWithToNearestSnapsToStep")
    fun `round with toNearest snaps to the closest step`() = expect(Measure(10.0, UnitsA("a"))) { round(Measure(9.6, UnitsA("a")), toNearest = Measure(10.0, UnitsA("a"))) }

    @Test @JsName("roundWithToNearestAcrossUnits")
    fun `round with toNearest converts across compatible units`() {
        val small = UnitsA("small",  1.0)
        val large = UnitsA("large", 10.0)
        expect(Measure(1.0, large)) { round(Measure(14.0, small), toNearest = Measure(1.0, large)) }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Measure
// ─────────────────────────────────────────────────────────────────────────────

class MeasureTests {
    @Test @JsName("zeroWorks")
    fun `zero works`() {
        val unitA = UnitsA("a", 10.0)
        val unitB = UnitsA("b",  1.0)

        val zero     = 0 * unitA
        val measureA = Measure(10.0, unitA)
        val measureB = Measure(10.0, unitB)
        val measureC = Measure( 0.0, unitB)
        val measureD = Measure( 0.0, unitA)

        expect(true, "$measureA > $zero"     ) { measureA  > zero     }
        expect(true, "$measureB > $zero"     ) { measureB  > zero     }
        expect(true, "$measureC == $zero"    ) { measureC == zero     }
//        expect(true, "$measureC == $measureD") { measureC == measureD }
    }

    @Test @JsName("comparisonsWork")
    fun `comparisons work`() {
        val unitA = UnitsA("a", 10.0)
        val unitB = UnitsA("b",  1.0)
        val unitC = UnitsA("c"      )

        val measureA = Measure(10.0, unitA)
        val measureB = Measure(10.0, unitB)
        val measureC = Measure(10.0, unitC)

        expect(true,  "$measureA == $measureA") { measureA == measureA }
        expect(true,  "$measureB == $measureB") { measureB == measureB }
        expect(true,  "$measureC == $measureC") { measureC == measureC }

        expect(true,  "$measureA  > $measureB") { measureA  > measureB }
        expect(false, "$measureA  < $measureB") { measureA  < measureB }
        expect(false, "$measureA == $measureB") { measureA == measureB }
//        expect(false, "$measureB == $measureC") { measureB == measureC }
    }

    @Test @JsName("plusMinusOperatorsWork")
    fun `+ -`() {
        val unitA = UnitsA("a", 10.0)
        val unitB = UnitsA("b",  1.0)

        val measureA = Measure(10.0, unitA)
        val measureB = Measure(10.0, unitB)

        expect(Measure(110.0, unitB)) { measureA + measureB }
        expect(Measure( 90.0, unitB)) { measureA - measureB }
    }

    @Test @JsName("unaryMinusOperatorsWork")
    fun `unary -`() {
        val unit = UnitsA("a")

        val measure = Measure(10.0, unit)

        expect(Measure(-10.0, unit)) { -measure }
    }

    @Test @JsName("timesDivideOperatorsWork")
    fun `＊ ÷`() {
        val op: (Operation<UnitsA>) -> Unit = {
            val unit    = UnitsA("a")
            val start   = 10.0
            val value   = 2.3
            val measure = Measure(start, unit)

            expect(Measure(it((measure `in` unit), value), unit)) { it(measure, value) }
        }

        listOf(times, divide).forEach(op)
    }

    @Test @JsName("remainderWorks")
    fun `a ﹪ b`() {
        val op: (Remainder<UnitsA>) -> Unit = {
            val unit     = UnitsA("a")
            val start    = 10.0
            val value    = 2.3
            val measure1 = Measure(start, unit)
            val measure2 = Measure(value, UnitsA("b", 0.5))

            expect(Measure(it((measure1 `in` unit), value), unit)) { it(measure1, value) }
            expect((measure1 `in` unit) % (measure2 `in` unit)) { it(measure1, measure2) }
        }

        op(Remainder())
    }

    @Test @JsName("toNearestWorks")
    fun `to nearest works`() {
        val unit = UnitsA("u")

        listOf(
            Pair( 25.67, 0.1 ),
            Pair(-50.00, 1.0 ),
            Pair(152.45, 10.0),
        ).forEach { (magnitude, nearest) ->
            expect((round(magnitude / nearest) * nearest) * unit) { (magnitude * unit).toNearest(nearest) }
        }
    }

    @Test @JsName("amountPropertyReturnsConstructorValue")
    fun `amount property returns the value passed to the constructor`() = expect(42.0) { Measure(42.0, UnitsA("a")).amount }

    @Test @JsName("unitsPropertyReturnsConstructorUnit")
    fun `units property returns the unit passed to the constructor`() {
        val unit = UnitsA("a")
        expect(unit) { Measure(1.0, unit).units }
    }

    @Test @JsName("measureToStringContainsAmountAndSuffix")
    fun `toString contains amount and unit suffix`() {
        val m = Measure(5.0, UnitsA("kg"))
        assertTrue(m.toString().contains("5") && m.toString().contains("kg"))
    }

    @Test @JsName("inInfixConvertsSameUnit")
    fun `in returns the raw amount when converting to the same unit`() {
        val unit = UnitsA("a", 10.0)
        expect(7.0) { (7 * unit) `in` unit }
    }

    @Test @JsName("inInfixConvertsToSmallerUnit")
    fun `in converts correctly to a smaller unit`() {
        val large = UnitsA("large", 10.0)
        val small = UnitsA("small",  1.0)
        expect(10.0) { (1 * large) `in` small }
    }

    @Test @JsName("inInfixConvertsToLargerUnit")
    fun `in converts correctly to a larger unit`() {
        val large = UnitsA("large", 10.0)
        val small = UnitsA("small",  1.0)
        expect(0.1) { (1 * small) `in` large }
    }

    @Test @JsName("asInfixReturnsMeasureInNewUnit")
    fun `as returns a Measure expressed in the target unit`() {
        val large  = UnitsA("large", 10.0)
        val small  = UnitsA("small",  1.0)
        expect(Measure(10.0, small)) { (1 * large) `as` small }
    }

    @Test @JsName("asInfixSameUnitIsIdentity")
    fun `as with the same unit is an identity`() {
        val unit = UnitsA("a", 5.0)
        expect(3 * unit) { (3 * unit) `as` unit }
    }

    @Test @JsName("absoluteValueOfPositiveIsUnchanged")
    fun `absoluteValue of a positive measure is unchanged`() {
        val unit = UnitsA("a")
        expect(5 * unit) { (5 * unit).absoluteValue }
    }

    @Test @JsName("absoluteValueOfNegativeIsPositive")
    fun `absoluteValue of a negative measure is positive`() {
        val unit = UnitsA("a")
        expect(5 * unit) { (-5 * unit).absoluteValue }
    }

    @Test @JsName("absoluteValueOfZeroIsZero")
    fun `absoluteValue of zero is zero`() {
        val unit = UnitsA("a")
        expect(0 * unit) { (0 * unit).absoluteValue }
    }

    @Test @JsName("signOfPositiveMeasureIsOne")
    fun `sign of a positive measure is 1`() = expect(1.0) { (10 * UnitsA("a")).sign }

    @Test @JsName("signOfNegativeMeasureIsMinusOne")
    fun `sign of a negative measure is -1`() = expect(-1.0) { (-10 * UnitsA("a")).sign }

    @Test @JsName("signOfZeroMeasureIsZero")
    fun `sign of a zero measure is 0`() = expect(0.0) { (0 * UnitsA("a")).sign }

    @Test @JsName("roundToIntRoundsHalfUp")
    fun `roundToInt rounds 0 point 5 up`() {
        val unit = UnitsA("a")
        expect(Measure(1.0, unit)) { Measure(0.5, unit).roundToInt() }
    }

    @Test @JsName("roundToIntRoundsDown")
    fun `roundToInt rounds values below 0 point 5 down`() {
        val unit = UnitsA("a")
        expect(Measure(2.0, unit)) { Measure(2.3, unit).roundToInt() }
    }

    @Test @JsName("roundToIntPreservesWholeNumber")
    fun `roundToInt leaves a whole number unchanged`() {
        val unit = UnitsA("a")
        expect(Measure(7.0, unit)) { Measure(7.0, unit).roundToInt() }
    }

    @Test @JsName("roundToIntNegativeValue")
    fun `roundToInt rounds negative values correctly`() {
        val unit = UnitsA("a")
        expect(Measure(-2.0, unit)) { Measure(-2.3, unit).roundToInt() }
    }

    @Test @JsName("toNearestMeasureRoundsToStep")
    fun `toNearest with a Measure step rounds to the closest multiple`() {
        val unit = UnitsA("a")
        expect(Measure(10.0, unit)) { Measure(9.6, unit) toNearest Measure(10.0, unit) }
    }

    @Test @JsName("toNearestMeasureRoundsDown")
    fun `toNearest with a Measure step rounds down when below midpoint`() {
        val unit = UnitsA("a")
        expect(Measure(10.0, unit)) { Measure(10.4, unit) toNearest Measure(10.0, unit) }
    }

    @Test @JsName("toNearestMeasureAcrossUnits")
    fun `toNearest snaps to the target unit`() {
        val small = UnitsA("small",  1.0)
        val large = UnitsA("large", 10.0)
        // 14 small → nearest 10 small (1 large) = 1 large expressed in large units
        expect(Measure(1.0, large)) { Measure(14.0, small) toNearest Measure(1.0, large) }
    }

    @Test @JsName("remMeasureByMeasureGivesScalarRemainder")
    fun `rem of two measures gives the scalar remainder`() {
        val unit = UnitsA("a")
        expect(1.0) { Measure(10.0, unit) % Measure(3.0, unit) }
    }

    @Test @JsName("remMeasureByMeasureAcrossUnits")
    fun `rem of two measures converts units before computing`() {
        val small = UnitsA("small",  1.0)
        val large = UnitsA("large", 10.0)
        // 25 small % 1 large(=10 small) → 25 % 10 = 5 (scalar)
        expect(5.0) { Measure(25.0, small) % Measure(1.0, large) }
    }

    @Test @JsName("remMeasureByDoubleGivesMeasure")
    fun `rem of a measure by a Double gives a Measure`() {
        val unit = UnitsA("a")
        expect(Measure(1.0, unit)) { Measure(10.0, unit) % 3.0 }
    }

    @Test @JsName("measureTimesIntScalar")
    fun `measure times Int scalar scales correctly`() {
        val unit = UnitsA("a")
        expect(Measure(30.0, unit)) { Measure(10.0, unit) * 3 }
    }

    @Test @JsName("measureDivIntScalar")
    fun `measure div Int scalar scales correctly`() {
        val unit = UnitsA("a")
        expect(Measure(5.0, unit)) { Measure(10.0, unit) / 2 }
    }

    @Test @JsName("measureTimesDoubleScalar")
    fun `measure times Double scalar scales correctly`() {
        val unit = UnitsA("a")
        expect(Measure(2.5, unit)) { Measure(10.0, unit) * 0.25 }
    }

    @Test @JsName("measureDivDoubleScalar")
    fun `measure div Double scalar scales correctly`() {
        val unit = UnitsA("a")
        expect(Measure(4.0, unit)) { Measure(10.0, unit) / 2.5 }
    }

    @Test @JsName("equalMeasuresHaveSameHashCode")
    fun `equal measures have the same hashCode`() {
        val unit = UnitsA("a", 1.0)
        assertEquals(Measure(5.0, unit).hashCode(), Measure(5.0, unit).hashCode())
    }

    @Test @JsName("measureNotEqualToNull")
    fun `measure is not equal to null`() =
        assertFalse(Measure(1.0, UnitsA("a")).equals(null))

    @Test @JsName("measureNotEqualToArbitraryObject")
    fun `measure is not equal to an arbitrary object`() =
        assertFalse(Measure(1.0, UnitsA("a")).equals("not a measure"))

    @Test @JsName("measuresWithDifferentAmountsNotEqual")
    fun `measures with different amounts are not equal`() =
        assertNotEquals(Measure(1.0, UnitsA("a")), Measure(2.0, UnitsA("a")))
}

// ─────────────────────────────────────────────────────────────────────────────
// Operation / Remainder helpers
// ─────────────────────────────────────────────────────────────────────────────

private interface Operation<T: Units> {
    operator fun invoke(first: Double,     second: Double): Double
    operator fun invoke(first: Measure<T>, second: Double): Measure<T>
}

private val times = object: Operation<UnitsA> {
    override fun invoke(first: Double,          second: Double) = first * second
    override fun invoke(first: Measure<UnitsA>, second: Double) = first * second
}

private val divide = object: Operation<UnitsA> {
    override fun invoke(first: Double,          second: Double) = first / second
    override fun invoke(first: Measure<UnitsA>, second: Double) = first / second
}

private class Remainder<T: Units> {
    operator fun invoke(first: Double,     second: Double    ): Double     = first % second
    operator fun invoke(first: Measure<T>, second: Double    ): Measure<T> = first % second
    operator fun invoke(first: Measure<T>, second: Measure<T>): Double     = first % second
}
