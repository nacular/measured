package io.nacular.measured.units

import kotlin.jvm.JvmName
import kotlin.math.roundToInt

/**
 * Base class for all types that can represent a unit of measure.
 * A Units type can have multiple "members", each being some fraction of the base
 * unit for that type. Time for example, might have seconds as the base unit
 * and minute as a unit that is 60 times the base unit. This allows for a
 * set of different representations of the unit.
 *
 * @constructor
 * @param suffix to use when printing the unit in a human readable way
 * @param ratio of this unit relative to the base-unit
 */
abstract class Units(val suffix: String, val ratio: Double = 1.0) {
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
        if (other !is Units) return false

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
class UnitsProduct<A: Units, B: Units>(val first: A, val second: B): Units(if (first==second) "($first)^2" else "$first$second", first.ratio * second.ratio)

typealias Square<T> = UnitsProduct<T, T>

/**
 * Represents the ratio of two Units: A / B.
 *
 * @constructor
 * @param numerator unit being divided
 * @param denominator unit dividing numerator
 */
class UnitsRatio<A: Units, B: Units>(val numerator: A, val denominator: B): Units("$numerator/$denominator", numerator.ratio / denominator.ratio) {
    /** The Inverse of this unit. */
    val reciprocal by lazy { UnitsRatio(denominator, numerator) }
}

/**
 * The inverse of a given Units.
 *
 * @constructor
 * @param unit this is the inverse of
 */
class InverseUnits<T: Units>(val unit: T): Units("1/${unit.suffix}", 1 / unit.ratio)

/**
 * Compares two units
 * @param other unit to compare
 * @return -1 if this unit is smaller, 1 if the other is smaller, and 0 if they are equal
 */
operator fun <A: Units, B: A> A.compareTo(other: B): Int = ratio.compareTo(other.ratio)

/**
 * @return the smaller of the two Units
 */
fun <A: Units, B: A> minOf(first: A, second: B) = if (first < second) first else second


/**
 * A quantity with a unit type
 */
class Measure<T: Units>(val amount: Double, val units: T): Comparable<Measure<T>> {
    /**
     * Convert this type into another compatible type.
     * Type must share parent
     * (eg Mile into Kilometer, because they both are made from Distance)
     */
    infix fun <A: T> `as`(other: A): Measure<out T> = if (units == other) this else Measure(this `in` other, other)

    infix fun <A: T> `in`(other: A): Double = if (units == other) amount else  amount * (units.ratio / other.ratio)

    /**
     * Add another compatible quantity to this one
     */
    operator fun plus(other: Measure<T>): Measure<T> = minOf(units, other.units).let { Measure((this `in` it) + (other `in` it), it) }

    /**
     * Subtract a compatible quantity from this one
     */
    operator fun minus(other: Measure<T>): Measure<T> = minOf(units, other.units).let { Measure((this `in` it) - (other `in` it), it) }

    operator fun unaryMinus(): Measure<T> = Measure(-amount, units)

    /**
     * Multiply this by a scalar value, used for things like "double this distance",
     * "1.5 times the speed", etc
     */
    operator fun times(other: Number): Measure<T> = amount * other.toDouble() * units

    /**
     * Divide this by a scalar, used for things like "halve the speed"
     */
    operator fun div(other: Number): Measure<T> = amount / other.toDouble() * units

    fun roundToInt(): Measure<T> = amount.roundToInt() * units

    /**
     * Compare this value with another quantity - which must have the same type
     * Units are converted before comparison
     */
    override fun compareTo(other: Measure<T>): Int = (this `as` other.units).amount.compareTo(other.amount)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Measure<*>) return false
//        if (this.amount == 0.0 && other.amount == 0.0) return true TODO: Should this be true?

        val resultUnit = minOf(units, (other as Measure<T>).units)

        val a = this  `in` resultUnit
        val b = other `in` resultUnit

        return a == b
    }

    override fun hashCode(): Int {
        return (amount * units.ratio).hashCode()
    }

    override fun toString(): String = "$amount${units.measureSuffix()}"
}

