/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.geom


import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.plot.base.*
import jetbrains.datalore.plot.base.geom.util.*
import jetbrains.datalore.plot.base.interact.GeomTargetCollector.TooltipParams.Companion.tooltip
import jetbrains.datalore.plot.base.interact.TipLayoutHint
import jetbrains.datalore.plot.base.render.SvgRoot

class RibbonGeom : GeomBase() {

    private fun dataPoints(aesthetics: Aesthetics): Iterable<DataPointAesthetics> {
        val data = GeomUtil.withDefined(aesthetics.dataPoints(), Aes.X, Aes.YMIN, Aes.YMAX)
        return GeomUtil.ordered_X(data)
    }

    override fun buildIntern(root: SvgRoot, aesthetics: Aesthetics, pos: PositionAdjustment, coord: CoordinateSystem, ctx: GeomContext) {
        val dataPoints = dataPoints(aesthetics)
        val helper = LinesHelper(pos, coord, ctx)
        val paths = helper.createBands(dataPoints, GeomUtil.TO_LOCATION_X_YMAX, GeomUtil.TO_LOCATION_X_YMIN)
        appendNodes(paths, root)

        //if you want to retain the side edges of ribbon: comment out the following codes, and switch decorate method in LinesHelper.createbands
        helper.setAlphaEnabled(false)
        val lines = helper.createLines(dataPoints, GeomUtil.TO_LOCATION_X_YMAX)
        lines.addAll(helper.createLines(dataPoints, GeomUtil.TO_LOCATION_X_YMIN))
        appendNodes(lines, root)

        buildHints(aesthetics, pos, coord, ctx)
    }

    private fun buildHints(aesthetics: Aesthetics, pos: PositionAdjustment, coord: CoordinateSystem, ctx: GeomContext) {
        val helper = GeomHelper(pos, coord, ctx)
        val colorsByDataPoint = HintColorUtil.createColorMarkerMapper(GeomKind.RIBBON, ctx)

        for (p in aesthetics.dataPoints()) {
            addTarget(p, ctx, GeomUtil.TO_LOCATION_X_YMAX, helper, colorsByDataPoint)
        }
    }

    private fun addTarget(
        p: DataPointAesthetics,
        ctx: GeomContext,
        toLocation: (DataPointAesthetics) -> DoubleVector?,
        helper: GeomHelper,
        colorsByDataPoint: (DataPointAesthetics) -> List<Color>
    ) {
        val coord = toLocation(p)
        if (coord != null) {
            val hint = HintsCollection.HintConfigFactory()
                .defaultObjectRadius(0.0)
                .defaultX(p.x()!!)
                .defaultKind(
                    if (ctx.flipped) {
                        TipLayoutHint.Kind.VERTICAL_TOOLTIP
                    } else {
                        TipLayoutHint.Kind.HORIZONTAL_TOOLTIP
                    }
                )
                .defaultColor(
                    p.fill()!!,
                    alpha = null
                )

            val hintsCollection = HintsCollection(p, helper)
                .addHint(hint.create(Aes.YMAX))
                .addHint(hint.create(Aes.YMIN))

            ctx.targetCollector.addPoint(
                p.index(),
                helper.toClient(coord, p),
                0.0,
                tooltip {
                    tipLayoutHints = hintsCollection.hints
                    markerColors = colorsByDataPoint(p)
                }
            )
        }
    }

    companion object {
//        val RENDERS = listOf(
//                Aes.X,
//                Aes.YMIN,
//                Aes.YMAX,
//                Aes.SIZE,
//                Aes.LINETYPE,
//                Aes.COLOR,
//                Aes.FILL,
//                Aes.ALPHA
//        )

        const val HANDLES_GROUPS = true
    }
}
