/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.vis.demoUtils

import jetbrains.datalore.plot.builder.presentation.Style
import jetbrains.datalore.vis.demoUtils.swing.SvgViewerDemoWindowBase
import jetbrains.datalore.vis.svg.SvgSvgElement
import jetbrains.datalore.vis.swing.ComposeMapperPanel
import java.awt.Color
import javax.swing.BorderFactory
import javax.swing.JComponent

class SvgViewerDemoWindowCompose(
    title: String,
    svgRoots: List<SvgSvgElement>,
    maxCol: Int = 2,
) : SvgViewerDemoWindowBase(
    title,
    svgRoots = svgRoots,
    maxCol = maxCol,
) {
    override fun createPlotComponent(svgRoot: SvgSvgElement): JComponent {
        val component = ComposeMapperPanel(svgRoot)

        component.border = BorderFactory.createLineBorder(Color.ORANGE, 1)
        return component
    }
}