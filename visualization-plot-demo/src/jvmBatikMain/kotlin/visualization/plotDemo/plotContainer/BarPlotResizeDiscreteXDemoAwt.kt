package jetbrains.datalore.visualization.plotDemo.plotContainer

import jetbrains.datalore.visualization.plotDemo.SwingDemoFactoryBatik
import jetbrains.datalore.visualization.plotDemo.model.plotContainer.BarPlotResizeDemo

object BarPlotResizeDiscreteXDemoAwt {
    @JvmStatic
    fun main(args: Array<String>) {
        PlotResizeDemoUtil.show(BarPlotResizeDemo.discreteX(), SwingDemoFactoryBatik())
    }
}