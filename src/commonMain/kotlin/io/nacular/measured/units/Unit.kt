package io.nacular.measured.units

import io.nacular.measured.JvmName
import kotlin.math.roundToInt

/**
 * Base class for all types that can represent a unit of measure.
 * A Unit type can have multiple "members", each being some fraction of the base
 * unit for that type. Time for example, might have seconds as the base unit
 * and minute as a unit that is 60 times the base unit. This allows for a
 * set of different representations of the unit.
 *
 * @constructor
 * @param suffix to use when printing the unit in a human readable way
 * @param ratio of this unit relative to the base-unit
 */
abstract class Unit(val suffix: String, val ratio: Double = 1.0) {
    /**
     * Whether there should be a space between the unit's name and the magnitude
     * of a value with that unit. Most units are displayed like this: 45 kg. But
     * some, like degrees are done w/o the space: 45°
     */
    protected open val spaceBetweenMagnitude = true

    internal fun measureSuffix() = if (spaceBetweenMagnitude) " $suffix" else suffix

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

/**
 * Represents the product of two Units: A * B.
 *
 * @constructor
 * @param first unit being multiplied
 * @param second unit being multiplied
 */
class UnitProduct<A: Unit, B: Unit>(val first: A, val second: B): Unit(if (first==second) "($first)^2" else "$first$second", first.ratio * second.ratio)

typealias Square<T> = UnitProduct<T, T>

/**
 * Represents the ratio of two Units: A / B.
 *
 * @constructor
 * @param numerator unit being divided
 * @param denominator unit dividing numerator
 */
class UnitRatio<A: Unit, B: Unit>(val numerator: A, val denominator: B): Unit("$numerator/$denominator", numerator.ratio / denominator.ratio) {
    /** The Inverse of this unit. */
    val reciprocal by lazy { UnitRatio(denominator, numerator) }
}

/**
 * The inverse of a given Unit.
 *
 * @constructor
 * @param unit this is the inverse of
 */
class InverseUnit<T: Unit>(val unit: T): Unit("1/${unit.suffix}", 1 / unit.ratio)

/**
 * Compares two units
 * @param other unit to compare
 * @return -1 if this unit is smaller, 1 if the other is smaller, and 0 if they are equal
 */
operator fun <A: Unit, B: A> A.compareTo(other: B): Int = ratio.compareTo(other.ratio)

/**
 * @return the smaller of the two Units
 */
fun <A: Unit, B: A> minOf(first: A, second: B) = if (first < second) first else second


/**
 * A quantity with a unit type
 */
class Measure<T: Unit>(val amount: Double, val unit: T): Comparable<Measure<T>> {
    /**
     * Convert this type into another compatible type.
     * Type must share parent
     * (eg Mile into Kilometer, because they both are made from Distance)
     */
    infix fun <A: T> `as`(other: A) = if (unit == other) this else Measure(this `in` other, other)

    infix fun <A: T> `in`(other: A) = if (unit == other) amount else  amount * (unit.ratio / other.ratio)

    /**
     * Add another compatible quantity to this one
     */
    operator fun plus(other: Measure<T>) = minOf(unit, other.unit).let { Measure((this `in` it) + (other `in` it), it) }

    /**
     * Subtract a compatible quantity from this one
     */
    operator fun minus(other: Measure<T>) = minOf(unit, other.unit).let { Measure((this `in` it) - (other `in` it), it) }

    operator fun unaryMinus(): Measure<T> = Measure(-amount, unit)

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

        val resultUnit = minOf(unit, (other as Measure<T>).unit)

        val a = this  `in` resultUnit
        val b = other `in` resultUnit

        return a == b
    }

    override fun hashCode(): Int {
        return (amount * unit.ratio).hashCode()
    }

    override fun toString(): String = "$amount${unit.measureSuffix()}"
}

