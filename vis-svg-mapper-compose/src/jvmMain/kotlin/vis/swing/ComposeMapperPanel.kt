/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.vis.swing

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.ComposePanel
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.loadSvgPainter
import androidx.compose.ui.unit.dp
import jetbrains.datalore.base.registration.Disposable
import jetbrains.datalore.mapper.core.MappingContext
import jetbrains.datalore.vis.svg.SvgNodeContainer
import jetbrains.datalore.vis.svg.SvgSvgElement
import jetbrains.datalore.vis.svgMapper.awt.RGBEncoderAwt
import jetbrains.datalore.vis.svgMapper.compose.SvgComposePeer
import jetbrains.datalore.vis.svgMapper.compose.SvgSvgElementMapper
import jetbrains.datalore.vis.svgMapper.compose.Translate
import jetbrains.datalore.vis.svgMapper.compose.drawable.Group
import jetbrains.datalore.vis.svgMapper.compose.drawable.Pane
import jetbrains.datalore.vis.svgMapper.compose.drawable.Text
import jetbrains.datalore.vis.svgToString.SvgToString
import org.jetbrains.skia.*
import java.awt.Dimension
import javax.swing.JPanel
import javax.swing.Timer

class ComposeMapperPanel(
    private val svg: SvgSvgElement,
) : JPanel(), Disposable {
    private val nodeContainer = SvgNodeContainer(svg)  // attach root

    val surface = Surface.makeRasterN32Premul(500, 500)

    private val composePanel = ComposePanel().apply {
        setContent {
            surface
        }
    }

    val font = Font(Typeface.makeDefault()).apply {
        edging = FontEdging.SUBPIXEL_ANTI_ALIAS
        size = 24f
    }

    val hello = Text().apply {
        text = "Hello"
        x = 0.0f
        y = 0.0f
    }

    val world = Text().apply {
        text = "world"
        x = 60.0f
        y = 0.0f
    }

    val asterisks = Text().apply {
        x = 120.0f
        y = 0.0f
        text = "!"
    }

    val helloWorld = Pane().apply {
        children.addAll(
            listOf(
                hello,
                world,
                Group().apply {
                    children.add(asterisks)
                    transforms += Translate(dx = 120.0f, dy = 0.0f)
                }
            )
        )
    }


    init {

        val rootMapper = SvgSvgElementMapper(svg, SvgComposePeer())
        rootMapper.attachRoot(MappingContext())

        val svgToString = SvgToString(RGBEncoderAwt())
        var dir = 1.0f
        Timer(1000 / 60) {
            val drawable = rootMapper.target

            composePanel.setContent {
                Canvas(Modifier.fillMaxSize()) {
                    drawIntoCanvas {
                        it.nativeCanvas.drawDrawable(drawable)
                    }
                    //helloWorld.x += 5.0f * dir
                    //helloWorld.y += 5.0f * dir
                    //if (helloWorld.x == 400.0f) dir = -1.0f
                    //if (helloWorld.x == 0.0f) dir = 1.0f
                }
            }
        }.start()

//        val s = object : Shape {
//            override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline {
//                return Outline.Generic(
//                    Path().apply {
//                        moveTo(0.0f, 0.0f)
//                        lineTo(100.0f, 100.0f)
//                    }
//                )
//            }
//
//        }

//        canvas.drawPaint(svgPainter)
//        canvas.drawString("trololo", 250.0f, 250.0f, font, Paint())
//        add(composePanel)
//        composePanel.setContent {
//
//            Canvas(Modifier.fillMaxSize()) {
//                Box {
//                    drawOutline(s.createOutline(size, ))
//                }
//            }
//            Box {
//                s
//            }
//
//        }

        val width = svg.width().get()!!.dp
        val height = svg.height().get()!!.dp

        val img = ImageVector.Builder(
            defaultWidth = width,
            defaultHeight = height,
            viewportWidth = width.value,
            viewportHeight = height.value,
        )


        add(composePanel)

//        composePanel.setContent {
//            val svgPainter = loadSvgPainter(svgToString.render(svg).byteInputStream(), LocalDensity.current)
//            Canvas(Modifier.fillMaxSize()) {
//                drawIntoCanvas {
//                    it.nativeCanvas.drawDrawable(helloWorld)
//                    with(svgPainter) {
//                        draw(svgPainter.intrinsicSize)
//                    }
//
//                    it.nativeCanvas.drawTextLine(
//                        TextLine.make("Text", font),
//                        0f,
//                        -font.metrics.ascent,
//                        Paint().apply {
//                            color = Color.makeRGB(0, 0, 0)
//                        }
//                    )
//                }
//            }
//        }
    }

    override fun dispose() {
        TODO("Not yet implemented")
    }

    override fun getPreferredSize(): Dimension {
        val size = Dimension(svg.width().get()!!.toInt(), svg.height().get()!!.toInt())
        composePanel.preferredSize = size
        return size
    }
}

