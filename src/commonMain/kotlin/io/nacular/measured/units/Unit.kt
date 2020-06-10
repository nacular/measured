package io.nacular.measured.units

import io.nacular.measured.JvmName
import kotlin.math.roundToInt

/**
 * Created by Nicholas Eddy on 4/4/18.
 */

abstract class Unit(val suffix: String, val ratio: Double = 1.0) {
    internal open val spaceBetweenMagnitude = true

    override fun toString() = suffix

    override fun hashCode(): Int {
        var result = suffix.hashCode()
        result = 31 * result + ratio.hashCode()
        result = 31 * result + spaceBetweenMagnitude.hashCode()
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Unit) return false

        if (suffix != other.suffix) return false
        if (ratio  != other.ratio ) return false
        if (spaceBetweenMagnitude != other.spaceBetweenMagnitude) return false

        return true
    }
}

class InverseUnit<T: Unit>(val unit: T): Unit("1/${unit.suffix}", 1 / unit.ratio)

operator fun <A: Unit, B: A> A.compareTo(other: B) = ratio.compareTo(other.ratio)

/**
 * Creates a QuotientUnit using the division operator, eg Mile / Hour
 */
operator fun <A: Unit, B: Unit> A.div(other: B) =
    UnitRatio(this, other)

operator fun <A: Unit, B: Unit> A.times(other: UnitRatio<B, A>) = this.ratio / other.denominator.ratio * other.numerator

operator fun <A: Unit, B: Unit> A.times(other: InverseUnit<B>) =
    UnitRatio(this, other.unit)

/**
 * For units after multiplication
 */
class UnitProduct<A: Unit, B: Unit>(val a: A, val b: B): Unit(if (a==b) "($a)^2" else "$a$b", a.ratio * b.ratio)

typealias Square<T> = UnitProduct<T, T>

@JvmName("div1") operator fun <A: Unit, B: Unit> UnitProduct<A, B>.div(other: A                ) = a.ratio / other.ratio * b
@JvmName("div2") operator fun <A: Unit, B: Unit> UnitProduct<A, B>.div(other: B                ) = b.ratio / other.ratio * a
@JvmName("div3") operator fun <A: Unit> UnitProduct<A, A>.div(other: A                ) = a.ratio / other.ratio * b

@JvmName("div1") operator fun <A: Unit, B: Unit, C: Unit> UnitProduct<A, B>.div(other: UnitProduct<C, B>) = a / other.a
@JvmName("div2") operator fun <A: Unit, B: Unit, C: Unit> UnitProduct<A, B>.div(other: UnitProduct<C, A>) = b / other.a
@JvmName("div3") operator fun <A: Unit, B: Unit, C: Unit> UnitProduct<A, B>.div(other: UnitProduct<B, C>) = a / other.b
@JvmName("div4") operator fun <A: Unit, B: Unit, C: Unit> UnitProduct<A, B>.div(other: UnitProduct<A, C>) = b / other.b
@JvmName("div5") operator fun <A: Unit, B: Unit> UnitProduct<A, B>.div(other: UnitProduct<A, A>) = b / other.b
@JvmName("div6") operator fun <A: Unit, B: Unit> UnitProduct<A, B>.div(other: UnitProduct<B, A>) = ratio / other.ratio
@JvmName("div7") operator fun <A: Unit, B: Unit> UnitProduct<A, B>.div(other: UnitProduct<A, B>) = ratio / other.ratio
@JvmName("div8") operator fun <A: Unit, B: Unit> UnitProduct<A, B>.div(other: UnitProduct<B, B>) = b.ratio / other.b.ratio * (a / other.a)

/**
 * Create a ProductUnit using multiplication operator, eg Metre * Metre for area
 */
operator fun <A: Unit, B: Unit> A.times(other: B) =
    UnitProduct(this, other)
operator fun <A: Unit, B: Unit> A.div(other: UnitRatio<A, B>) = this * other.reciprocal

/**
 * For units after division, A/B
 */
