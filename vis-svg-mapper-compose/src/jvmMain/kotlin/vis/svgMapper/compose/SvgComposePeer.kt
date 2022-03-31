/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.vis.svgMapper.compose

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.unsupported.UNSUPPORTED
import jetbrains.datalore.mapper.core.Mapper
import jetbrains.datalore.vis.svg.SvgLocatable
import jetbrains.datalore.vis.svg.SvgNode
import jetbrains.datalore.vis.svg.SvgPlatformPeer
import jetbrains.datalore.vis.svg.SvgTextContent
import jetbrains.datalore.vis.svgMapper.compose.drawable.Element

class SvgComposePeer : SvgPlatformPeer {
    private val myMappingMap = HashMap<SvgNode, Mapper<out SvgNode, out Element>>()

//    private fun ensureElementConsistency(source: SvgNode, target: Node) {
//        if (source is SvgElement && target !is SVGOMElement) {
//            throw IllegalStateException("Target of SvgElement must be SVGOMElement")
//        }
//    }

//    private fun ensureLocatableConsistency(source: SvgNode, target: Node) {
//        if (source is SvgLocatable && target !is SVGLocatable) {
//            throw IllegalStateException("Target of SvgLocatable must be SVGLocatable")
//        }
//    }

//    private fun ensureTextContentConsistency(source: SvgNode, target: Node) {
//        if (source is SvgTextContent && target !is SVGOMTextContentElement) {
//            throw IllegalStateException("Target of SvgTextContent must be SVGOMTextContentElement")
//        }
//    }

//    private fun ensureTransformableConsistency(source: SvgNode, target: Node) {
//        if (source is SvgTransformable && target !is SVGTransformable) {
//            throw IllegalStateException("Target of SvgTransformable must be SVGTransformable")
//        }
//    }

//    private fun ensureSourceTargetConsistency(source: SvgNode, target: Node) {
//        ensureElementConsistency(source, target)
//        ensureLocatableConsistency(source, target)
//        ensureTextContentConsistency(source, target)
//        ensureTransformableConsistency(source, target)
//    }

    private fun ensureSourceRegistered(source: SvgNode) {
        if (!myMappingMap.containsKey(source)) {
            throw IllegalStateException("Trying to call platform peer method of unmapped node: ${source::class.simpleName}")
        }
    }

    fun registerMapper(source: SvgNode, mapper: SvgNodeMapper<out SvgNode, out Element>) {
        myMappingMap[source] = mapper
    }

    fun unregisterMapper(source: SvgNode) {
        myMappingMap.remove(source)
    }

    override fun getComputedTextLength(node: SvgTextContent): Double {
        UNSUPPORTED("getComputedTextLength")
    }

    @Suppress("UNUSED_PARAMETER")
    private fun transformCoordinates(relative: SvgLocatable, point: DoubleVector, inverse: Boolean): DoubleVector {
        UNSUPPORTED("getComputedTextLength")
    }

    override fun invertTransform(relative: SvgLocatable, point: DoubleVector): DoubleVector {
        return transformCoordinates(relative, point, true)
    }

    override fun applyTransform(relative: SvgLocatable, point: DoubleVector): DoubleVector {
        return transformCoordinates(relative, point, false)
    }

    override fun getBBox(element: SvgLocatable): DoubleRectangle {
        ensureSourceRegistered(element as SvgNode)
        val target = myMappingMap[element]!!.target

        // Otherwise Node will have outdated bounds if changes made by CSS (JFX apply CSS on next pulse)
        return target.bounds.let {
            DoubleRectangle(
                it.left.toDouble(),
                it.top.toDouble(),
                it.width.toDouble(),
                it.height.toDouble()
            )
        }
    }
}