package com.vlad1m1r.watchface.components.ticks

import com.vlad1m1r.watchface.components.ticks.layout.*
import com.vlad1m1r.watchface.data.DataStorage
import com.vlad1m1r.watchface.data.TicksLayoutType
import javax.inject.Inject
import javax.inject.Provider

class GetTicks @Inject constructor(
    private val dataStorage: DataStorage,
    private val ticksLayoutOriginal: Provider<TicksLayoutOriginal>,
    private val ticksLayout1: Provider<TicksLayout1>,
    private val ticksLayout2: Provider<TicksLayout2>,
    private val ticksLayout3: Provider<TicksLayout3>,
) {

    operator fun invoke(): TicksLayout {
        return when (dataStorage.getTicksLayoutType()) {
            TicksLayoutType.ORIGINAL -> {
                ticksLayoutOriginal.get()
            }
            TicksLayoutType.TICKS_LAYOUT_1 -> {
                ticksLayout1.get()
            }
            TicksLayoutType.TICKS_LAYOUT_2 -> {
                ticksLayout2.get()
            }
            TicksLayoutType.TICKS_LAYOUT_3 -> {
                ticksLayout3.get()
            }
        }
    }
}