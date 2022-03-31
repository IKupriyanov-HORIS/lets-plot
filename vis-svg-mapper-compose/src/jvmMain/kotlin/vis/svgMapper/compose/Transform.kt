/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.vis.svgMapper.compose

import org.jetbrains.skia.Canvas
import org.jetbrains.skia.Matrix33

interface Transform {
    fun apply(canvas: Canvas)
}

class Translate(
    private val dx: Float,
    private val dy: Float
) : Transform {
    override fun apply(canvas: Canvas) {
        canvas.translate(dx, dy)
    }
}

class Rotate(
    private val deg: Float,
    private val x: Float,
    private val y: Float
) : Transform {
    override fun apply(canvas: Canvas) {
        canvas.rotate(deg, x, y)
    }
}

class Scale(
    private val sx: Float,
    private val sy: Float
) : Transform {
    override fun apply(canvas: Canvas) {
        canvas.scale(sx, sy)
    }
}

class Skew(
    private val sx: Float,
    private val sy: Float
) : Transform {
    override fun apply(canvas: Canvas) {
        canvas.skew(sx, sy)
    }
}

class Matrix(
    private val matrix: Matrix33
) : Transform {
    override fun apply(canvas: Canvas) {
        canvas.setMatrix(matrix)
    }
}
