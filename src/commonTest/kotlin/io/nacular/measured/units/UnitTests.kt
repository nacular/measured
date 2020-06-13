@file:Suppress("FunctionName", "NonAsciiCharacters")

package io.nacular.measured.units

import io.nacular.measured.JsName
import kotlin.test.Test
import kotlin.test.expect

/**
 * Created by Nicholas Eddy on 2/18/18.
 */

private class UnitA: Unit {
    constructor(suffix: String               ): super(suffix       )
    constructor(suffix: String, ratio: Double): super(suffix, ratio)

    operator fun div(other: UnitA) = ratio / other.ratio
}

private class UnitB: Unit {
    constructor(suffix: String               ): super(suffix       )
    constructor(suffix: String, ratio: Double): super(suffix, ratio)

    operator fun div(other: UnitA) = ratio / other.ratio
}

class UnitTests {
    @Test @JsName("defaultRatioIs1")
    fun `default ratio is 1`() {
        val a = object: Unit("a") {}

        expect(1.0, "$a.ratio") { a.ratio }
    }

    @Test @JsName("divWorks")
    fun `div works`() {
        val a = UnitA("a", 10.0)
        val b = UnitA("b",  1.0)

        expect(10.0, "$a / $b") { a / b }
    }

    @Test @JsName("toStringWorks")
    fun `toString works`() {
        val a = object: Unit("description", 10.0) {}

        expect("description", "$a.toString()") { a.toString() }
    }

    @Test @JsName("comparisonsWork")
    fun `comparisons work`() {
        val a = UnitA("a", 10.0)
        val b = UnitA("b",  1.0)
        val c = UnitA("a", 10.0)

        expect(true,  "$a > $b" ) { a  > b }
        expect(false, "$a < $b" ) { a  < b }
        expect(true,  "$a == $a") { a == a }
        expect(false, "$a == $b") { a == b }
        expect(true,  "$a == $c") { a == c }
    }
}

class UnitRatioTests {
    @Test @JsName("defaultMultiplierIs1")
    fun `default ratio is 1`() {
        val a = UnitRatio(UnitA("a"), UnitB("b"))

        expect(1.0, "$a.ratio") { a.ratio }
    }

    @Test @JsName("doubleReciprocal")
    fun `1 ÷ (1 ÷ a) == a`() {
        val a = UnitRatio(UnitA("a"), UnitB("b"))

        expect(a, "$a.reciprocal.reciprocal") { a.reciprocal.reciprocal }
    }

//    @Test @JsName("divAWorks")
//    fun `(A÷B) ÷ A = 1÷B`() {
//        val a = UnitRatio(UnitA("a", 10.0), UnitB("b"))
//        val b = UnitB("a", 10.0)
//
//        expect(1 * Unit("b"), "$a / $b") { a / b }
//    }

    @Test @JsName("timesBWorks")
    fun `(A÷B) * B = A`() {
        val a = UnitRatio(UnitA("a", 10.0), UnitB("b"))
        val b = UnitB("b", 1.0)

        expect(1 * UnitA("a", 10.0), "$a * $b") { a * b }
    }

    @Test @JsName("timesInverseWorks")
    fun `(A÷B) * (B÷A) = 1`() {
        val a = UnitRatio(UnitA("a", 10.0), UnitB("b"))

        expect(1.0, "$a * 1/$a") { a * a.reciprocal }
    }

    @Test @JsName("divSelfWorks")
    fun `(A÷B) ÷ (A÷B) = 1`() {
        val a = UnitRatio(UnitA("a", 10.0), UnitB("b"))

        expect(1.0, "$a / $a") { a / a }
    }

    @Test @JsName("toStringWorks")
    fun `toString works`() {
        val a     = UnitA("a", 10.0)
        val b     = UnitB("b"      )
        val ratio = UnitRatio(a, b)

        ratio.let            { expect("$a/$b", "$it.toString()") { it.toString() } }
        ratio.reciprocal.let { expect("$b/$a", "$it.toString()") { it.toString() } }
    }

    @Test @JsName("comparisonsWork")
    fun `comparisons work`() {
        val a = UnitRatio(UnitA("a1", 10.0), UnitB("b"))
        val b = UnitRatio(UnitA("a2",  1.0), UnitB("b"))
        val c = UnitRatio(UnitA("a1", 10.0), UnitB("b"))

        expect(true,  "$a > $b" ) { a  > b }
        expect(false, "$a < $b" ) { a  < b }
        expect(true,  "$a == $a") { a == a }
        expect(false, "$a == $b") { a == b }
        expect(true,  "$a == $c") { a == c }
    }
}

class MeasureTests {
    @Test @JsName("zeroWorks")
    fun `zero works`() {
        val unitA = UnitA("a", 10.0)
        val unitB = UnitA("b",  1.0)

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
        val unitA = UnitA("a", 10.0)
        val unitB = UnitA("b",  1.0)
        val unitC = UnitA("c"      )

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
        val unitA = UnitA("a", 10.0)
        val unitB = UnitA("b",  1.0)

        val measureA = Measure(10.0, unitA)
        val measureB = Measure(10.0, unitB)

        expect(Measure(110.0, unitB)) { measureA + measureB }
        expect(Measure( 90.0, unitB)) { measureA - measureB }
    }

    @Test @JsName("unaryMinusOperatorsWork")
    fun `unary -`() {
        val unit = UnitA("a")

        val measure = Measure(10.0, unit)

        expect(Measure(-10.0, unit)) { -measure }
    }

    @Test @JsName("timesDivideOperatorsWork")
    fun `* ÷`() {
        val op: (Operation<UnitA>) -> kotlin.Unit = {
            val unit    = UnitA("a")
            val start   = 10.0
            val value   = 2.3
            val measure = Measure(start, unit)

            expect(Measure(it((measure `in` unit), value), unit)) { it(measure, value) }
        }

        listOf(times, divide).forEach(op)
    }
}

interface Operation<T: Unit> {
    operator fun invoke(first: Double,     second: Double): Double
    operator fun invoke(first: Measure<T>, second: Double): Measure<T>
}

private val times = object:
    Operation<UnitA> {
    override fun invoke(first: Double,         second: Double) = first * second
    override fun invoke(first: Measure<UnitA>, second: Double) = first * second
}

private val divide = object:
    Operation<UnitA> {
    override fun invoke(first: Double,         second: Double) = first / second
    override fun invoke(first: Measure<UnitA>, second: Double) = first / second
}