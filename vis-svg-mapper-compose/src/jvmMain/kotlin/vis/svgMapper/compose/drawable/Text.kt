/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.vis.svgMapper.compose.drawable

import org.jetbrains.skia.*
import kotlin.reflect.KProperty

class Text : Figure() {
    enum class FontStyle {
        NORMAL,
        BOLD,
        ITALIC,
        BOLD_ITALIC
    }

    var x: Float by visualProp(0.0f)
    var y: Float by visualProp(0.0f)
    var text: String by visualProp("")
    var fontFamily: String? by visualProp(null)
    var fontStyle by visualProp(FontStyle.NORMAL)
    var fontSize by visualProp(25.0f)
    private var font = Font(Typeface.makeDefault())

    override fun <T> onVisualPropertyChanged(property: KProperty<*>, oldValue: T, newValue: T) {
        update()
    }

    private fun update() {
        val style = when (fontStyle) {
            FontStyle.NORMAL -> org.jetbrains.skia.FontStyle.NORMAL
            FontStyle.BOLD -> org.jetbrains.skia.FontStyle.BOLD
            FontStyle.ITALIC -> org.jetbrains.skia.FontStyle.ITALIC
            FontStyle.BOLD_ITALIC -> org.jetbrains.skia.FontStyle.BOLD_ITALIC
        }
        font = Font(
            typeface = Typeface.makeFromName(fontFamily, style),
            size = fontSize
        )
    }

    override fun onDraw(canvas: Canvas?) {
        val y = y - font.metrics.ascent
        canvas!!.drawString(text, x, y, font, Paint())
        //canvas?.drawTextLine(TextLine.make(text, font), x, y, Paint())
    }

//    override fun onGetBounds(): Rect {
//        val textBounds = font.measureText(text)
//        val parentTranslate = getTranslate()
//        return textBounds.offset(parentTranslate).offset(x, y)
//    }
}
