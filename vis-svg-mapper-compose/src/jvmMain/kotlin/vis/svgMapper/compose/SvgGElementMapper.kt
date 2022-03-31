/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.vis.svgMapper.compose

import jetbrains.datalore.mapper.core.Synchronizers
import jetbrains.datalore.vis.svg.SvgGElement
import jetbrains.datalore.vis.svgMapper.SvgNodeSubtreeGeneratingSynchronizer
import jetbrains.datalore.vis.svgMapper.compose.drawable.Group

internal class SvgGElementMapper(
    source: SvgGElement,
    target: Group,
    peer: SvgComposePeer
) : SvgElementMapper<SvgGElement, Group>(source, target, peer) {

    override fun registerSynchronizers(conf: SynchronizersConfiguration) {
        super.registerSynchronizers(conf)

        if (!source.isPrebuiltSubtree) {
            val targetList = Utils.elementChildren(target)
            conf.add(
                Synchronizers.forObservableRole(
                    this,
                    source.children(),
                    targetList,
                    SvgNodeMapperFactory(peer)
                )
            )
        } else {
//            UNSUPPORTED("isPrebuiltSubtree")
            conf.add(
                SvgNodeSubtreeGeneratingSynchronizer(
                    source,
                    target,
                    ComposeTargetPeer()
                )
            )
        }
    }
}