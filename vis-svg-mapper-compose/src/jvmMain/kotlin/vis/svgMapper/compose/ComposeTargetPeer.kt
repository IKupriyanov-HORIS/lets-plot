/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.vis.svgMapper.compose

import jetbrains.datalore.base.registration.Registration
import jetbrains.datalore.base.unsupported.UNSUPPORTED
import jetbrains.datalore.vis.svg.SvgElement
import jetbrains.datalore.vis.svg.SvgTextNode
import jetbrains.datalore.vis.svg.event.SvgEventSpec
import jetbrains.datalore.vis.svg.slim.SvgSlimElements
import jetbrains.datalore.vis.svg.slim.SvgSlimNode
import jetbrains.datalore.vis.svgMapper.TargetPeer
import jetbrains.datalore.vis.svgMapper.compose.drawable.*

internal class ComposeTargetPeer : TargetPeer<Element> {
    override fun appendChild(target: Element, child: Element) {
        Utils.getChildren(target as Group).add(child)
    }

    override fun removeAllChildren(target: Element) {
        if (target is Group) {
            Utils.getChildren(target).clear()
        }
    }

    override fun newSvgElement(source: SvgElement): Element {
        return Utils.newSceneNode(source)
    }

    override fun newSvgTextNode(source: SvgTextNode): Element {
        TODO() // return Text(source.textContent().get())
    }

    override fun newSvgSlimNode(source: SvgSlimNode): Element {
        return when (source.elementName) {
            SvgSlimElements.GROUP -> Group()
            SvgSlimElements.LINE -> Line()
            SvgSlimElements.CIRCLE -> Circle()
            SvgSlimElements.RECT -> Rectangle()
            SvgSlimElements.PATH -> Path()
            else -> throw IllegalStateException("Unsupported slim node " + source::class.simpleName + " '" + source.elementName + "'")
        }
    }

    override fun setAttribute(target: Element, name: String, value: String) {
        Utils.setAttribute(target, name, value)
    }

    override fun hookEventHandlers(source: SvgElement, target: Element, eventSpecs: Set<SvgEventSpec>): Registration {
        UNSUPPORTED("hookEventHandlers")
    }
}