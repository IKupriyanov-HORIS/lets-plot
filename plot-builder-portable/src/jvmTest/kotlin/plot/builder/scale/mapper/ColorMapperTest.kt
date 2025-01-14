/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.scale.mapper

import jetbrains.datalore.base.assertion.assertEquals
import jetbrains.datalore.base.gcommon.collect.DoubleSpan
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.base.values.Colors
import kotlin.test.Test

class ColorMapperTest {
    @Test
    fun gradientHSV_Hue() {
        val lowHue = 0.0
        val highHue = 360.0
        val saturation = 0.5
        val value = 0.9

        val f = ColorMapper.gradientHSV(
                DoubleSpan(0.0, 1.0),
                doubleArrayOf(lowHue, saturation, value),
                doubleArrayOf(highHue, saturation, value),
                false,
                Color.GRAY
        )

        val hue0 = Colors.hsvFromRgb(f(0.0))[0]
        val hue1 = Colors.hsvFromRgb(f(0.5))[0]
        val hue2 = Colors.hsvFromRgb(f(1.0))[0]

        val accuracy = .001
        assertEquals(0.0, hue0, accuracy)
        assertEquals(180.0, hue1, accuracy)
        assertEquals(0.0, hue2, accuracy)
    }
}