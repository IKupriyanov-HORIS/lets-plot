/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.vis.svgMapper.compose.drawable

import jetbrains.datalore.base.values.Color
import org.jetbrains.skia.Color4f

abstract class Figure : Element() {
    var fill: Color4f? by visualProp(null)
    var stroke: Color4f? by visualProp(null)
    var strokeWidth: Float? by visualProp(null)
    var strokeDashArray: List<Double>? by visualProp(null)
}