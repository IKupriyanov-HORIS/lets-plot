/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.vis.svgMapper.compose.drawable

import org.jetbrains.skia.Canvas
import org.jetbrains.skia.Rect

class Circle: Figure() {
    var centerX: Float by visualProp(0.0f)
    var centerY: Float by visualProp(0.0f)
    var radius: Float? by visualProp(0.0f)

    override fun onDraw(canvas: Canvas?) {

    }
}