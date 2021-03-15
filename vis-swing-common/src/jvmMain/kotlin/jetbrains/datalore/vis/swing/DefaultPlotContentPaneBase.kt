/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.vis.swing

import jetbrains.datalore.base.registration.Disposable
import jetbrains.datalore.plot.MonolithicCommon
import java.awt.Color
import java.awt.Component
import javax.swing.BorderFactory
import javax.swing.BoxLayout
import javax.swing.JLabel
import javax.swing.JPanel

/**
 * In IDEA plugin: inherit and implement 'com.intellij.openapi.Disposable'.
 */
abstract class DefaultPlotContentPaneBase(
    rawSpec: MutableMap<String, Any>,
    private val preferredSizeFromPlot: Boolean,
    private val repaintDelay: Int,  // ms
    private val applicationContext: ApplicationContext

) : Disposable, JPanel() {

    init {
        layout = BoxLayout(this, BoxLayout.Y_AXIS)

        val processedSpec = MonolithicCommon.processRawSpecs(rawSpec, frontendOnly = false)
        createContent(processedSpec)
    }

    /**
     * In IDEA plugin: override and check for 'com.intellij.openapi.Disposable'.
     */
    override fun dispose() {
        for (component in components) {
            when (component) {
                is Disposable -> component.dispose()
            }
        }
        removeAll()
    }

    private fun createContent(processedSpec: MutableMap<String, Any>) {
        var shownMessages = HashSet<String>()
        val messagesArea: JLabel = JLabel().apply {
            foreground = Color.BLUE
            isFocusable = true
        }

        val componentProvider = createPlotComponentProvider(processedSpec) { messages ->
            if (messages.isNotEmpty()) {
                val text = messages.joinToString(
                    separator = "<br>",
                    prefix = "<html>",
                    postfix = "</html>"
                )
                if (!shownMessages.contains(text)) {
                    shownMessages.add(text)
                    messagesArea.border = BorderFactory.createCompoundBorder(
                        BorderFactory.createEmptyBorder(10, 0, 0, 0),
                        BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1, true)
                    )
                    messagesArea.text = text
                }
            }
        }

        val plotPanel = createPlotPanel(
            plotComponentProvider = componentProvider,
            preferredSizeFromPlot = preferredSizeFromPlot,
            repaintDelay = repaintDelay,
            applicationContext = applicationContext
        )

        plotPanel.alignmentX = Component.CENTER_ALIGNMENT
        messagesArea.alignmentX = Component.CENTER_ALIGNMENT

        this.add(plotPanel)
        this.add(messagesArea)
    }

    protected abstract fun createPlotComponentProvider(
        processedSpec: MutableMap<String, Any>,
        computationMessagesHandler: (List<String>) -> Unit
    ): PlotComponentProvider

    protected open fun createPlotPanel(
        plotComponentProvider: PlotComponentProvider,
        preferredSizeFromPlot: Boolean,
        repaintDelay: Int,  // ms
        applicationContext: ApplicationContext
    ): PlotPanel {
        return PlotPanel(
            plotComponentProvider = plotComponentProvider,
            preferredSizeFromPlot = preferredSizeFromPlot,
            repaintDelay = repaintDelay,
            applicationContext = applicationContext
        )
    }
}