/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.vis.swing.compose

import jetbrains.datalore.base.logging.PortableLogging
import jetbrains.datalore.vis.svg.SvgSvgElement
import jetbrains.datalore.vis.swing.PlotSpecComponentProvider
import jetbrains.datalore.vis.swing.ComposeMapperPanel
import javax.swing.JComponent
import javax.swing.JScrollPane
import javax.swing.ScrollPaneConstants

open class DefaultPlotComponentProviderCompose(
    processedSpec: MutableMap<String, Any>,
    preserveAspectRatio: Boolean,
    executor: (() -> Unit) -> Unit,
    computationMessagesHandler: (List<String>) -> Unit
) : PlotSpecComponentProvider(
    processedSpec = processedSpec,
    preserveAspectRatio = preserveAspectRatio,
    svgComponentFactory = SVG_COMPONENT_FACTORY_JFX,
    executor = executor,
    computationMessagesHandler = computationMessagesHandler
) {

    /**
     * Override when in IDEA plugin.
     * Use: JBScrollPane
     */
    override fun createScrollPane(plotComponent: JComponent): JScrollPane {
        return JScrollPane(
            plotComponent,
            ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
            ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED
        ).apply {
            border = null
        }
    }

    companion object {
        private val LOG = PortableLogging.logger(DefaultPlotComponentProviderCompose::class)

        private val SVG_COMPONENT_FACTORY_JFX =
            { svg: SvgSvgElement -> ComposeMapperPanel(svg) }
    }
}