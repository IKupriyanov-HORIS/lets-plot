/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.vis.svgMapper.compose.drawable

import org.jetbrains.skia.Canvas
import org.jetbrains.skia.Rect

class Pane: Parent() {
    var x: Float by visualProp(0.0f)
    var y: Float by visualProp(0.0f)
    var width: Float by visualProp(0.0f)
    var height: Float by visualProp(0.0f)

    override fun onGetBounds(): Rect {
        return Rect(x, y, x + width, y + height)
    }
}