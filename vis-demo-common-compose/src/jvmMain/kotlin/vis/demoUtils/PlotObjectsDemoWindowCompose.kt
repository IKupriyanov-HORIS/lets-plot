/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.vis.demoUtils

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.builder.PlotSvgComponent
import jetbrains.datalore.plot.builder.PlotContainer
import jetbrains.datalore.plot.builder.presentation.Style
import jetbrains.datalore.vis.demoUtils.swing.PlotObjectsDemoWindowBase
import jetbrains.datalore.vis.swing.ComposeMapperPanel
import java.awt.Dimension
import javax.swing.JComponent

class PlotObjectsDemoWindowCompose(
    title: String,
    plotList: List<PlotSvgComponent>,
    private val stylesheets: List<String> = listOf(Style.JFX_PLOT_STYLESHEET),
    maxCol: Int = 2,
    plotSize: Dimension = Dimension(500, 350)
) : PlotObjectsDemoWindowBase(
    title,
    plotList = plotList,
    maxCol = maxCol,
    plotSize = plotSize
) {
    override fun createPlotComponent(plot: PlotSvgComponent, plotSize: Dimension): JComponent {
        val plotContainer = PlotContainer(
            plot,
            DoubleVector(plotSize.getWidth(), plotSize.getHeight())
        )

        plotContainer.ensureContentBuilt()
        return ComposeMapperPanel(plotContainer.svg)
    }
}