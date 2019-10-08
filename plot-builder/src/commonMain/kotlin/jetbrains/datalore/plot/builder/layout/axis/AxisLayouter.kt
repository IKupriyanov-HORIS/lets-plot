package jetbrains.datalore.plot.builder.layout.axis

import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.plot.builder.layout.AxisLayoutInfo
import jetbrains.datalore.plot.builder.layout.axis.label.AxisLabelsLayout
import jetbrains.datalore.plot.builder.theme.AxisTheme
import jetbrains.datalore.visualization.plot.base.scale.Mappers

abstract class AxisLayouter protected constructor(val orientation: jetbrains.datalore.plot.builder.guide.Orientation, val domainRange: ClosedRange<Double>, val labelsLayout: AxisLabelsLayout) {

    fun doLayout(axisLength: Double, maxTickLabelsBounds: DoubleRectangle?): AxisLayoutInfo {
        val labelsInfo = labelsLayout.doLayout(axisLength, toAxisMapper(axisLength), maxTickLabelsBounds)
        val labelsBounds = labelsInfo.bounds

        val builder = AxisLayoutInfo.Builder()
                .axisBreaks(labelsInfo.breaks)
                .axisLength(axisLength)
                .orientation(orientation)
                .axisDomain(domainRange)
                .tickLabelsBoundsMax(maxTickLabelsBounds)
                // todo: add 1 labels info object
                .tickLabelSmallFont(labelsInfo.smallFont)
                .tickLabelAdditionalOffsets(labelsInfo.labelAdditionalOffsets)
                .tickLabelHorizontalAnchor(labelsInfo.labelHorizontalAnchor)
                .tickLabelVerticalAnchor(labelsInfo.labelVerticalAnchor)
                .tickLabelRotationAngle(labelsInfo.labelRotationAngle)
                .tickLabelsBounds(labelsBounds)

        return builder.build()
    }

    protected abstract fun toAxisMapper(axisLength: Double): (Double?) -> Double?

    protected fun toScaleMapper(axisLength: Double): (Double?) -> Double? {
        return Mappers.mul(domainRange, axisLength)
    }

    companion object {
        fun create(orientation: jetbrains.datalore.plot.builder.guide.Orientation,
                   axisDomain: ClosedRange<Double>, breaksProvider: AxisBreaksProvider, theme: AxisTheme): AxisLayouter {

            if (orientation.isHorizontal) {
                val labelsLayout: AxisLabelsLayout
                if (breaksProvider.isFixedBreaks) {
                    labelsLayout = AxisLabelsLayout.horizontalFixedBreaks(orientation, axisDomain, breaksProvider.fixedBreaks, theme)
                } else {
                    labelsLayout = AxisLabelsLayout.horizontalFlexBreaks(orientation, axisDomain, breaksProvider, theme)
                }
                return HorizontalAxisLayouter(orientation, axisDomain, labelsLayout)
            }

            // vertical
            val labelsLayout: AxisLabelsLayout
            if (breaksProvider.isFixedBreaks) {
                labelsLayout = AxisLabelsLayout.verticalFixedBreaks(orientation, axisDomain, breaksProvider.fixedBreaks, theme)
            } else {
                labelsLayout = AxisLabelsLayout.verticalFlexBreaks(orientation, axisDomain, breaksProvider, theme)
            }
            return VerticalAxisLayouter(orientation, axisDomain, labelsLayout)
        }
    }
}