/** A * B */                              operator fun <A: Units, B: Units> A.times(other: B               ): UnitsProduct<A, B> = UnitsProduct(this, other)
/** A * (1 / B) */                        operator fun <A: Units, B: Units> A.times(other: InverseUnits<B> ): UnitsRatio<A, B>   = this / other.unit
/** A * (B / A) */                        operator fun <A: Units, B: Units> A.times(other: UnitsRatio<B, A>): Measure<B>         = this.ratio / other.denominator.ratio * other.numerator
// This cannot be defined given the next definition unfortunately
//operator fun <A: Units> A.div(other: A) = this.ratio / other.ratio
/** A / B */                              operator fun <A: Units, B: Units> A.div  (other: B               ): UnitsRatio<A, B>   = UnitsRatio(this, other)
/** A / (A / B) == A * (B / A) */         operator fun <A: Units, B: Units> A.div  (other: UnitsRatio<A, B>): Measure<B>         = this * other.reciprocal

/** (A * B) / A */       @JvmName("div1") operator fun <A: Units, B: Units>           UnitsProduct<A, B>.div(other: A                 ): Measure<B>                = first.ratio  / other.ratio * second
/** (A * B) / B */       @JvmName("div2") operator fun <A: Units, B: Units>           UnitsProduct<A, B>.div(other: B                 ): Measure<A>                = second.ratio / other.ratio * first
/** (A * A) / A */       @JvmName("div3") operator fun <A: Units>                     UnitsProduct<A, A>.div(other: A                 ): Measure<A>                = first.ratio  / other.ratio * second
/** (A * B) / (C * B) */ @JvmName("div1") operator fun <A: Units, B: Units, C: Units> UnitsProduct<A, B>.div(other: UnitsProduct<C, B>): UnitsRatio<A, C>          = first  / other.first
/** (A * B) / (C * A) */ @JvmName("div2") operator fun <A: Units, B: Units, C: Units> UnitsProduct<A, B>.div(other: UnitsProduct<C, A>): UnitsRatio<B, C>          = second / other.first
/** (A * B) / (B * C) */ @JvmName("div3") operator fun <A: Units, B: Units, C: Units> UnitsProduct<A, B>.div(other: UnitsProduct<B, C>): UnitsRatio<A, C>          = first  / other.second
/** (A * B) / (A * C) */ @JvmName("div4") operator fun <A: Units, B: Units, C: Units> UnitsProduct<A, B>.div(other: UnitsProduct<A, C>): UnitsRatio<B, C>          = second / other.second
/** (A * B) / (A * A) */ @JvmName("div5") operator fun <A: Units, B: Units>           UnitsProduct<A, B>.div(other: UnitsProduct<A, A>): UnitsRatio<B, A>          = second / other.second
/** (A * B) / (A * B) */ @JvmName("div7") operator fun <A: Units, B: Units>           UnitsProduct<A, B>.div(other: UnitsProduct<A, B>): Double                    = ratio  / other.ratio
/** (A * B) / (B * A) */ @JvmName("div6") operator fun <A: Units, B: Units>           UnitsProduct<A, B>.div(other: UnitsProduct<B, A>): Double                    = ratio  / other.ratio
/** (A * B) / (B * B) */ @JvmName("div8") operator fun <A: Units, B: Units>           UnitsProduct<A, B>.div(other: UnitsProduct<B, B>): Measure<UnitsRatio<A, B>> = second.ratio / other.second.ratio * (first / other.first)