/** A * B */                              operator fun <A: Unit, B: Unit> A.times(other: B              ): UnitProduct<A, B> = UnitProduct(this, other)
/** A * (1 / B) */                        operator fun <A: Unit, B: Unit> A.times(other: InverseUnit<B> ): UnitRatio<A, B>   = this / other.unit
/** A * (B / A) */                        operator fun <A: Unit, B: Unit> A.times(other: UnitRatio<B, A>): Measure<B>        = this.ratio / other.denominator.ratio * other.numerator
// This cannot be defined given the next definition unfortunately
//operator fun <A: Unit> A.div(other: A) = this.ratio / other.ratio
/** A / B */                              operator fun <A: Unit, B: Unit> A.div  (other: B              ): UnitRatio<A, B>   = UnitRatio(this, other)
/** A / (A / B) == A * (B / A) */         operator fun <A: Unit, B: Unit> A.div  (other: UnitRatio<A, B>): Measure<B>        = this * other.reciprocal

/** (A * B) / A */       @JvmName("div1") operator fun <A: Unit, B: Unit>          UnitProduct<A, B>.div(other: A                ): Measure<B>               = first.ratio  / other.ratio * second
/** (A * B) / B */       @JvmName("div2") operator fun <A: Unit, B: Unit>          UnitProduct<A, B>.div(other: B                ): Measure<A>               = second.ratio / other.ratio * first
/** (A * A) / A */       @JvmName("div3") operator fun <A: Unit>                   UnitProduct<A, A>.div(other: A                ): Measure<A>               = first.ratio  / other.ratio * second
/** (A * B) / (C * B) */ @JvmName("div1") operator fun <A: Unit, B: Unit, C: Unit> UnitProduct<A, B>.div(other: UnitProduct<C, B>): UnitRatio<A, C>          = first  / other.first
/** (A * B) / (C * A) */ @JvmName("div2") operator fun <A: Unit, B: Unit, C: Unit> UnitProduct<A, B>.div(other: UnitProduct<C, A>): UnitRatio<B, C>          = second / other.first
/** (A * B) / (B * C) */ @JvmName("div3") operator fun <A: Unit, B: Unit, C: Unit> UnitProduct<A, B>.div(other: UnitProduct<B, C>): UnitRatio<A, C>          = first  / other.second
/** (A * B) / (A * C) */ @JvmName("div4") operator fun <A: Unit, B: Unit, C: Unit> UnitProduct<A, B>.div(other: UnitProduct<A, C>): UnitRatio<B, C>          = second / other.second
/** (A * B) / (A * A) */ @JvmName("div5") operator fun <A: Unit, B: Unit>          UnitProduct<A, B>.div(other: UnitProduct<A, A>): UnitRatio<B, A>          = second / other.second
/** (A * B) / (A * B) */ @JvmName("div7") operator fun <A: Unit, B: Unit>          UnitProduct<A, B>.div(other: UnitProduct<A, B>): Double                   = ratio  / other.ratio
/** (A * B) / (B * A) */ @JvmName("div6") operator fun <A: Unit, B: Unit>          UnitProduct<A, B>.div(other: UnitProduct<B, A>): Double                   = ratio  / other.ratio
/** (A * B) / (B * B) */ @JvmName("div8") operator fun <A: Unit, B: Unit>          UnitProduct<A, B>.div(other: UnitProduct<B, B>): Measure<UnitRatio<A, B>> = second.ratio / other.second.ratio * (first / other.first)

