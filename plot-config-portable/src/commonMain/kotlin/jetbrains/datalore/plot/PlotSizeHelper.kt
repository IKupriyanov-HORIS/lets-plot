/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.builder.assemble.PlotFacets
import jetbrains.datalore.plot.config.BunchConfig
import jetbrains.datalore.plot.config.Option
import jetbrains.datalore.plot.config.OptionsAccessor

internal object PlotSizeHelper {
    private const val ASPECT_RATIO = 3.0 / 2.0   // TODO: theme
    private const val DEF_PLOT_WIDTH = 500.0
    private const val DEF_LIVE_MAP_WIDTH = 800.0
    private val DEF_PLOT_SIZE = DoubleVector(DEF_PLOT_WIDTH, DEF_PLOT_WIDTH / ASPECT_RATIO)
    private val DEF_LIVE_MAP_SIZE = DoubleVector(DEF_LIVE_MAP_WIDTH, DEF_LIVE_MAP_WIDTH / ASPECT_RATIO)


    fun singlePlotSize(
        plotSpec: Map<String, Any>,
        plotSize: DoubleVector?,
        facets: PlotFacets,
        containsLiveMap: Boolean
    ): DoubleVector {
        return if (plotSize != null) {
            plotSize
        } else {
            var plotSizeSpec = getSizeOptionOrNull(plotSpec)
            if (plotSizeSpec != null) {
                plotSizeSpec
            } else {
                defaultSinglePlotSize(facets, containsLiveMap)
            }
        }
    }

    internal fun bunchItemBoundsList(bunchSpec: Map<String, Any>): List<DoubleRectangle> {
        val bunchConfig = BunchConfig(bunchSpec)
        if (bunchConfig.bunchItems.isEmpty()) {
            throw IllegalArgumentException("No plots in the bunch")
        }

        val plotBounds = ArrayList<DoubleRectangle>()
        for (bunchItem in bunchConfig.bunchItems) {
            plotBounds.add(
                DoubleRectangle(
                    DoubleVector(bunchItem.x, bunchItem.y),
                    bunchItemSize(bunchItem)
                )
            )
        }
        return plotBounds
    }

    internal fun bunchItemSize(bunchItem: BunchConfig.BunchItem): DoubleVector {
        return if (bunchItem.hasSize()) {
            bunchItem.size
        } else {
            singlePlotSize(
                bunchItem.featureSpec,
                null,
                PlotFacets.undefined(), false
            )
        }
    }

    private fun defaultSinglePlotSize(facets: PlotFacets, containsLiveMap: Boolean): DoubleVector {
        var plotSize = DEF_PLOT_SIZE
        if (facets.isDefined) {
            val xLevels = facets.xLevels!!
            val yLevels = facets.yLevels!!
            val columns = if (xLevels.isEmpty()) 1 else xLevels.size
            val rows = if (yLevels.isEmpty()) 1 else yLevels.size
            val panelWidth = DEF_PLOT_SIZE.x * (0.5 + 0.5 / columns)
            val panelHeight = DEF_PLOT_SIZE.y * (0.5 + 0.5 / rows)
            plotSize = DoubleVector(panelWidth * columns, panelHeight * rows)
        } else if (containsLiveMap) {
            plotSize = DEF_LIVE_MAP_SIZE
        }
        return plotSize
    }

    private fun getSizeOptionOrNull(singlePlotSpec: Map<String, Any>): DoubleVector? {
        if (!singlePlotSpec.containsKey(Option.Plot.SIZE)) {
            return null
        }
        val map = OptionsAccessor.over(singlePlotSpec).getMap(Option.Plot.SIZE)
        val sizeSpec = OptionsAccessor.over(map)
        val width = sizeSpec.getDouble("width")
        val height = sizeSpec.getDouble("height")
        if (width == null || height == null) {
            return null
        }
        return DoubleVector(width, height)
    }

    fun plotBunchSize(bunchItemBoundsIterable: Iterable<DoubleRectangle>): DoubleVector {
        return bunchItemBoundsIterable
            .fold(DoubleRectangle(DoubleVector.ZERO, DoubleVector.ZERO)) { acc, bounds ->
                acc.union(bounds)
            }
            .dimension
    }
}