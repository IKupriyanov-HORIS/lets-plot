/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.vis.swing.jfx

import jetbrains.datalore.vis.swing.PlotPanel

open class DefaultPlotPanelJfx(
    processedSpec: MutableMap<String, Any>,
    preserveAspectRatio: Boolean,
    preferredSizeFromPlot: Boolean,
    repaintDelay: Int,  // ms,
    computationMessagesHandler: (List<String>) -> Unit
) : PlotPanel(
    plotComponentProvider = DefaultPlotComponentProviderJfx(
        processedSpec = processedSpec,
        preserveAspectRatio = preserveAspectRatio,
        executor = DefaultSwingContextJfx.JFX_EDT_EXECUTOR,
        computationMessagesHandler = computationMessagesHandler
    ),
    preferredSizeFromPlot = preferredSizeFromPlot,
    repaintDelay = repaintDelay,
    applicationContext = DefaultSwingContextJfx()
)
