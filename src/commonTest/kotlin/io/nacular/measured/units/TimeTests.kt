package io.nacular.measured.units

import io.nacular.measured.units.Time.Companion.hours
import io.nacular.measured.units.Time.Companion.milliseconds
import io.nacular.measured.units.Time.Companion.minutes
import io.nacular.measured.units.Time.Companion.seconds
import kotlin.js.JsName
import kotlin.test.Test
import kotlin.test.expect
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Created by Nicholas Eddy on 2/22/18.
 */
class TimeTests {
//      ms      s  min hr
//  ms  1  1/1000
//   s
// min
//  hr

    @Test @JsName("combinationsWork")
    fun `combinations work`() {
        testUnit(
            mapOf(
                milliseconds to mapOf(1.0        to milliseconds, 1.0 / 1000 to seconds, 1.0 / 60_000 to minutes, 1.0 / 3600_000 to hours),
                seconds      to mapOf(1000.0     to milliseconds, 1.0        to seconds, 1.0 / 60     to minutes, 1.0 / 3600     to hours),
                minutes      to mapOf(60_000.0   to milliseconds, 60.0       to seconds, 1.0          to minutes, 1.0 / 60       to hours),
                hours        to mapOf(3600_000.0 to milliseconds, 3600.0     to seconds, 60.0         to minutes, 1.0            to hours)
            )
        )
    }

    @Test @JsName("millisecondsConstants")
    fun `milliseconds constants`() {
        expect(10   * milliseconds) { 10   * milliseconds }
        expect(10.0 * milliseconds) { 10.0 * milliseconds }
        expect(10f  * milliseconds) { 10f  * milliseconds }
        expect(10L  * milliseconds) { 10L  * milliseconds }
    }

    @Test @JsName("secondsConstants")
    fun `seconds constants`() {
        expect(10   * seconds) { 10   * seconds }
        expect(10.0 * seconds) { 10.0 * seconds }
        expect(10f  * seconds) { 10f  * seconds }
        expect(10L  * seconds) { 10L  * seconds }
    }

    @Test @JsName("minutesConstants")
    fun `minutes constants`() {
        expect(10   * minutes) { 10   * minutes }
        expect(10.0 * minutes) { 10.0 * minutes }
        expect(10f  * minutes) { 10f  * minutes }
        expect(10L  * minutes) { 10L  * minutes }
    }

