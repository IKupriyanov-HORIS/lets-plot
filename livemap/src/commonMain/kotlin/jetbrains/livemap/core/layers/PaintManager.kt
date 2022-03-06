/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.core.layers

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.typedGeometry.Vec
import jetbrains.datalore.vis.canvas.Canvas
import jetbrains.datalore.vis.canvas.CanvasControl
import jetbrains.datalore.vis.canvas.SingleCanvasControl
import jetbrains.livemap.Client
import jetbrains.livemap.core.ecs.EcsEntity

interface PaintManager {
    fun addLayer(name: String, group: LayerGroup): CanvasLayerComponent
    fun removeLayer(canvasLayer: CanvasLayer)
    fun createLayersOrderComponent(): LayersOrderComponent
    fun pan(offset: Vec<Client>)
    fun repaint(layers: List<CanvasLayer>, dirtyLayerEntities: Collection<EcsEntity>)
}

private fun log(msg: String) {
    println("PaintManager: $msg")
}

class OffscreenPaintManager(canvasControl: CanvasControl) : PaintManager {
    private val singleCanvasControl: SingleCanvasControl = SingleCanvasControl(canvasControl)
    private val rect: DoubleRectangle = DoubleRectangle(DoubleVector.ZERO, canvasControl.size.toDoubleVector())
    private val myGroupedLayers = GroupedLayers()

    override fun pan(offset: Vec<Client>) {
        log("pan() - offset: $offset")
        singleCanvasControl.context.clearRect(rect)
        myGroupedLayers.orderedLayers.forEach {
            if (it.group != LayerGroup.UI) {
                singleCanvasControl.context.drawImage(it.snapshot(), offset.x, offset.y)
            } else {
                singleCanvasControl.context.drawImage(it.snapshot(), 0.0, 0.0)
            }
        }
    }

    override fun repaint(layers: List<CanvasLayer>, dirtyLayerEntities: Collection<EcsEntity>) {
        if (dirtyLayerEntities.isEmpty()) return

        log(dirtyLayerEntities.map { it.name }.joinToString { it })
        dirtyLayerEntities.forEach {
            it.get<CanvasLayerComponent>().canvasLayer.apply {
                log("repaint layer: $name")
                clear()
                render()
            }
            it.untag<DirtyCanvasLayerComponent>()
        }

        singleCanvasControl.context.clearRect(rect)
        layers.forEach {
            singleCanvasControl.context.drawImage(it.snapshot(), 0.0, 0.0)
        }
    }

    override fun addLayer(name: String, group: LayerGroup): CanvasLayerComponent {
        val canvasLayer = CanvasLayer(singleCanvasControl.createCanvas(), name, group)
        myGroupedLayers.add(group, canvasLayer)
        return CanvasLayerComponent(canvasLayer)
    }

    override fun removeLayer(canvasLayer: CanvasLayer) {
        myGroupedLayers.remove(canvasLayer)
    }

    override fun createLayersOrderComponent(): LayersOrderComponent {
        return LayersOrderComponent(myGroupedLayers)
    }
}

class ScreenPaintManager(
    private val canvasControl: CanvasControl,
) : PaintManager {
    private val rect: DoubleRectangle = DoubleRectangle(DoubleVector.ZERO, canvasControl.size.toDoubleVector())
    private val myGroupedLayers = GroupedLayers()
    private val myBackingStore = mutableMapOf<CanvasLayer, Canvas.Snapshot>()

    override fun pan(offset: Vec<Client>) {
        log("pan() - offset: $offset")
        myGroupedLayers.orderedLayers.forEach { layer ->
            if (layer.group != LayerGroup.UI) {
                val layerBackingImage = myBackingStore.getOrPut(layer, layer::snapshot)
                layer.clear()
                layer.canvas.context2d.drawImage(layerBackingImage, offset.x, offset.y)
            }
        }
    }

    override fun repaint(layers: List<CanvasLayer>, dirtyLayerEntities: Collection<EcsEntity>) {
        dirtyLayerEntities.forEach {
            it.get<CanvasLayerComponent>().canvasLayer.apply {
                log("repaint layer: $name")
                clear()
                render()
                myBackingStore.remove(this)
            }
            it.untag<DirtyCanvasLayerComponent>()
        }
    }

    override fun addLayer(name: String, group: LayerGroup): CanvasLayerComponent {
        val canvas = canvasControl.createCanvas(canvasControl.size)
        val canvasLayer = CanvasLayer(canvas, name, group)
        myGroupedLayers.add(group, canvasLayer)

        canvasControl.addChild(myGroupedLayers.orderedLayers.indexOf(canvasLayer), canvas)
        return CanvasLayerComponent(canvasLayer)
    }

    override fun removeLayer(canvasLayer: CanvasLayer) {
        canvasLayer.removeFrom(canvasControl)
        myGroupedLayers.remove(canvasLayer)
    }

    override fun createLayersOrderComponent(): LayersOrderComponent {
        return LayersOrderComponent(myGroupedLayers)
    }
}