/** (A / B) * B */        @JvmName("times1") operator fun <A: Unit, B: Unit>                   UnitRatio<A, B>.                times(other: B              ): Measure<A>                                      = other.ratio / denominator.ratio * numerator
/** (A / (B * C)) * B */  @JvmName("times2") operator fun <A: Unit, B: Unit, C: Unit>          UnitRatio<A, UnitProduct<B, C>>.times(other: B              ): Measure<UnitRatio<A, C>>                        = other.ratio / denominator.first.ratio * (numerator / denominator.second)
/** (A / (B * C)) * D) */ @JvmName("times3") operator fun <A: Unit, B: Unit, C: Unit, D: Unit> UnitRatio<A, UnitProduct<B, C>>.times(other: D              ): UnitRatio<UnitProduct<A, D>, UnitProduct<B, C>> = numerator * other / denominator
/** (A / B) * (A / B) */  @JvmName("times1") operator fun <A: Unit, B: Unit>                   UnitRatio<A, B>.                times(other: UnitRatio<A, B>): UnitRatio<UnitProduct<A, A>, UnitProduct<B, B>> = numerator * other.numerator / (denominator * other.denominator)
/** (A / B) * (B / A)) */ @JvmName("times2") operator fun <A: Unit, B: Unit>                   UnitRatio<A, B>.                times(other: UnitRatio<B, A>): Double                                          = numerator * other.numerator / (denominator * other.denominator)
/** (A / B) * (C / D)) */ @JvmName("times3") operator fun <A: Unit, B: Unit, C: Unit, D: Unit> UnitRatio<A, B>.                times(other: UnitRatio<C, D>): UnitRatio<UnitProduct<A, C>, UnitProduct<B, D>> = numerator * other.numerator / (denominator * other.denominator)

@JvmName("div1")   operator fun <A: Unit, B: Unit>                   UnitRatio<A, B>.                                div(other: UnitRatio<A, B>): Double                                          = this * other.reciprocal
@JvmName("div2")   operator fun <A: Unit, B: Unit>                   UnitRatio<A, B>.                                div(other: UnitRatio<B, A>): UnitRatio<UnitProduct<A, A>, UnitProduct<B, B>> = this * other.reciprocal
@JvmName("div3")   operator fun <A: Unit, B: Unit, C: Unit, D: Unit> UnitRatio<A, B>.                                div(other: UnitRatio<C, D>): UnitRatio<UnitProduct<A, D>, UnitProduct<B, C>> = this * other.reciprocal
                   operator fun <A: Unit, B: Unit>                   UnitRatio<A, B>.                                div(other: B              ): UnitRatio<A, UnitProduct<B, B>>                 = numerator / (denominator * other)
@JvmName("div1")   operator fun <A: Unit, B: Unit>                   UnitRatio<UnitProduct<A, A>, UnitProduct<B, B>>.div(other: A              ): Measure<UnitRatio<A, UnitProduct<B, B>>>        = numerator.first.ratio / other.ratio * (numerator.second / denominator)
@JvmName("div2")   operator fun <A: Unit, B: Unit, C: Unit>          UnitRatio<UnitProduct<A, A>, UnitProduct<B, C>>.div(other: A              ): Measure<UnitRatio<A, UnitProduct<B, C>>>        = numerator.first.ratio / other.ratio * (numerator.second / denominator)
@JvmName("div3")   operator fun <A: Unit, B: Unit, C: Unit, D: Unit> UnitRatio<UnitProduct<A, B>, UnitProduct<C, D>>.div(other: A              ): Measure<UnitRatio<B, UnitProduct<C, D>>>        = numerator.first.ratio / other.ratio * (numerator.second / denominator)

// m/s * (s2/m) => s
operator fun <A: Unit, B: Unit> UnitRatio<A, B>.div(other: UnitRatio<A, Square<B>>): Measure<B> = numerator.ratio / other.numerator.ratio * (other.denominator / denominator)

