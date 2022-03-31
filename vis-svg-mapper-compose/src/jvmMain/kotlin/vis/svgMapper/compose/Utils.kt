/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.vis.svgMapper.compose


import jetbrains.datalore.base.event.Button
import jetbrains.datalore.base.event.KeyModifiers
import jetbrains.datalore.base.observable.collections.list.ObservableList
import jetbrains.datalore.vis.svg.*
import jetbrains.datalore.vis.svgMapper.compose.attr.SvgAttrMapping
import jetbrains.datalore.vis.svgMapper.compose.attr.SvgGAttrMapping
import jetbrains.datalore.vis.svgMapper.compose.attr.SvgRectAttrMapping
import jetbrains.datalore.vis.svgMapper.compose.attr.SvgSvgAttrMapping
import jetbrains.datalore.vis.svgMapper.compose.drawable.*
import kotlin.reflect.KClass


@Suppress("UNCHECKED_CAST")
private val ATTR_MAPPINGS: Map<KClass<out Element>, SvgAttrMapping<Element>> = mapOf(
    Pane::class to (SvgSvgAttrMapping as SvgAttrMapping<Element>),
    //StackPane::class to (SvgSvgAttrMapping as SvgAttrMapping<Element>),
    Group::class to (SvgGAttrMapping as SvgAttrMapping<Element>),
    Rectangle::class to (SvgRectAttrMapping as SvgAttrMapping<Element>),
    //Line::class to (SvgLineAttrMapping as SvgAttrMapping<Element>),
    //Ellipse::class to (SvgEllipseAttrMapping as SvgAttrMapping<Element>),
    //Circle::class to (SvgCircleAttrMapping as SvgAttrMapping<Element>),
    //Text::class to (SvgTextElementAttrMapping as SvgAttrMapping<Element>),
    //SVGPath::class to (SvgPathAttrMapping as SvgAttrMapping<Element>),
    //ImageView::class to (SvgImageAttrMapping as SvgAttrMapping<Element>)
)

internal object Utils {
    fun elementChildren(e: Element): MutableList<Element> {
        return object : AbstractMutableList<Element>() {
            override val size: Int
                get() = getChildren(e).size

            override fun get(index: Int): Element {
                return getChildren(e)[index]
            }

            override fun set(index: Int, element: Element): Element {
                if (element.parent != null) {
                    throw IllegalStateException()
                }
                return getChildren(e).set(index, element)
            }

            override fun add(index: Int, element: Element) {
                if (element.parent != null) {
                    throw IllegalStateException()
                }
                getChildren(e).add(index, element)
            }

            override fun removeAt(index: Int): Element {
                return getChildren(e).removeAt(index)
            }
        }
    }

    fun getChildren(parent: Element): MutableList<Element> {
        return when (parent) {
            is Group -> parent.children
            is Pane -> parent.children
            else -> throw IllegalArgumentException("Unsupported parent type: ${parent.javaClass.simpleName}")
        }
    }

    fun newSceneNode(source: SvgNode): Element {
        return when (source) {
            is SvgEllipseElement -> TODO() // Ellipse()
            is SvgCircleElement -> Circle()
            is SvgRectElement -> Rectangle()
            is SvgTextElement -> Text()
            is SvgPathElement -> Path()
            is SvgLineElement -> Line()
            is SvgSvgElement -> Pane() // Rectangle()
            is SvgGElement -> Group()
            is SvgStyleElement -> TODO() // Group()          // ignore
//            is SvgTextNode -> myDoc.createTextNode(null)
//            is SvgTSpanElement -> SVGOMTSpanElement(null, myDoc)
            is SvgDefsElement -> Group() // ignore
//            is SvgClipPathElement -> SVGOMClipPathElement(null, myDoc)
            is SvgImageElement -> TODO() // ImageView()
            else -> throw IllegalArgumentException("Unsupported source svg element: ${source.javaClass.simpleName}")
        }
    }

    //fun getButton(evt: MouseEvent): Button {
    //    return when (evt.button) {
    //        PRIMARY -> Button.LEFT
    //        MIDDLE -> Button.MIDDLE
    //        SECONDARY -> Button.RIGHT
    //        else -> Button.NONE
    //    }
    //}

    //fun getModifiers(evt: MouseEvent): KeyModifiers {
    //    val ctrlKey = evt.isControlDown
    //    val altKey = evt.isAltDown
    //    val shiftKey = evt.isShiftDown
    //    val metaKey = evt.isMetaDown
    //    return KeyModifiers(ctrlKey, altKey, shiftKey, metaKey)
    //}

    fun setAttribute(target: Element, name: String, value: Any?) {
        val attrMapping = ATTR_MAPPINGS[target::class]
        attrMapping?.setAttribute(target, name, value)
            //?: throw IllegalArgumentException("Unsupported target: ${target::class}")
            ?: println("Unsupported target: ${target::class}")
    }
}