    @Test @JsName("hoursConstants")
    fun `hours constants`() {
        expect(10   * hours) { 10   * hours }
        expect(10.0 * hours) { 10.0 * hours }
        expect(10f  * hours) { 10f  * hours }
        expect(10L  * hours) { 10L  * hours }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Suffixes
    // ─────────────────────────────────────────────────────────────────────────

    @Test @JsName("millisecondsSuffixIsMs")
    fun `milliseconds suffix is ms`() = assertEquals("ms", milliseconds.suffix)

    @Test @JsName("secondsSuffixIsS")
    fun `seconds suffix is s`() = assertEquals("s", seconds.suffix)

    @Test @JsName("minutesSuffixIsMin")
    fun `minutes suffix is min`() = assertEquals("min", minutes.suffix)

    @Test @JsName("hoursSuffixIsHr")
    fun `hours suffix is hr`() = assertEquals("hr", hours.suffix)

    // ─────────────────────────────────────────────────────────────────────────
    // Base ratios (all stored relative to milliseconds as the base unit)
    // ─────────────────────────────────────────────────────────────────────────

    @Test @JsName("millisecondsRatioIs1")
    fun `milliseconds ratio is 1`() = assertApprox(1.0, milliseconds.ratio)

    @Test @JsName("secondsRatioIs1000")
    fun `seconds ratio is 1000 milliseconds`() = assertApprox(1_000.0, seconds.ratio)

    @Test @JsName("minutesRatioIs60000")
    fun `minutes ratio is 60 000 milliseconds`() = assertApprox(60_000.0, minutes.ratio)

    @Test @JsName("hoursRatioIs3600000")
    fun `hours ratio is 3 600 000 milliseconds`() = assertApprox(3_600_000.0, hours.ratio)

    // ─────────────────────────────────────────────────────────────────────────
    // div operator – unit-to-unit ratio
    // ─────────────────────────────────────────────────────────────────────────

    @Test @JsName("secondsDivMilliseconds")
    fun `seconds div milliseconds equals 1000`() = assertApprox(1_000.0, seconds / milliseconds)

    @Test @JsName("millisecondsDivSeconds")
    fun `milliseconds div seconds equals 1 over 1000`() = assertApprox(1.0 / 1_000.0, milliseconds / seconds)

    @Test @JsName("minutesDivSeconds")
    fun `minutes div seconds equals 60`() = assertApprox(60.0, minutes / seconds)

    @Test @JsName("hoursDivMinutes")
    fun `hours div minutes equals 60`() = assertApprox(60.0, hours / minutes)

    @Test @JsName("hoursDivSeconds")
    fun `hours div seconds equals 3600`() = assertApprox(3_600.0, hours / seconds)

    @Test @JsName("hoursDivMilliseconds")
    fun `hours div milliseconds equals 3 600 000`() = assertApprox(3_600_000.0, hours / milliseconds)

    @Test @JsName("samUnitDivIsOne")
    fun `any unit divided by itself is 1`() {
        assertApprox(1.0, milliseconds / milliseconds, "ms / ms"  )
        assertApprox(1.0, seconds      / seconds,      "s / s"    )
        assertApprox(1.0, minutes      / minutes,      "min / min")
        assertApprox(1.0, hours        / hours,        "hr / hr"  )
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Measure conversions using `in`
    // ─────────────────────────────────────────────────────────────────────────

    @Test @JsName("oneSecondInMilliseconds")
    fun `1 second in milliseconds is 1000`() = assertApprox(1_000.0, (1 * seconds) `in` milliseconds)

    @Test @JsName("oneMinuteInSeconds")
    fun `1 minute in seconds is 60`() = assertApprox(60.0, (1 * minutes) `in` seconds)

    @Test @JsName("oneHourInMinutes")
    fun `1 hour in minutes is 60`() = assertApprox(60.0, (1 * hours) `in` minutes)

    @Test @JsName("oneHourInSeconds")
    fun `1 hour in seconds is 3600`() = assertApprox(3_600.0, (1 * hours) `in` seconds)

    @Test @JsName("oneHourInMilliseconds")
    fun `1 hour in milliseconds is 3 600 000`() = assertApprox(3_600_000.0, (1 * hours) `in` milliseconds)

    @Test @JsName("halfMinuteInSeconds")
    fun `0 point 5 minutes in seconds is 30`() = assertApprox(30.0, (0.5 * minutes) `in` seconds)

    @Test @JsName("_90SecondsInMinutes")
    fun `90 seconds in minutes is 1 point 5`() = assertApprox(1.5, (90 * seconds) `in` minutes)

    @Test @JsName("_1500msInSeconds")
    fun `1500 milliseconds in seconds is 1 point 5`() = assertApprox(1.5, (1500 * milliseconds) `in` seconds)

    @Test @JsName("roundTripMsToHoursAndBack")
    fun `round-trip milliseconds to hours and back`() {
        val original = 7_200_000.0               // 2 hours in ms
        val inHours  = (original * milliseconds) `in` hours
        val backToMs = (inHours  * hours       ) `in` milliseconds
        assertApprox(original, backToMs, "round-trip ms -> hr -> ms")
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Arithmetic on Measure<Time>
    // ─────────────────────────────────────────────────────────────────────────

    @Test @JsName("addTwoMillisecondMeasures")
    fun `adding two millisecond measures gives correct sum`() = assertApprox(1_000.0, 300 * milliseconds + 700 * milliseconds `in` milliseconds)

    @Test @JsName("addSecondsAndMilliseconds")
    fun `adding seconds and milliseconds gives correct sum in milliseconds`() = assertApprox(1_500.0, 1 * seconds + 500 * milliseconds `in` milliseconds)

    @Test @JsName("addMinutesAndSeconds")
    fun `adding minutes and seconds gives correct total in seconds`() = assertApprox(150.0, 2 * minutes + 30 * seconds `in` seconds)

    @Test @JsName("subtractMillisecondMeasures")
    fun `subtracting millisecond measures gives correct difference`() = assertApprox(800.0, 1 * seconds - 200 * milliseconds `in` milliseconds)

    @Test @JsName("subtractMinutesFromHour")
    fun `subtracting 30 minutes from 1 hour leaves 30 minutes`() = assertApprox(30.0, 1 * hours - 30 * minutes `in` minutes)

    @Test @JsName("scalarMultiplication")
    fun `scalar multiplication scales measure correctly`() = assertApprox(60.0, 3 * (20 * seconds) `in` seconds)

    @Test @JsName("negativeScalar")
    fun `multiplying by negative scalar negates the measure`() = assertApprox(-5.0, -1 * (5 * minutes) `in` minutes)

    // ─────────────────────────────────────────────────────────────────────────
    // Comparison of Measure<Time>
    // ─────────────────────────────────────────────────────────────────────────

    @Test @JsName("oneMinuteGreaterThan59Seconds")
    fun `1 minute is greater than 59 seconds`() = assertTrue((1 * minutes) > (59 * seconds))

    @Test @JsName("_3600SecondsEqualsOneHour")
    fun `3600 seconds equals 1 hour`() = assertEquals(3600 * seconds, 1 * hours)

    @Test @JsName("_60000MillisecondsEqualsOneMinute")
    fun `60 000 milliseconds equals 1 minute`() = assertEquals(60_000 * milliseconds, 1 * minutes)

    @Test @JsName("oneSecondGreaterThan999ms")
    fun `1 second is greater than 999 milliseconds`() = assertTrue((1 * seconds) > (999 * milliseconds))

    @Test @JsName("oneSecondNotGreaterThan1000ms")
    fun `1 second is not greater than 1000 milliseconds`() = assertTrue((1 * seconds) <= (1000 * milliseconds))

    // ─────────────────────────────────────────────────────────────────────────
    // Edge cases
    // ─────────────────────────────────────────────────────────────────────────

    @Test @JsName("zeroMillisecondsInSeconds")
    fun `0 milliseconds in seconds is 0`() = assertApprox(0.0, (0 * milliseconds) `in` seconds)

    @Test @JsName("fractionalMilliseconds")
    fun `fractional seconds convert to fractional milliseconds`() = assertApprox(1.5, (1.5 * seconds) `in` seconds)

    @Test @JsName("largeValueConversion")
    fun `large value converts correctly across many units`() {
        // 1 day = 24 hours = 1440 minutes = 86400 seconds = 86 400 000 ms
        val oneDayInMs = 24 * hours
        assertApprox(86_400_000.0, oneDayInMs `in` milliseconds)
        assertApprox(86_400.0,     oneDayInMs `in` seconds     )
        assertApprox(1_440.0,      oneDayInMs `in` minutes     )
        assertApprox(24.0,         oneDayInMs `in` hours       )
    }

    @Test @JsName("additionIsCommutative")
    fun `measure addition is commutative`() {
        val a = 30 * seconds + 1 * minutes
        val b =  1 * minutes + 30 * seconds
        assertApprox(a `in` milliseconds, b `in` milliseconds)
    }
}