@JvmName("times1") operator fun <A: Unit, B: Unit>                   Measure<A>.                              times(other: Measure<B>              ): Measure<UnitProduct<A, B>>                               = amount * other.amount * (unit * other.unit)
@JvmName("times2") operator fun <A: Unit, B: Unit>                   Measure<A>.                              times(other: Measure<UnitRatio<B, A>>): Measure<B>                                               = amount * other.amount * (unit * other.unit)
@JvmName("times7") operator fun <A: Unit, B: Unit>                   Measure<A>.                              times(other: Measure<InverseUnit<B>> ): Measure<UnitRatio<A, B>>                                 = amount * other.amount * (unit * other.unit)
@JvmName("times3") operator fun <A: Unit, B: Unit>                   Measure<UnitRatio<A, B>>.                times(other: Measure<B>              ): Measure<A>                                               = amount * other.amount * (unit * other.unit)
@JvmName("times4") operator fun <A: Unit, B: Unit>                   Measure<UnitRatio<A, B>>.                times(other: Measure<UnitRatio<A, B>>): Measure<UnitRatio<UnitProduct<A, A>, UnitProduct<B, B>>> = amount * other.amount * (unit * other.unit)
@JvmName("times5") operator fun <A: Unit, B: Unit, C: Unit>          Measure<UnitRatio<A, UnitProduct<B, C>>>.times(other: Measure<B>              ): Measure<UnitRatio<A, C>>                                 = amount * other.amount * (unit * other.unit)
@JvmName("times6") operator fun <A: Unit, B: Unit, C: Unit, D: Unit> Measure<UnitRatio<A, UnitProduct<B, C>>>.times(other: Measure<D>              ): Measure<UnitRatio<UnitProduct<A, D>, UnitProduct<B, C>>> = amount * other.amount * (unit * other.unit)

@JvmName("times1") operator fun <A: Unit, B: Unit>                   Measure<A>.                              times(other: B              ): Measure<UnitProduct<A, B>>                               = amount * (unit * other)
@JvmName("times2") operator fun <A: Unit, B: Unit>                   Measure<A>.                              times(other: UnitRatio<B, A>): Measure<B>                                               = amount * (unit * other)
@JvmName("times7") operator fun <A: Unit, B: Unit>                   Measure<A>.                              times(other: InverseUnit<B> ): Measure<UnitRatio<A, B>>                                 = amount * (unit * other)
@JvmName("times3") operator fun <A: Unit, B: Unit>                   Measure<UnitRatio<A, B>>.                times(other: B              ): Measure<A>                                               = amount * (unit * other)
@JvmName("times4") operator fun <A: Unit, B: Unit>                   Measure<UnitRatio<A, B>>.                times(other: UnitRatio<A, B>): Measure<UnitRatio<UnitProduct<A, A>, UnitProduct<B, B>>> = amount * (unit * other)
@JvmName("times5") operator fun <A: Unit, B: Unit, C: Unit>          Measure<UnitRatio<A, UnitProduct<B, C>>>.times(other: B              ): Measure<UnitRatio<A, C>>                                 = amount * (unit * other)
@JvmName("times6") operator fun <A: Unit, B: Unit, C: Unit, D: Unit> Measure<UnitRatio<A, UnitProduct<B, C>>>.times(other: D              ): Measure<UnitRatio<UnitProduct<A, D>, UnitProduct<B, C>>> = amount * (unit * other)

// TODO: Kapt code generation possible?
operator fun <A: Unit> Measure<A>.rem(other: Measure<A>): Double = amount % other.amount * (unit.ratio % other.unit.ratio)

@JvmName("div16") operator fun <A: Unit>                   Measure<A>.                div(other: Measure<A>                ): Double                   = amount / other.amount * (unit.ratio / other.unit.ratio)
@JvmName("div16") operator fun <A: Unit, B: Unit>          Measure<A>.                div(other: Measure<B>                ): Measure<UnitRatio<A, B>> = amount / other.amount * (unit / other.unit)
@JvmName("div1" ) operator fun <A: Unit, B: Unit>          Measure<UnitProduct<A, B>>.div(other: Measure<A>                ): Measure<B>               = amount / other.amount * (unit / other.unit)
@JvmName("div2" ) operator fun <A: Unit, B: Unit>          Measure<UnitProduct<A, B>>.div(other: Measure<B>                ): Measure<A>               = amount / other.amount * (unit / other.unit)
@JvmName("div3" ) operator fun <A: Unit, B: Unit, C: Unit> Measure<UnitProduct<A, B>>.div(other: Measure<UnitProduct<C, B>>): Measure<UnitRatio<A, C>> = amount / other.amount * (unit / other.unit)
@JvmName("div4" ) operator fun <A: Unit, B: Unit, C: Unit> Measure<UnitProduct<A, B>>.div(other: Measure<UnitProduct<C, A>>): Measure<UnitRatio<B, C>> = amount / other.amount * (unit / other.unit)
@JvmName("div5" ) operator fun <A: Unit, B: Unit, C: Unit> Measure<UnitProduct<A, B>>.div(other: Measure<UnitProduct<B, C>>): Measure<UnitRatio<A, C>> = amount / other.amount * (unit / other.unit)
@JvmName("div6" ) operator fun <A: Unit, B: Unit, C: Unit> Measure<UnitProduct<A, B>>.div(other: Measure<UnitProduct<A, C>>): Measure<UnitRatio<B, C>> = amount / other.amount * (unit / other.unit)
@JvmName("div7" ) operator fun <A: Unit, B: Unit>          Measure<UnitProduct<A, B>>.div(other: Measure<UnitProduct<A, A>>): Measure<UnitRatio<B, A>> = amount / other.amount * (unit / other.unit)
@JvmName("div8" ) operator fun <A: Unit, B: Unit>          Measure<UnitProduct<A, B>>.div(other: Measure<UnitProduct<B, B>>): Measure<UnitRatio<A, B>> = amount / other.amount * (unit / other.unit)

