package io.nacular.measured.units

import io.nacular.measured.units.Angle.Companion.acos
import io.nacular.measured.units.Angle.Companion.acosh
import io.nacular.measured.units.Angle.Companion.asin
import io.nacular.measured.units.Angle.Companion.asinh
import io.nacular.measured.units.Angle.Companion.atan
import io.nacular.measured.units.Angle.Companion.atan2
import io.nacular.measured.units.Angle.Companion.atanh
import io.nacular.measured.units.Angle.Companion.cos
import io.nacular.measured.units.Angle.Companion.cosh
import io.nacular.measured.units.Angle.Companion.degrees
import io.nacular.measured.units.Angle.Companion.radians
import io.nacular.measured.units.Angle.Companion.sin
import io.nacular.measured.units.Angle.Companion.sinh
import io.nacular.measured.units.Angle.Companion.tan
import io.nacular.measured.units.Angle.Companion.tanh
import kotlin.js.JsName
import kotlin.math.PI
import kotlin.test.Test
import kotlin.test.expect

/**
 * Created by Nicholas Eddy on 2/22/18.
 */
class AngleTests {

    @Test @JsName("combinationsWork")
    fun `combinations work`() {
        testUnit(
            mapOf(
                radians to mapOf(1.0 to radians, 180 / PI to degrees),
                degrees to mapOf(PI / 180 to radians, 1.0 to degrees)
            )
        )
    }

    @Test @JsName("radiansConstants")
    fun `radians constants`() {
        expect(10   * radians) { 10   * radians }
        expect(10.0 * radians) { 10.0 * radians }
        expect(10f  * radians) { 10f  * radians }
        expect(10L  * radians) { 10L  * radians }
    }

    @Test @JsName("degreesConstants")
    fun `degrees constants`() {
        expect(10   * degrees) { 10   * degrees }
        expect(10.0 * degrees) { 10.0 * degrees }
        expect(10f  * degrees) { 10f  * degrees }
        expect(10L  * degrees) { 10L  * degrees }
    }

    @Test @JsName("mathWorks")
    fun `math works`() {
        val angle1 = 79 * degrees
        val value1 =  0.5
        val value2 =  0.678

        expect(kotlin.math.sin  (angle1 `in` radians)          ) { sin  (angle1        )              }
        expect(kotlin.math.cos  (angle1 `in` radians)          ) { cos  (angle1        )              }
        expect(kotlin.math.tan  (angle1 `in` radians)          ) { tan  (angle1        )              }
        expect(kotlin.math.asin (value1             )          ) { asin (value1        ) `in` radians }
        expect(kotlin.math.acos (value1             )          ) { acos (value1        ) `in` radians }
        expect(kotlin.math.atan (value1             )          ) { atan (value1        ) `in` radians }
        expect(kotlin.math.atan2(value1, value2     )          ) { atan2(value1, value2) `in` radians }
        expect(kotlin.math.sinh (angle1 `in` radians)          ) { sinh (angle1        )              }
        expect(kotlin.math.cosh (angle1 `in` radians)          ) { cosh (angle1        )              }
        expect(kotlin.math.tanh (angle1 `in` radians)          ) { tanh (angle1        )              }
        expect(kotlin.math.asinh(value1             ) * radians) { asinh(value1        )              }
        expect(kotlin.math.acosh(3.0                ) * radians) { acosh(3.0           )              }
        expect(kotlin.math.atanh(value1             ) * radians) { atanh(value1        )              }
    }
}