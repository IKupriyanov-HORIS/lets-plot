/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.vis.svgMapper.compose.drawable

import androidx.compose.ui.graphics.Matrix
import jetbrains.datalore.base.observable.collections.CollectionItemEvent
import jetbrains.datalore.base.observable.collections.list.ObservableArrayList
import jetbrains.datalore.base.observable.event.EventHandler
import jetbrains.datalore.vis.svgMapper.compose.Transform
import org.jetbrains.skia.Drawable
import org.jetbrains.skia.Matrix33
import org.jetbrains.skia.Point
import org.jetbrains.skia.Rect
import kotlin.properties.ObservableProperty
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

abstract class Element: Drawable() {
    var parent: Element? by visualProp(null)
    var transforms = ObservableArrayList<Transform>()

    init {
        transforms.addHandler(object : EventHandler<CollectionItemEvent<out Transform>> {
            override fun onEvent(event: CollectionItemEvent<out Transform>) {
                invalidate()
            }
        })
    }

    protected fun invalidate() {
        notifyDrawingChanged()
    }

    protected fun getTranslate(): Point {
        var p = parent
        var offsetX = 0.0f
        var offsetY = 0.0f

        //while (p != null) {
        //    offsetX += p.x
        //    offsetY += p.y
        //}

        return Point(offsetX, offsetY)
    }


    protected fun <T> visualProp(initialValue: T): ReadWriteProperty<Any?, T> {
        return object : ObservableProperty<T>(initialValue) {
            override fun afterChange(property: KProperty<*>, oldValue: T, newValue: T) = onChange(property, oldValue, newValue)
        }
    }


    private fun <T> onChange(property: KProperty<*>, oldValue: T, newValue: T) {
        if (oldValue != newValue) {
            onVisualPropertyChanged(property, oldValue, newValue)
            invalidate()
        }
    }

    protected open fun <T> onVisualPropertyChanged(property: KProperty<*>, oldValue: T, newValue: T) {}

    override fun onGetBounds(): Rect {
        var root = parent
        while (root?.parent != null) {
            root = parent
        }
        return root?.onGetBounds() ?: Rect(0.0f, 0.0f, 0.0f, 0.0f)
    }
}