/** (A / B) * B */        @JvmName("times1") operator fun <A: Units, B: Units>                     UnitsRatio<A, B>.                 times(other: B               ): Measure<A>                                         = other.ratio / denominator.ratio * numerator
/** (A / (B * C)) * B */  @JvmName("times2") operator fun <A: Units, B: Units, C: Units>           UnitsRatio<A, UnitsProduct<B, C>>.times(other: B               ): Measure<UnitsRatio<A, C>>                          = other.ratio / denominator.first.ratio * (numerator / denominator.second)
/** (A / (B * C)) * D) */ @JvmName("times3") operator fun <A: Units, B: Units, C: Units, D: Units> UnitsRatio<A, UnitsProduct<B, C>>.times(other: D               ): UnitsRatio<UnitsProduct<A, D>, UnitsProduct<B, C>> = numerator * other / denominator
/** (A / B) * (A / B) */  @JvmName("times1") operator fun <A: Units, B: Units>                     UnitsRatio<A, B>.                 times(other: UnitsRatio<A, B>): UnitsRatio<UnitsProduct<A, A>, UnitsProduct<B, B>> = numerator * other.numerator / (denominator * other.denominator)
/** (A / B) * (B / A)) */ @JvmName("times2") operator fun <A: Units, B: Units>                     UnitsRatio<A, B>.                 times(other: UnitsRatio<B, A>): Double                                             = numerator * other.numerator / (denominator * other.denominator)
/** (A / B) * (C / D)) */ @JvmName("times3") operator fun <A: Units, B: Units, C: Units, D: Units> UnitsRatio<A, B>.                 times(other: UnitsRatio<C, D>): UnitsRatio<UnitsProduct<A, C>, UnitsProduct<B, D>> = numerator * other.numerator / (denominator * other.denominator)

@JvmName("div1")   operator fun <A: Units, B: Units>                     UnitsRatio<A, B>.                                  div(other: UnitsRatio<A, B>): Double                                             = this * other.reciprocal
@JvmName("div2")   operator fun <A: Units, B: Units>                     UnitsRatio<A, B>.                                  div(other: UnitsRatio<B, A>): UnitsRatio<UnitsProduct<A, A>, UnitsProduct<B, B>> = this * other.reciprocal
@JvmName("div3")   operator fun <A: Units, B: Units, C: Units, D: Units> UnitsRatio<A, B>.                                  div(other: UnitsRatio<C, D>): UnitsRatio<UnitsProduct<A, D>, UnitsProduct<B, C>> = this * other.reciprocal
                   operator fun <A: Units, B: Units>                     UnitsRatio<A, B>.                                  div(other: B               ): UnitsRatio<A, UnitsProduct<B, B>>                  = numerator / (denominator * other)
@JvmName("div1")   operator fun <A: Units, B: Units>                     UnitsRatio<UnitsProduct<A, A>, UnitsProduct<B, B>>.div(other: A               ): Measure<UnitsRatio<A, UnitsProduct<B, B>>>         = numerator.first.ratio / other.ratio * (numerator.second / denominator)
@JvmName("div2")   operator fun <A: Units, B: Units, C: Units>           UnitsRatio<UnitsProduct<A, A>, UnitsProduct<B, C>>.div(other: A               ): Measure<UnitsRatio<A, UnitsProduct<B, C>>>         = numerator.first.ratio / other.ratio * (numerator.second / denominator)
@JvmName("div3")   operator fun <A: Units, B: Units, C: Units, D: Units> UnitsRatio<UnitsProduct<A, B>, UnitsProduct<C, D>>.div(other: A               ): Measure<UnitsRatio<B, UnitsProduct<C, D>>>         = numerator.first.ratio / other.ratio * (numerator.second / denominator)

// m/s * (s2/m) => s
operator fun <A: Units, B: Units> UnitsRatio<A, B>.div(other: UnitsRatio<A, Square<B>>): Measure<B> = numerator.ratio / other.numerator.ratio * (other.denominator / denominator)

