/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.layout

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.builder.guide.LegendPosition
import jetbrains.datalore.plot.builder.theme.LegendTheme
import kotlin.math.max

internal class LegendBoxesLayout(
    private val outerBounds: DoubleRectangle,
    private val innerBounds: DoubleRectangle,
    private val theme: LegendTheme
) {

    fun doLayout(legendsBlockInfo: LegendsBlockInfo): LegendsBlockInfo {
        val legendPosition = theme.position()
        val legendJustification = theme.justification()

        val blockSize = legendsBlockInfo.size()
        val innerCenter = innerBounds.center
        val sideLegendTop = max(outerBounds.top, innerCenter.y - blockSize.y / 2)

        val legendOrigin: DoubleVector = when (legendPosition) {
            LegendPosition.LEFT -> DoubleVector(outerBounds.left, sideLegendTop)
            LegendPosition.RIGHT -> DoubleVector(outerBounds.right - blockSize.x, sideLegendTop)
            LegendPosition.TOP -> DoubleVector(innerCenter.x - blockSize.x / 2, outerBounds.top)
            LegendPosition.BOTTOM -> DoubleVector(innerCenter.x - blockSize.x / 2, outerBounds.bottom - blockSize.y)
            else -> LegendBoxesLayoutUtil.overlayLegendOrigin(
                innerBounds,
                blockSize,
                legendPosition,
                legendJustification
            )
        }
        return legendsBlockInfo.moveAll(legendOrigin)
    }

    class BoxWithLocation internal constructor(val legendBox: LegendBoxInfo, val location: DoubleVector) {

        internal fun size(): DoubleVector {
            return legendBox.size
        }

        internal fun bounds(): DoubleRectangle {
            return DoubleRectangle(location, legendBox.size)
        }
    }
}