class UnitRatio<A: Unit, B: Unit>(val numerator: A, val denominator: B): Unit("$numerator/$denominator", numerator.ratio / denominator.ratio) {
    val reciprocal by lazy { UnitRatio(denominator, numerator) }
}

@JvmName("times1") operator fun <A: Unit, B: Unit> UnitRatio<A, B>.times(other: UnitRatio<A, B>) = numerator * other.numerator / (denominator * other.denominator)
@JvmName("times2") operator fun <A: Unit, B: Unit> UnitRatio<A, B>.times(other: UnitRatio<B, A>) = numerator * other.numerator / (denominator * other.denominator)
@JvmName("times3") operator fun <A: Unit, B: Unit, C: Unit, D: Unit> UnitRatio<A, B>.times(other: UnitRatio<C, D>) = numerator * other.numerator / (denominator * other.denominator)

@JvmName("div1") operator fun <A: Unit, B: Unit> UnitRatio<A, B>.div(other: UnitRatio<A, B>) = this * other.reciprocal
@JvmName("div2") operator fun <A: Unit, B: Unit> UnitRatio<A, B>.div(other: UnitRatio<B, A>) = this * other.reciprocal
@JvmName("div3") operator fun <A: Unit, B: Unit, C: Unit, D: Unit> UnitRatio<A, B>.div(other: UnitRatio<C, D>) = this * other.reciprocal

operator fun <A: Unit, B: Unit> UnitRatio<A, B>.                times(other: B                 ) = other.ratio / denominator.ratio * numerator

@JvmName("abcb") operator fun <A: Unit, B: Unit, C: Unit> UnitRatio<A, UnitProduct<B, C>>.times(other: B                 ) = other.ratio / denominator.a.ratio * (numerator / denominator.b)
@JvmName("abcd") operator fun <A: Unit, B: Unit, C: Unit, D: Unit> UnitRatio<A, UnitProduct<B, C>>.times(other: D                 ) = numerator * other / denominator

                 operator fun <A: Unit, B: Unit> UnitRatio<A, B>.                                div(other: B                 ) = numerator / (denominator * other)
@JvmName("div1") operator fun <A: Unit, B: Unit> UnitRatio<UnitProduct<A, A>, UnitProduct<B, B>>.div(other: A                 ) = numerator.a.ratio / other.ratio * (numerator.b / denominator)
@JvmName("div2") operator fun <A: Unit, B: Unit, C: Unit> UnitRatio<UnitProduct<A, A>, UnitProduct<B, C>>.div(other: A                 ) = numerator.a.ratio / other.ratio * (numerator.b / denominator)
@JvmName("div3") operator fun <A: Unit, B: Unit, C: Unit, D: Unit> UnitRatio<UnitProduct<A, B>, UnitProduct<C, D>>.div(other: A                 ) = numerator.a.ratio / other.ratio * (numerator.b / denominator)
//@JvmName("square") operator fun <A: Unit2, B: Unit2, C: Unit2> QuotientUnit<A, B>.div(other: QuotientUnit<A, C>) = this * other.denominator / other.numerator
//@JvmName("square") operator fun <A: Unit2, B: Unit2, C: Unit2> QuotientUnit<A, B>.div(other: QuotientUnit<B, C>) = this * other.denominator / other.numerator

// m/s * (s2/m) => s
operator fun <A: Unit, B: Unit> UnitRatio<A, B>.div(other: UnitRatio<A, Square<B>>) = numerator.ratio / other.numerator.ratio * (other.denominator / denominator)

operator fun <A: Unit, B: Unit> Measure<A>.div(other: Measure<B>) = amount / other.amount * (unit / other.unit)