@JvmName("div16") operator fun <A: Unit>                   Measure<A>.                div(other: A                ): Double                   = amount * (unit.ratio / other.ratio)
@JvmName("div16") operator fun <A: Unit, B: Unit>          Measure<A>.                div(other: B                ): Measure<UnitRatio<A, B>> = amount * (unit / other)
@JvmName("div1" ) operator fun <A: Unit, B: Unit>          Measure<UnitProduct<A, B>>.div(other: A                ): Measure<B>               = amount * (unit / other)
@JvmName("div2" ) operator fun <A: Unit, B: Unit>          Measure<UnitProduct<A, B>>.div(other: B                ): Measure<A>               = amount * (unit / other)
@JvmName("div3" ) operator fun <A: Unit, B: Unit, C: Unit> Measure<UnitProduct<A, B>>.div(other: UnitProduct<C, B>): Measure<UnitRatio<A, C>> = amount * (unit / other)
@JvmName("div4" ) operator fun <A: Unit, B: Unit, C: Unit> Measure<UnitProduct<A, B>>.div(other: UnitProduct<C, A>): Measure<UnitRatio<B, C>> = amount * (unit / other)
@JvmName("div5" ) operator fun <A: Unit, B: Unit, C: Unit> Measure<UnitProduct<A, B>>.div(other: UnitProduct<B, C>): Measure<UnitRatio<A, C>> = amount * (unit / other)
@JvmName("div6" ) operator fun <A: Unit, B: Unit, C: Unit> Measure<UnitProduct<A, B>>.div(other: UnitProduct<A, C>): Measure<UnitRatio<B, C>> = amount * (unit / other)
@JvmName("div7" ) operator fun <A: Unit, B: Unit>          Measure<UnitProduct<A, B>>.div(other: UnitProduct<A, A>): Measure<UnitRatio<B, A>> = amount * (unit / other)
@JvmName("div8" ) operator fun <A: Unit, B: Unit>          Measure<UnitProduct<A, B>>.div(other: UnitProduct<B, B>): Measure<UnitRatio<A, B>> = amount * (unit / other)

