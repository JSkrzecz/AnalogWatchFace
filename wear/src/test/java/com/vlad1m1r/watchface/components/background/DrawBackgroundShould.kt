package com.vlad1m1r.watchface.components.background

import android.graphics.Bitmap
import android.graphics.Canvas
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import org.junit.Test

class DrawBackgroundShould {

    private val ambientBitmapMock = mock<Bitmap>()
    private val normalBitmapMock = mock<Bitmap>()

    private val backgroundBitmapProviderMock = mock<BackgroundBitmapProvider>().apply {
        whenever(this.getAmbientBitmap()).thenReturn(ambientBitmapMock)
        whenever(this.getNormalBitmap()).thenReturn(normalBitmapMock)
    }

    private val canvasMock = mock<Canvas>()

    @Test
    fun switchToAmbientMode() {
        val drawBackground = DrawBackground(
            backgroundBitmapProviderMock
        )

        drawBackground.setInAmbientMode(true)
        drawBackground(canvasMock)

        verify(canvasMock).drawBitmap(backgroundBitmapProviderMock.getAmbientBitmap(), 0f, 0f, null)
    }

    @Test
    fun switchToInteractiveMode() {
        val drawBackground = DrawBackground(
            backgroundBitmapProviderMock
        )

        drawBackground.setInAmbientMode(false)
        drawBackground(canvasMock)

        verify(canvasMock).drawBitmap(backgroundBitmapProviderMock.getNormalBitmap(), 0f, 0f, null)
    }
}