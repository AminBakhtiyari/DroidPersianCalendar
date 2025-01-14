/*
 * Single class, no dependency, ColorPickerView.
 * Unlike the rest of the project is released under MIT license.
 * Feel free to copy and use it wherever you like or suggest improvements to it
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.byagowi.persiancalendar.ui.preferences.widgetnotification

import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Build
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView
import androidx.core.view.setPadding
import androidx.core.view.updatePadding
import java.util.*

class ColorPickerView : LinearLayout {
    private lateinit var colorResultView: TextView
    private lateinit var redSeekBar: SeekBar
    private lateinit var greenSeekBar: SeekBar
    private lateinit var blueSeekBar: SeekBar
    private lateinit var colorsToPick: LinearLayout
    private var colorCodeVisibility = false

    /*@ColorInt*/
    val pickerColor: Int
        get() = Color.argb(0xFF,
                redSeekBar.progress, greenSeekBar.progress, blueSeekBar.progress)

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        orientation = VERTICAL

        val context = context ?: return

        colorResultView = TextView(context).apply {
            setTextIsSelectable(true)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                gravity = Gravity.CENTER_HORIZONTAL or Gravity.BOTTOM
            }
            setOnClickListener {
                colorCodeVisibility = !colorCodeVisibility
                showColor()
            }
        }

        val density = context.resources.displayMetrics.density
        val seekBarPadding = density.toInt() * 8

        val listener = object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                showColor()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}

            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        }

        redSeekBar = SeekBar(context).apply {
            updatePadding(top = seekBarPadding, bottom = seekBarPadding)
            max = 255
            progressDrawable.setColorFilter(-0x400000, PorterDuff.Mode.SRC_IN)
            setOnSeekBarChangeListener(listener)
        }

        greenSeekBar = SeekBar(context).apply {
            updatePadding(top = seekBarPadding, bottom = seekBarPadding)
            max = 255
            progressDrawable.setColorFilter(-0xff4000, PorterDuff.Mode.SRC_IN)
            setOnSeekBarChangeListener(listener)
        }

        blueSeekBar = SeekBar(context).apply {
            updatePadding(top = seekBarPadding, bottom = seekBarPadding)
            max = 255
            progressDrawable.setColorFilter(-0xffff40, PorterDuff.Mode.SRC_IN)
            setOnSeekBarChangeListener(listener)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            redSeekBar.thumb.setColorFilter(-0x400000, PorterDuff.Mode.SRC_IN)
            greenSeekBar.thumb.setColorFilter(-0xff4000, PorterDuff.Mode.SRC_IN)
            blueSeekBar.thumb.setColorFilter(-0xffff40, PorterDuff.Mode.SRC_IN)
        }

        val seekBars = LinearLayout(context).apply {
            orientation = VERTICAL
            addView(redSeekBar)
            addView(greenSeekBar)
            addView(blueSeekBar)
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT).apply {
                weight = 1f
            }
            measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
                    MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED))
        }

        val frameLayout = FrameLayout(context).apply {
            addView(colorResultView)
            layoutParams = LayoutParams(seekBars.measuredHeight,
                    LayoutParams.MATCH_PARENT)
            setBackgroundColor(Color.LTGRAY)
            val framePadding = density.toInt()
            setPadding(framePadding, framePadding, framePadding, framePadding)
        }

        val widgetMain = LinearLayout(context).apply {
            addView(seekBars)
            addView(frameLayout)
        }

        colorsToPick = LinearLayout(context).apply {
            gravity = Gravity.CENTER
            orientation = HORIZONTAL
        }

        addView(widgetMain)
        addView(colorsToPick)
    }

    fun setColorsToPick(/*@ColorInt*/colors: IntArray) {
        colorsToPick.removeAllViews()

        val context = context ?: return

        val density = context.resources.displayMetrics.density

        for (color in colors) {
            val view = View(context).apply {
                setBackgroundColor(color)
                layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT)
            }

            val frameLayout = FrameLayout(context).apply {
                val rectSize = (40 * density).toInt()
                val margin = (5 * density).toInt()
                setBackgroundColor(Color.LTGRAY)
                layoutParams = LayoutParams(rectSize, rectSize).apply {
                    setMargins(margin, margin * 2, margin, margin)
                }
                addView(view)
                setPadding(density.toInt())
                setOnClickListener { setPickedColor(color) }
            }
            colorsToPick.addView(frameLayout)
        }
    }

    private fun showColor() {
        val color = Color.argb(0xFF, redSeekBar.progress,
                greenSeekBar.progress, blueSeekBar.progress)
        colorResultView.apply {
            setBackgroundColor(color)
            text = if (colorCodeVisibility)
                String.format(Locale.ENGLISH, "#%06X", 0xFFFFFF and color)
            else
                ""
            setTextColor(color xor 0xFFFFFF)
        }
    }

    fun setPickedColor(/*@ColorInt*/color: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            redSeekBar.setProgress(Color.red(color), true)
            greenSeekBar.setProgress(Color.green(color), true)
            blueSeekBar.setProgress(Color.blue(color), true)
        } else {
            redSeekBar.progress = Color.red(color)
            greenSeekBar.progress = Color.green(color)
            blueSeekBar.progress = Color.blue(color)
        }
        showColor()
    }
}
