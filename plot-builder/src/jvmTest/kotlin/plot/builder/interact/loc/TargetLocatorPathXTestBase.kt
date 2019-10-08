package jetbrains.datalore.plot.builder.interact.loc

import jetbrains.datalore.plot.builder.interact.TestUtil.PathPoint
import jetbrains.datalore.plot.builder.interact.TestUtil.PathPointsBuilder
import jetbrains.datalore.plot.builder.interact.TestUtil.createLocator
import jetbrains.datalore.plot.builder.interact.TestUtil.pathTarget
import jetbrains.datalore.visualization.plot.base.interact.GeomTargetLocator
import jetbrains.datalore.visualization.plot.base.interact.GeomTargetLocator.LookupSpace
import jetbrains.datalore.visualization.plot.base.interact.GeomTargetLocator.LookupStrategy
import kotlin.test.BeforeTest

abstract class TargetLocatorPathXTestBase {

    internal lateinit var p0: PathPoint
    internal lateinit var p1: PathPoint
    internal lateinit var p2: PathPoint
    internal lateinit var locator: GeomTargetLocator

    internal abstract val strategy: LookupStrategy

    @BeforeTest
    fun setUp() {
        val builder = PathPointsBuilder().defaultY(jetbrains.datalore.plot.builder.interact.loc.TargetLocatorPathXTestBase.Companion.Y)
        p0 = builder.x(100.0)
        p1 = builder.x(200.0)
        p2 = builder.x(300.0)

        locator = createLocator(LookupStrategy.HOVER, LookupSpace.X,
                pathTarget(jetbrains.datalore.plot.builder.interact.loc.TargetLocatorPathXTestBase.Companion.FIRST_PATH_KEY, p0, p1, p2)
        )
    }

    companion object {
        internal const val FIRST_PATH_KEY = 1
        internal const val Y = 17.0
        internal const val THIS_POINT_DISTANCE = 25.0
        internal const val MIDDLE_POINTS_DISTANCE = 50.0
        internal const val NEXT_POINT_DISTANCE = 60.0
    }

}