@JvmName("times1") operator fun <A: Unit, B: Unit> Measure<A>.                              times(other: Measure<B>) = amount * other.amount * (unit * other.unit)
@JvmName("times2") operator fun <A: Unit, B: Unit> Measure<A>.                              times(other: Measure<UnitRatio<B, A>>) = amount * other.amount * (unit * other.unit)
@JvmName("times3") operator fun <A: Unit, B: Unit> Measure<UnitRatio<A, B>>.                times(other: Measure<B>) = amount * other.amount * (unit * other.unit)
@JvmName("times4") operator fun <A: Unit, B: Unit> Measure<UnitRatio<A, B>>.                times(other: Measure<UnitRatio<A, B>>) = amount * other.amount * (unit * other.unit)
@JvmName("times5") operator fun <A: Unit, B: Unit, C: Unit> Measure<UnitRatio<A, UnitProduct<B, C>>>.times(other: Measure<B>) = amount * other.amount * (unit * other.unit)
@JvmName("times6") operator fun <A: Unit, B: Unit, C: Unit, D: Unit> Measure<UnitRatio<A, UnitProduct<B, C>>>.times(other: Measure<D>) = amount * other.amount * (unit * other.unit)
@JvmName("times7") operator fun <A: Unit, B: Unit> Measure<A>.                              times(other: Measure<InverseUnit<B>>) = amount * other.amount * (unit * other.unit)

// TODO: Kapt code generation possible?
operator fun <A: Unit> Measure<A>.rem(other: Measure<A>) = amount % other.amount * (unit.ratio % other.unit.ratio)

@JvmName("div16") operator fun <A: Unit> Measure<A>.                div(other: Measure<A>) = amount / other.amount * (unit.ratio / other.unit.ratio)
@JvmName("div1" ) operator fun <A: Unit, B: Unit> Measure<UnitProduct<A, B>>.div(other: Measure<A>) = amount / other.amount * (unit / other.unit)
@JvmName("div2" ) operator fun <A: Unit, B: Unit> Measure<UnitProduct<A, B>>.div(other: Measure<B>) = amount / other.amount * (unit / other.unit)
@JvmName("div3" ) operator fun <A: Unit, B: Unit, C: Unit> Measure<UnitProduct<A, B>>.div(other: Measure<UnitProduct<C, B>>) = amount / other.amount * (unit / other.unit)
@JvmName("div4" ) operator fun <A: Unit, B: Unit, C: Unit> Measure<UnitProduct<A, B>>.div(other: Measure<UnitProduct<C, A>>) = amount / other.amount * (unit / other.unit)
@JvmName("div5" ) operator fun <A: Unit, B: Unit, C: Unit> Measure<UnitProduct<A, B>>.div(other: Measure<UnitProduct<B, C>>) = amount / other.amount * (unit / other.unit)
@JvmName("div6" ) operator fun <A: Unit, B: Unit, C: Unit> Measure<UnitProduct<A, B>>.div(other: Measure<UnitProduct<A, C>>) = amount / other.amount * (unit / other.unit)
@JvmName("div7" ) operator fun <A: Unit, B: Unit> Measure<UnitProduct<A, B>>.div(other: Measure<UnitProduct<A, A>>) = amount / other.amount * (unit / other.unit)
@JvmName("div8" ) operator fun <A: Unit, B: Unit> Measure<UnitProduct<A, B>>.div(other: Measure<UnitProduct<B, B>>) = amount / other.amount * (unit / other.unit)

@JvmName("div9" ) operator fun <A: Unit, B: Unit> Measure<UnitRatio<A, B>>.                                div(other: Measure<B>) = amount / other.amount * (unit / other.unit)
@JvmName("div10") operator fun <A: Unit, B: Unit, C: Unit, D: Unit> Measure<UnitRatio<A, B>>.                                div(other: Measure<UnitRatio<C, D>>) = amount / other.amount * (unit / other.unit)
@JvmName("div11") operator fun <A: Unit, B: Unit> Measure<UnitRatio<A, B>>.                                div(other: Measure<UnitRatio<A, Square<B>>>) = amount / other.amount * (unit / other.unit)
@JvmName("div12") operator fun <A: Unit, B: Unit> Measure<UnitRatio<UnitProduct<A, A>, UnitProduct<B, B>>>.div(other: Measure<A>) = amount / other.amount * (unit / other.unit)
@JvmName("div13") operator fun <A: Unit, B: Unit, C: Unit> Measure<UnitRatio<UnitProduct<A, A>, UnitProduct<B, C>>>.div(other: Measure<A>) = amount / other.amount * (unit / other.unit)
@JvmName("div14") operator fun <A: Unit, B: Unit, C: Unit, D: Unit> Measure<UnitRatio<UnitProduct<A, B>, UnitProduct<C, D>>>.div(other: Measure<A>) = amount / other.amount * (unit / other.unit)
@JvmName("div15") operator fun <A: Unit, B: Unit> Measure<A>.                                              div(other: Measure<UnitRatio<A, B>>) = amount / other.amount * (unit / other.unit)

