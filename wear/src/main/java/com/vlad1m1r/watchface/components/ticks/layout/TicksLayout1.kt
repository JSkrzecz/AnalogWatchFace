package com.vlad1m1r.watchface.components.ticks.layout

import android.content.Context
import android.graphics.Canvas
import com.vlad1m1r.watchface.R
import com.vlad1m1r.watchface.components.ticks.usecase.AdjustTicks
import com.vlad1m1r.watchface.components.ticks.usecase.RoundCorners
import com.vlad1m1r.watchface.components.ticks.TickPaintProvider
import com.vlad1m1r.watchface.data.ColorStorage
import com.vlad1m1r.watchface.data.DataStorage
import com.vlad1m1r.watchface.model.Mode
import com.vlad1m1r.watchface.model.Point
import com.vlad1m1r.watchface.utils.getLighterGrayscale
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlin.math.*

class TicksLayout1 @Inject constructor(
    @ApplicationContext context: Context,
    colorStorage: ColorStorage,
    private val dataStorage: DataStorage,
    override val  tickPaintProvider: TickPaintProvider,
    override val adjustTicks: AdjustTicks,
    override val roundCorners: RoundCorners
) : TicksLayout(context, dataStorage) {

    private val tickLength = context.resources.getDimension(R.dimen.design1_tick_length)
    private val watchHourTickColor = colorStorage.getHourTicksColor()
    private val tickWidth = context.resources.getDimension(R.dimen.design1_tick_width)

    private val tickLengthMinute = context.resources.getDimension(R.dimen.design1_tick_length_minute)
    private val watchMinuteTickColor = colorStorage.getMinuteTicksColor()
    private val tickWidthMinute = context.resources.getDimension(R.dimen.design1_tick_width_minute)

    private val tickBurnInPadding = context.resources.getDimension(R.dimen.design1__tick_padding)
    private var tickPadding = tickBurnInPadding

    private val tickPaint = tickPaintProvider.getTickPaint(watchHourTickColor, tickWidth)
    private val tickPaintMinute = tickPaintProvider.getTickPaint(watchMinuteTickColor, tickWidthMinute)

    private var center = Point()
    private var outerTickRadius: Float = 0f
    private var innerTickRadiusHours: Float = 0f
    private var innerTickRadiusMinute: Float = 0f

    override fun setCenter(center: Point) {
        centerInvalidated = false
        this.center = center
        this.outerTickRadius = center.x - tickPadding
        this.innerTickRadiusHours = center.x - tickLength - tickPadding
        this.innerTickRadiusMinute = center.x - tickLengthMinute - tickPadding
    }

    override fun draw(canvas: Canvas) {
        for (tickIndex in 0..59) {
            val tickRotation = tickIndex * PI / 30
            val adjust = adjustTicks(tickRotation,
                center,
                bottomInset,
                isSquareScreen,
                shouldAdjustToSquareScreen
            )
            val roundCorners = if (shouldAdjustToSquareScreen) roundCorners(tickRotation, center, PI / 20) * 10 else 0.0

            val outerX = sin(tickRotation) * (outerTickRadius - roundCorners) * adjust
            val outerY = -cos(tickRotation) * (outerTickRadius - roundCorners) * adjust

            val innerTickRadius = if (tickIndex % 5 == 0) innerTickRadiusHours else innerTickRadiusMinute
            val paint = if (tickIndex % 5 == 0) tickPaint else tickPaintMinute

            val innerX = sin(tickRotation) * (innerTickRadius - roundCorners) * adjust
            val innerY = -cos(tickRotation) * (innerTickRadius - roundCorners) * adjust

            canvas.drawLine(
                (center.x + innerX).toFloat(), (center.y + innerY).toFloat(),
                (center.x + outerX).toFloat(), (center.y + outerY).toFloat(), paint
            )
        }
    }

    override fun setMode(mode: Mode) {
        tickPaint.apply {
            if (mode.isAmbient) {
                tickPaintProvider.inAmbientMode(this, getLighterGrayscale(watchHourTickColor), dataStorage.useAntiAliasingInAmbientMode())
                if (mode.isBurnInProtection) {
                    strokeWidth = 0f
                }
            } else {
                tickPaintProvider.inInteractiveMode(this, watchHourTickColor)
                strokeWidth = tickWidth
            }
        }
        tickPaintMinute.apply {
            if (mode.isAmbient) {
                tickPaintProvider.inAmbientMode(this, getLighterGrayscale(watchMinuteTickColor), dataStorage.useAntiAliasingInAmbientMode())
                if (mode.isBurnInProtection) {
                    strokeWidth = 0f
                }
            } else {
                tickPaintProvider.inInteractiveMode(this, watchMinuteTickColor)
                strokeWidth = tickWidthMinute
            }
        }
        tickPadding = if (shouldAdjustForBurnInProtection(mode)) {
            tickBurnInPadding
        } else {
            -2f
        }
        centerInvalidated = true
    }
}