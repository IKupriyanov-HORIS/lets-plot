/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.vis.svgMapper.compose

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.observable.collections.DataloreIndexOutOfBoundsException
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.base.values.Colors
import jetbrains.datalore.vis.svg.SvgColor
import jetbrains.datalore.vis.svg.SvgColors
import jetbrains.datalore.vis.svg.SvgPathData
import jetbrains.datalore.vis.svg.SvgPathData.Action
import jetbrains.datalore.vis.svg.SvgTransform
import jetbrains.datalore.vis.svgMapper.compose.attr.SvgAttrMapping.Companion.asFloat
import org.jetbrains.skia.Matrix33
import kotlin.math.PI
import kotlin.math.tan

object TransformUtil {
    private const val OPTIONAL_PARAM = "(-?\\d+\\.?\\d*)?,? ?"

    private const val DEFAULT_FONT = "15px arial"
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

    internal fun parseSvgColorString(colorString: String?): SvgColor {
        return when {
            colorString == null -> SvgColors.NONE
            SvgColors.isColorName(colorString) -> SvgColors.forName(colorString)
            else -> SvgColors.create(parseColorString(colorString))
        }
    }

    private fun parseColorString(colorString: String?): Color? {
        return when {
            colorString == null -> null
            colorString.startsWith('#') -> Color.parseHex(colorString)
            Colors.isColorName(colorString) -> Colors.forName(colorString)
            else -> Color.parseOrNull(colorString)
        }
    }

    internal fun extractFont(style: String?): String {
        val FONT = 1
        val FONT_ATTRIBUTE = Regex("font:(.+);")
        fun extractStyleFont(style: String?): String? {
            if (style == null) {
                return null
            }
            val matchResult = FONT_ATTRIBUTE.find(style)
            return matchResult?.groupValues?.get(FONT)?.trim()
        }
        return extractStyleFont(style) ?: DEFAULT_FONT
    }

    private val TRANSFORM = MyPatternBuilder("(").or(
        SvgTransform.TRANSLATE,
        SvgTransform.ROTATE,
        SvgTransform.SCALE,
        SvgTransform.SKEW_X,
        SvgTransform.SKEW_Y,
        SvgTransform.MATRIX
    )
        .append(")\\( ?(-?\\d+\\.?\\d*),? ?").pluralAppend(OPTIONAL_PARAM, 5).append("\\)").toString()

    private val PATH = MyPatternBuilder("(").charset(Action.values()).append(") ?")
        .pluralAppend(OPTIONAL_PARAM, 7).toString()

    private val TRANSFORM_EXP = Regex(TRANSFORM) //RegExp.compile(TRANSFORM, "g")
    private val PATH_EXP = Regex(PATH) //RegExp.compile(PATH, "g")

    private const val NAME_INDEX = 1
    private const val FIRST_PARAM_INDEX = 2


    internal fun parseTransform(input: String?): List<Result> {
        return parse(input, TRANSFORM_EXP)
    }

    internal fun parsePath(input: String?): List<Result> {
        return parse(input, PATH_EXP)
    }

    private fun getName(matcher: MatchResult): String {
        return matcher.groupValues[NAME_INDEX]
    }

    private fun getParam(matcher: MatchResult, i: Int): String? {
        return matcher.groupValues[i + FIRST_PARAM_INDEX]
    }

    private fun getParamCount(matcher: MatchResult): Int {
        return matcher.groupValues.size - FIRST_PARAM_INDEX
    }

    private fun parse(input: String?, regExp: Regex): List<Result> {
        if (input == null) return listOf()

        val results = ArrayList<Result>()
        var matcher: MatchResult? = regExp.find(input)
        while (matcher != null) {
            val paramCount = getParamCount(matcher)
            val r = Result(getName(matcher), paramCount)
            for (i in 0 until paramCount) {
                val g = getParam(matcher, i) ?: break
                if (g == "") break
                r.addParam(g)
            }
            results.add(r)
            matcher = matcher.next()
        }
        return results
    }

    private class MyPatternBuilder internal constructor(s: String) {
        private val sb: StringBuilder = StringBuilder(s)

        internal fun append(s: String): MyPatternBuilder {
            sb.append(s)
            return this
        }

        internal fun pluralAppend(s: String, count: Int): MyPatternBuilder {
            for (i in 0 until count) {
                sb.append(s)
            }
            return this
        }

        internal fun or(vararg ss: String): MyPatternBuilder {
            val ssLastIndex = ss.size - 1
            for ((index, s) in ss.withIndex()) {
                sb.append(s)
                if (index < ssLastIndex) {
                    sb.append('|')
                }
            }
            return this
        }

        internal fun charset(actions: Array<Action>): MyPatternBuilder {
            sb.append('[')
            for (v in actions) {
                sb.append(v.absoluteCmd())
                sb.append(v.relativeCmd())
            }
            sb.append(']')
            return this
        }

        override fun toString(): String {
            return sb.toString()
        }
    }

    internal class Result constructor(val name: String, paramCount: Int) {
        private val myParams: MutableList<Double?> = ArrayList(paramCount)

        val params: List<Double?>
            get() = myParams

        val paramCount: Int
            get() = myParams.size

        fun addParam(p: String?) {
            myParams.add(if (p == "") null else p?.toDouble())
        }

        fun getParam(i: Int): Double? {
            if (!containsParam(i)) {
                throw DataloreIndexOutOfBoundsException("index: $i; size: $paramCount; name: $name")
            }
            return myParams[i]
        }

        fun getVector(startIndex: Int): DoubleVector {
            return DoubleVector(getParam(startIndex)!!, getParam(startIndex + 1)!!)
        }

        fun containsParam(i: Int): Boolean {
            return i < paramCount
        }
    }
    fun toRadians(degrees: Double): Double = degrees * PI / 180

    private fun getTransforms(transforms: List<Result>): List<Transform> {
        return transforms.map { t ->
            when (t.name) {
                SvgTransform.SCALE -> Scale(t.getParam(SCALE_X)!!.asFloat, if (t.containsParam(SCALE_Y)) t.getParam(SCALE_Y)!!.asFloat else t.getParam(SCALE_X)!!.asFloat)
                SvgTransform.SKEW_X -> Skew(t.getParam(SKEW_X_ANGLE)!!.asFloat, 0.0f)
                SvgTransform.SKEW_Y -> Skew(0.0f, t.getParam(SKEW_Y_ANGLE)!!.asFloat)
                SvgTransform.ROTATE -> {
                    if (t.paramCount == 3) {
                        Rotate(0.0f, t.getParam(ROTATE_X)!!.asFloat, t.getParam(ROTATE_Y)!!.asFloat)
                    }
                    Rotate(t.getParam(ROTATE_ANGLE)!!.asFloat, 0.0f, 0.0f)
                }
                SvgTransform.TRANSLATE -> Translate(t.getParam(TRANSLATE_X)!!.asFloat, if (t.containsParam(TRANSLATE_Y)) t.getParam(TRANSLATE_Y)!!.asFloat else 0.0f)
                SvgTransform.MATRIX -> Matrix(
                    Matrix33(
                        t.getParam(MATRIX_11)!!.asFloat, t.getParam(MATRIX_12)!!.asFloat,
                        t.getParam(MATRIX_21)!!.asFloat, t.getParam(MATRIX_22)!!.asFloat,
                        t.getParam(MATRIX_DX)!!.asFloat, t.getParam(MATRIX_DY)!!.asFloat
                    )
                )
                else -> throw IllegalArgumentException("Unknown transform: " + t.name)
            }
        }
    }
}

