@file:Suppress("FunctionName", "NonAsciiCharacters")

package io.nacular.measured.units

import kotlin.js.JsName
import kotlin.test.Test
import kotlin.test.expect

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
}

class UnitRatioTests {
    @Test @JsName("defaultMultiplierIs1")
    fun `default ratio is 1`() {
        val a = UnitsRatio(UnitsA("a"), UnitsB("b"))

        expect(1.0, "$a.ratio") { a.ratio }
    }

    @Test @JsName("doubleReciprocal")
    fun `1 ÷ (1 ÷ a) == a`() {
        val a = UnitsRatio(UnitsA("a"), UnitsB("b"))

        expect(a, "$a.reciprocal.reciprocal") { a.reciprocal.reciprocal }
    }

//    @Test @JsName("divAWorks")
//    fun `(A÷B) ÷ A = 1÷B`() {
//        val a = UnitsRatio(UnitsA("a", 10.0), UnitsB("b"))
//        val b = UnitsB("a", 10.0)
//
//        expect(1 * Units("b"), "$a / $b") { a / b }
//    }

    @Test @JsName("timesBWorks")
    fun `(A÷B) * B = A`() {
        val a = UnitsRatio(UnitsA("a", 10.0), UnitsB("b"))
        val b = UnitsB("b", 1.0)

        expect(1 * UnitsA("a", 10.0), "$a * $b") { a * b }
    }

    @Test @JsName("timesInverseWorks")
    fun `(A÷B) * (B÷A) = 1`() {
        val a = UnitsRatio(UnitsA("a", 10.0), UnitsB("b"))

        expect(1.0, "$a * 1/$a") { a * a.reciprocal }
    }

    @Test @JsName("divSelfWorks")
    fun `(A÷B) ÷ (A÷B) = 1`() {
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
}

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
    fun `* ÷`() {
        val op: (Operation<UnitsA>) -> kotlin.Unit = {
            val unit    = UnitsA("a")
            val start   = 10.0
            val value   = 2.3
            val measure = Measure(start, unit)

            expect(Measure(it((measure `in` unit), value), unit)) { it(measure, value) }
        }

        listOf(times, divide).forEach(op)
    }
}

interface Operation<T: Units> {
    operator fun invoke(first: Double,     second: Double): Double
    operator fun invoke(first: Measure<T>, second: Double): Measure<T>
}

private val times = object:
    Operation<UnitsA> {
    override fun invoke(first: Double,         second: Double) = first * second
    override fun invoke(first: Measure<UnitsA>, second: Double) = first * second
}

private val divide = object:
    Operation<UnitsA> {
    override fun invoke(first: Double,         second: Double) = first / second
    override fun invoke(first: Measure<UnitsA>, second: Double) = first / second
}