@JvmName("times1") operator fun <A: Units, B: Units>                     Measure<A>.                                times(other: Measure<B>               ): Measure<UnitsProduct<A, B>>                                 = amount * other.amount * (units * other.units)
@JvmName("times2") operator fun <A: Units, B: Units>                     Measure<A>.                                times(other: Measure<UnitsRatio<B, A>>): Measure<B>                                                  = amount * other.amount * (units * other.units)
@JvmName("times7") operator fun <A: Units, B: Units>                     Measure<A>.                                times(other: Measure<InverseUnits<B>> ): Measure<UnitsRatio<A, B>>                                   = amount * other.amount * (units * other.units)
@JvmName("times3") operator fun <A: Units, B: Units>                     Measure<UnitsRatio<A, B>>.                 times(other: Measure<B>               ): Measure<A>                                                  = amount * other.amount * (units * other.units)
@JvmName("times4") operator fun <A: Units, B: Units>                     Measure<UnitsRatio<A, B>>.                 times(other: Measure<UnitsRatio<A, B>>): Measure<UnitsRatio<UnitsProduct<A, A>, UnitsProduct<B, B>>> = amount * other.amount * (units * other.units)
@JvmName("times5") operator fun <A: Units, B: Units, C: Units>           Measure<UnitsRatio<A, UnitsProduct<B, C>>>.times(other: Measure<B>               ): Measure<UnitsRatio<A, C>>                                   = amount * other.amount * (units * other.units)
@JvmName("times6") operator fun <A: Units, B: Units, C: Units, D: Units> Measure<UnitsRatio<A, UnitsProduct<B, C>>>.times(other: Measure<D>               ): Measure<UnitsRatio<UnitsProduct<A, D>, UnitsProduct<B, C>>> = amount * other.amount * (units * other.units)

@JvmName("times1") operator fun <A: Units, B: Units>                     Measure<A>.                                times(other: B               ): Measure<UnitsProduct<A, B>>                                 = amount * (units * other)
@JvmName("times2") operator fun <A: Units, B: Units>                     Measure<A>.                                times(other: UnitsRatio<B, A>): Measure<B>                                                  = amount * (units * other)
@JvmName("times7") operator fun <A: Units, B: Units>                     Measure<A>.                                times(other: InverseUnits<B> ): Measure<UnitsRatio<A, B>>                                   = amount * (units * other)
@JvmName("times3") operator fun <A: Units, B: Units>                     Measure<UnitsRatio<A, B>>.                 times(other: B               ): Measure<A>                                                  = amount * (units * other)
@JvmName("times4") operator fun <A: Units, B: Units>                     Measure<UnitsRatio<A, B>>.                 times(other: UnitsRatio<A, B>): Measure<UnitsRatio<UnitsProduct<A, A>, UnitsProduct<B, B>>> = amount * (units * other)
@JvmName("times5") operator fun <A: Units, B: Units, C: Units>           Measure<UnitsRatio<A, UnitsProduct<B, C>>>.times(other: B               ): Measure<UnitsRatio<A, C>>                                   = amount * (units * other)
@JvmName("times6") operator fun <A: Units, B: Units, C: Units, D: Units> Measure<UnitsRatio<A, UnitsProduct<B, C>>>.times(other: D               ): Measure<UnitsRatio<UnitsProduct<A, D>, UnitsProduct<B, C>>> = amount * (units * other)

// TODO: Kapt code generation possible?
operator fun <A: Units> Measure<A>.rem(other: Measure<A>): Double = amount % other.amount * (units.ratio % other.units.ratio)