fun <A: Unit, B: A> minOf(a: A, b: B) = if (a < b) a else b

/**
 * A quantity with a unit type
 */
class Measure<T: Unit>(val amount: Double, val unit: T): Comparable<Measure<T>> {
    /**
     * Convert this type into another compatible type.
     * Type must share parent
     * (eg Mile into Kilometer, because they both are made from Distance)
     */
    infix fun <A: T> `as`(other: A) = if (unit == other) this else Measure(
        this `in` other,
        other
    )

    infix fun <A: T> `in`(other: A) = if (unit == other) amount else  amount * (unit.ratio / other.ratio)

    /**
     * Add another compatible quantity to this one
     */
    operator fun plus(other: Measure<T>) = minOf(
        unit,
        other.unit
    ).let { Measure((this `in` it) + (other `in` it), it) }

    /**
     * Subtract a compatible quantity from this one
     */
    operator fun minus(other: Measure<T>) = minOf(
        unit,
        other.unit
    ).let { Measure((this `in` it) - (other `in` it), it) }

    operator fun unaryMinus(): Measure<T> =
        Measure(-amount, unit)

    /**
     * Multiply this by a scalar value, used for things like "double this distance",
     * "1.5 times the speed", etc
     */
    operator fun times(other: Number) = amount * other.toDouble() * unit

    /**
     * Divide this by a scalar, used for things like "halve the speed"
     */
    operator fun div(other: Number) = amount / other.toDouble() * unit

    fun roundToInt() = amount.roundToInt() * unit

    /**
     * Compare this value with another quantity - which must have the same type
     * Units are converted before comparison
     */
    override fun compareTo(other: Measure<T>) = (this `as` other.unit).amount.compareTo(other.amount)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Measure<*>) return false
//        if (this.amount == 0.0 && other.amount == 0.0) return true TODO: Should this be true?

        val resultUnit = minOf(
            unit,
            (other as Measure<T>).unit
        )

        val a = this  `in` resultUnit
        val b = other `in` resultUnit

        return a == b
    }

    override fun hashCode(): Int {
        return (amount * unit.ratio).hashCode()
    }

    override fun toString(): String {
        return "$amount${if (unit.spaceBetweenMagnitude) " " else ""}${unit.suffix}"
    }
}

/**
 * Helpers for converting numbers into quantities
 */
infix fun <T: Unit> Number.into(unit: T) =
    Measure(this.toDouble(), unit)

operator fun <T: Unit> Number.times(unit: T) = this into unit
operator fun <T: Unit> Number.div(unit: T) = this into InverseUnit(
    unit
)

operator fun <T: Unit> T.times(value: Number) = value into this

operator fun <T: Unit> T.invoke(value: Number) = value into this


fun <T: Unit> abs(value: Measure<T>) = kotlin.math.abs(value.amount) * value.unit

/**
 * Inverse of Quantity.times(value: Number)
 * Haven't implemented division due to no reciprocal unit
 */
operator fun <T: Unit> Number.times(quantity: Measure<T>) = quantity * this

operator fun Length.times(other: Time) =
    UnitProduct(this, other)
operator fun Time.times(other: Length) =
    UnitProduct(other, this)

typealias Velocity     = UnitRatio<Length, Time>
typealias Acceleration = UnitRatio<Length, Square<Time>>