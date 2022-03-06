/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.core.layers

import jetbrains.datalore.base.typedGeometry.Vec
import jetbrains.livemap.Client
import jetbrains.livemap.World
import jetbrains.livemap.core.ecs.AbstractSystem
import jetbrains.livemap.core.ecs.EcsComponentManager
import jetbrains.livemap.core.ecs.EcsEntity
import jetbrains.livemap.mapengine.LiveMapContext

class LayersRenderingSystem internal constructor(
    componentManager: EcsComponentManager,
    private val myPaintManager: PaintManager,
) : AbstractSystem<LiveMapContext>(componentManager) {
    private var myDirtyLayers: List<Int> = emptyList()
    private var movingStartPosition: Vec<World>? = null
    private var lastDragDelta: Vec<Client>? = null

    val dirtyLayers: List<Int>
        get() = myDirtyLayers

    var updated: Boolean = true
        private set

    override fun updateImpl(context: LiveMapContext, dt: Double) {
        updated = false

        if (context.camera.panDistance != null) {
            val d = context.camera.panDistance!!
            if (lastDragDelta != d) {
                lastDragDelta = d
                myPaintManager.pan(d)
                updated = true
            }
        } else {
            movingStartPosition = null
            val canvasLayers = getSingleton<LayersOrderComponent>().canvasLayers
            val dirtyEntities = getEntities<DirtyCanvasLayerComponent>().toList()

            myPaintManager.repaint(canvasLayers, dirtyEntities)

            myDirtyLayers = dirtyEntities.map(EcsEntity::id)
            updated = dirtyEntities.isNotEmpty()
        }
    }
}