@JvmName("div16") operator fun <A: Units>                     Measure<A>.                 div(other: Measure<A>                 ): Double                    = amount / other.amount * (units.ratio / other.units.ratio)
@JvmName("div16") operator fun <A: Units, B: Units>           Measure<A>.                 div(other: Measure<B>                 ): Measure<UnitsRatio<A, B>> = amount / other.amount * (units / other.units)
@JvmName("div1" ) operator fun <A: Units, B: Units>           Measure<UnitsProduct<A, B>>.div(other: Measure<A>                 ): Measure<B>                = amount / other.amount * (units / other.units)
@JvmName("div2" ) operator fun <A: Units, B: Units>           Measure<UnitsProduct<A, B>>.div(other: Measure<B>                 ): Measure<A>                = amount / other.amount * (units / other.units)
@JvmName("div3" ) operator fun <A: Units, B: Units, C: Units> Measure<UnitsProduct<A, B>>.div(other: Measure<UnitsProduct<C, B>>): Measure<UnitsRatio<A, C>> = amount / other.amount * (units / other.units)
@JvmName("div4" ) operator fun <A: Units, B: Units, C: Units> Measure<UnitsProduct<A, B>>.div(other: Measure<UnitsProduct<C, A>>): Measure<UnitsRatio<B, C>> = amount / other.amount * (units / other.units)
@JvmName("div5" ) operator fun <A: Units, B: Units, C: Units> Measure<UnitsProduct<A, B>>.div(other: Measure<UnitsProduct<B, C>>): Measure<UnitsRatio<A, C>> = amount / other.amount * (units / other.units)
@JvmName("div6" ) operator fun <A: Units, B: Units, C: Units> Measure<UnitsProduct<A, B>>.div(other: Measure<UnitsProduct<A, C>>): Measure<UnitsRatio<B, C>> = amount / other.amount * (units / other.units)
@JvmName("div7" ) operator fun <A: Units, B: Units>           Measure<UnitsProduct<A, B>>.div(other: Measure<UnitsProduct<A, A>>): Measure<UnitsRatio<B, A>> = amount / other.amount * (units / other.units)
@JvmName("div8" ) operator fun <A: Units, B: Units>           Measure<UnitsProduct<A, B>>.div(other: Measure<UnitsProduct<B, B>>): Measure<UnitsRatio<A, B>> = amount / other.amount * (units / other.units)

@JvmName("div16") operator fun <A: Units>                     Measure<A>.                 div(other: A                 ): Double                    = amount * (units.ratio / other.ratio)
@JvmName("div16") operator fun <A: Units, B: Units>           Measure<A>.                 div(other: B                 ): Measure<UnitsRatio<A, B>> = amount * (units / other)
@JvmName("div1" ) operator fun <A: Units, B: Units>           Measure<UnitsProduct<A, B>>.div(other: A                 ): Measure<B>                = amount * (units / other)
@JvmName("div2" ) operator fun <A: Units, B: Units>           Measure<UnitsProduct<A, B>>.div(other: B                 ): Measure<A>                = amount * (units / other)
@JvmName("div3" ) operator fun <A: Units, B: Units, C: Units> Measure<UnitsProduct<A, B>>.div(other: UnitsProduct<C, B>): Measure<UnitsRatio<A, C>> = amount * (units / other)
@JvmName("div4" ) operator fun <A: Units, B: Units, C: Units> Measure<UnitsProduct<A, B>>.div(other: UnitsProduct<C, A>): Measure<UnitsRatio<B, C>> = amount * (units / other)
@JvmName("div5" ) operator fun <A: Units, B: Units, C: Units> Measure<UnitsProduct<A, B>>.div(other: UnitsProduct<B, C>): Measure<UnitsRatio<A, C>> = amount * (units / other)
@JvmName("div6" ) operator fun <A: Units, B: Units, C: Units> Measure<UnitsProduct<A, B>>.div(other: UnitsProduct<A, C>): Measure<UnitsRatio<B, C>> = amount * (units / other)
@JvmName("div7" ) operator fun <A: Units, B: Units>           Measure<UnitsProduct<A, B>>.div(other: UnitsProduct<A, A>): Measure<UnitsRatio<B, A>> = amount * (units / other)
@JvmName("div8" ) operator fun <A: Units, B: Units>           Measure<UnitsProduct<A, B>>.div(other: UnitsProduct<B, B>): Measure<UnitsRatio<A, B>> = amount * (units / other)

