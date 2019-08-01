package jetbrains.datalore.visualization.base.svgToScene

import javafx.scene.transform.*
import jetbrains.datalore.visualization.base.svg.SvgTransform
import jetbrains.datalore.visualization.base.svgToCanvas.ParsingUtil
import jetbrains.datalore.visualization.base.svgToCanvas.toRadians
import kotlin.math.sin

private const val SCALE_X = 0
private const val SCALE_Y = 1
private const val SKEW_X_ANGLE = 0
private const val SKEW_Y_ANGLE = 0
private const val ROTATE_ANGLE = 0
private const val ROTATE_X = 1
private const val ROTATE_Y = 2
private const val TRANSLATE_X = 0
private const val TRANSLATE_Y = 1
private const val MATRIX_11 = 0
private const val MATRIX_12 = 1
private const val MATRIX_21 = 2
private const val MATRIX_22 = 3
private const val MATRIX_DX = 4
private const val MATRIX_DY = 5

fun parseSvgTransform(svgTransform: String): List<Transform> {
    val results = ParsingUtil.parseTransform(svgTransform)

    val transforms = ArrayList<Transform>()
    for (res in results) {
        val transform: Transform =
            when (res.name) {
                SvgTransform.SCALE -> {
                    val scaleX = res.getParam(SCALE_X)!!
                    val scaleY = res.getParam(SCALE_Y) ?: scaleX
                    Scale(scaleX, scaleY)
                }

                SvgTransform.SKEW_X -> {
                    val angle = res.getParam(SKEW_X_ANGLE)!!
                    val factor = sin(toRadians(angle))
                    Shear(factor, 0.0)
                }

                SvgTransform.SKEW_Y -> {
                    val angle = res.getParam(SKEW_Y_ANGLE)!!
                    val factor = sin(toRadians(angle))
                    Shear(0.0, factor)
                }

                SvgTransform.ROTATE -> {
                    val rotate = Rotate(res.getParam(ROTATE_ANGLE)!!)
                    if (res.paramCount == 3) {
                        rotate.pivotX = res.getParam(ROTATE_X)!!
                        rotate.pivotY = res.getParam(ROTATE_Y)!!
                    }
                    rotate
                }

                SvgTransform.TRANSLATE -> {
                    val dX = res.getParam(TRANSLATE_X)!!
                    val dY = res.getParam(TRANSLATE_Y) ?: 0.0
                    Translate(dX, dY)
                }

                SvgTransform.MATRIX -> TODO("We don't use MATRIX")

                else -> throw IllegalArgumentException("Unknown transform: " + res.name)
            }
        transforms.add(transform)
    }

    return transforms
}