@JvmName("div9" ) operator fun <A: Unit, B: Unit>                   Measure<UnitRatio<A, B>>.                                div(other: Measure<B>                      ): Measure<UnitRatio<A, UnitProduct<B, B>>>                 = amount / other.amount * (unit / other.unit)
@JvmName("div10") operator fun <A: Unit, B: Unit, C: Unit, D: Unit> Measure<UnitRatio<A, B>>.                                div(other: Measure<UnitRatio<C, D>>        ): Measure<UnitRatio<UnitProduct<A, D>, UnitProduct<B, C>>> = amount / other.amount * (unit / other.unit)
@JvmName("div11") operator fun <A: Unit, B: Unit>                   Measure<UnitRatio<A, B>>.                                div(other: Measure<UnitRatio<A, Square<B>>>): Measure<B>                                               = amount / other.amount * (unit / other.unit)
@JvmName("div12") operator fun <A: Unit, B: Unit>                   Measure<UnitRatio<UnitProduct<A, A>, UnitProduct<B, B>>>.div(other: Measure<A>                      ): Measure<UnitRatio<A, UnitProduct<B, B>>>                 = amount / other.amount * (unit / other.unit)
@JvmName("div13") operator fun <A: Unit, B: Unit, C: Unit>          Measure<UnitRatio<UnitProduct<A, A>, UnitProduct<B, C>>>.div(other: Measure<A>                      ): Measure<UnitRatio<A, UnitProduct<B, C>>>                 = amount / other.amount * (unit / other.unit)
@JvmName("div14") operator fun <A: Unit, B: Unit, C: Unit, D: Unit> Measure<UnitRatio<UnitProduct<A, B>, UnitProduct<C, D>>>.div(other: Measure<A>                      ): Measure<UnitRatio<B, UnitProduct<C, D>>>                 = amount / other.amount * (unit / other.unit)
@JvmName("div15") operator fun <A: Unit, B: Unit>                   Measure<A>.                                              div(other: Measure<UnitRatio<A, B>>        ): Measure<B>                                               = amount / other.amount * (unit / other.unit)

@JvmName("div9" ) operator fun <A: Unit, B: Unit>                   Measure<UnitRatio<A, B>>.                                div(other: B                      ): Measure<UnitRatio<A, UnitProduct<B, B>>>                 = amount * (unit / other)
@JvmName("div10") operator fun <A: Unit, B: Unit, C: Unit, D: Unit> Measure<UnitRatio<A, B>>.                                div(other: UnitRatio<C, D>        ): Measure<UnitRatio<UnitProduct<A, D>, UnitProduct<B, C>>> = amount * (unit / other)
@JvmName("div11") operator fun <A: Unit, B: Unit>                   Measure<UnitRatio<A, B>>.                                div(other: UnitRatio<A, Square<B>>): Measure<B>                                               = amount * (unit / other)
@JvmName("div12") operator fun <A: Unit, B: Unit>                   Measure<UnitRatio<UnitProduct<A, A>, UnitProduct<B, B>>>.div(other: A                      ): Measure<UnitRatio<A, UnitProduct<B, B>>>                 = amount * (unit / other)
@JvmName("div13") operator fun <A: Unit, B: Unit, C: Unit>          Measure<UnitRatio<UnitProduct<A, A>, UnitProduct<B, C>>>.div(other: A                      ): Measure<UnitRatio<A, UnitProduct<B, C>>>                 = amount * (unit / other)
@JvmName("div14") operator fun <A: Unit, B: Unit, C: Unit, D: Unit> Measure<UnitRatio<UnitProduct<A, B>, UnitProduct<C, D>>>.div(other: A                      ): Measure<UnitRatio<B, UnitProduct<C, D>>>                 = amount * (unit / other)
@JvmName("div15") operator fun <A: Unit, B: Unit>                   Measure<A>.                                              div(other: UnitRatio<A, B>        ): Measure<B>                                               = amount * (unit / other)


// Helpers for converting numbers into measures

private infix fun <T: Unit> Number.into(unit: T): Measure<T> = Measure(this.toDouble(), unit)

operator fun <T: Unit> Number.times(unit: T): Measure<T>              = this into unit
operator fun <T: Unit> Number.div  (unit: T): Measure<InverseUnit<T>> = this into InverseUnit(unit)

operator fun <T: Unit> T.times (value: Number): Measure<T> = value into this
operator fun <T: Unit> T.invoke(value: Number): Measure<T> = value into this

fun <T: Unit> abs(value: Measure<T>) = kotlin.math.abs(value.amount) * value.unit

operator fun <T: Unit> Number.times(measure: Measure<T>): Measure<T> = measure * this

typealias Velocity     = UnitRatio<Length, Time>
typealias Acceleration = UnitRatio<Length, Square<Time>>