@JvmName("div9" ) operator fun <A: Units, B: Units>                     Measure<UnitsRatio<A, B>>.                                  div(other: Measure<B>                       ): Measure<UnitsRatio<A, UnitsProduct<B, B>>>                  = amount / other.amount * (units / other.units)
@JvmName("div10") operator fun <A: Units, B: Units, C: Units, D: Units> Measure<UnitsRatio<A, B>>.                                  div(other: Measure<UnitsRatio<C, D>>        ): Measure<UnitsRatio<UnitsProduct<A, D>, UnitsProduct<B, C>>> = amount / other.amount * (units / other.units)
@JvmName("div11") operator fun <A: Units, B: Units>                     Measure<UnitsRatio<A, B>>.                                  div(other: Measure<UnitsRatio<A, Square<B>>>): Measure<B>                                                  = amount / other.amount * (units / other.units)
@JvmName("div12") operator fun <A: Units, B: Units>                     Measure<UnitsRatio<UnitsProduct<A, A>, UnitsProduct<B, B>>>.div(other: Measure<A>                       ): Measure<UnitsRatio<A, UnitsProduct<B, B>>>                  = amount / other.amount * (units / other.units)
@JvmName("div13") operator fun <A: Units, B: Units, C: Units>           Measure<UnitsRatio<UnitsProduct<A, A>, UnitsProduct<B, C>>>.div(other: Measure<A>                       ): Measure<UnitsRatio<A, UnitsProduct<B, C>>>                  = amount / other.amount * (units / other.units)
@JvmName("div14") operator fun <A: Units, B: Units, C: Units, D: Units> Measure<UnitsRatio<UnitsProduct<A, B>, UnitsProduct<C, D>>>.div(other: Measure<A>                       ): Measure<UnitsRatio<B, UnitsProduct<C, D>>>                  = amount / other.amount * (units / other.units)
@JvmName("div15") operator fun <A: Units, B: Units>                     Measure<A>.                                                 div(other: Measure<UnitsRatio<A, B>>        ): Measure<B>                                                  = amount / other.amount * (units / other.units)

@JvmName("div9" ) operator fun <A: Units, B: Units>                     Measure<UnitsRatio<A, B>>.                                  div(other: B                       ): Measure<UnitsRatio<A, UnitsProduct<B, B>>>                  = amount * (units / other)
@JvmName("div10") operator fun <A: Units, B: Units, C: Units, D: Units> Measure<UnitsRatio<A, B>>.                                  div(other: UnitsRatio<C, D>        ): Measure<UnitsRatio<UnitsProduct<A, D>, UnitsProduct<B, C>>> = amount * (units / other)
@JvmName("div11") operator fun <A: Units, B: Units>                     Measure<UnitsRatio<A, B>>.                                  div(other: UnitsRatio<A, Square<B>>): Measure<B>                                                  = amount * (units / other)
@JvmName("div12") operator fun <A: Units, B: Units>                     Measure<UnitsRatio<UnitsProduct<A, A>, UnitsProduct<B, B>>>.div(other: A                       ): Measure<UnitsRatio<A, UnitsProduct<B, B>>>                  = amount * (units / other)
@JvmName("div13") operator fun <A: Units, B: Units, C: Units>           Measure<UnitsRatio<UnitsProduct<A, A>, UnitsProduct<B, C>>>.div(other: A                       ): Measure<UnitsRatio<A, UnitsProduct<B, C>>>                  = amount * (units / other)
@JvmName("div14") operator fun <A: Units, B: Units, C: Units, D: Units> Measure<UnitsRatio<UnitsProduct<A, B>, UnitsProduct<C, D>>>.div(other: A                       ): Measure<UnitsRatio<B, UnitsProduct<C, D>>>                  = amount * (units / other)
@JvmName("div15") operator fun <A: Units, B: Units>                     Measure<A>.                                                 div(other: UnitsRatio<A, B>        ): Measure<B>                                                  = amount * (units / other)


// Helpers for converting numbers into measures

private infix fun <T: Units> Number.into(unit: T): Measure<T> = Measure(this.toDouble(), unit)

operator fun <T: Units> Number.times(unit: T): Measure<T>               = this into unit
operator fun <T: Units> Number.div  (unit: T): Measure<InverseUnits<T>> = this into InverseUnits(unit)

operator fun <T: Units> T.times (value: Number): Measure<T> = value into this
operator fun <T: Units> T.invoke(value: Number): Measure<T> = value into this

fun <T: Units> abs(value: Measure<T>) = kotlin.math.abs(value.amount) * value.units

operator fun <T: Units> Number.times(measure: Measure<T>): Measure<T> = measure * this

typealias Velocity     = UnitsRatio<Length, Time>
typealias Acceleration = UnitsRatio<Length, Square<Time>>