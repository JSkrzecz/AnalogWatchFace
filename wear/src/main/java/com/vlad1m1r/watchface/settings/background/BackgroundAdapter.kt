package com.vlad1m1r.watchface.settings.background

import android.app.Activity
import android.graphics.drawable.Icon
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.recyclerview.widget.RecyclerView
import com.vlad1m1r.watchface.R
import com.vlad1m1r.watchface.components.background.SettingsBackgroundComplicationViewHolder
import com.vlad1m1r.watchface.data.ColorStorage
import com.vlad1m1r.watchface.data.DataStorage
import com.vlad1m1r.watchface.model.Point
import com.vlad1m1r.watchface.settings.BACKGROUND_LEFT_COLOR_PICKER_REQUEST_CODE
import com.vlad1m1r.watchface.settings.BACKGROUND_RIGHT_COLOR_PICKER_REQUEST_CODE
import com.vlad1m1r.watchface.settings.base.viewholders.*
import java.lang.IllegalArgumentException

private const val TYPE_TITLE = 0
private const val TYPE_PREVIEW = 1
private const val TYPE_COLOR_LEFT = 2
private const val TYPE_COLOR_RIGHT = 3
private const val TYPE_BLACK_AMBIENT = 4
private const val TYPE_BACKGROUND_COMPLICATION = 5

class BackgroundAdapter(
    private val colorStorage: ColorStorage,
    private val dataStorage: DataStorage,
    @StringRes private val title: Int,
    private val openBackgroundComplicationPicker: () -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var backgroundComplicationTitle: String? = null
    private var backgroundComplicationDescription: String? = null
    private var backgroundComplicationIcon: Icon? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_TITLE -> TitleViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(
                        R.layout.item_settings_title,
                        parent,
                        false
                    )
            )
            TYPE_PREVIEW -> WatchPreviewViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(
                        R.layout.item_settings_hand_preview,
                        parent,
                        false
                    )
            )
            TYPE_COLOR_LEFT,
            TYPE_COLOR_RIGHT -> ColorPickerViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(
                        R.layout.item_settings_text,
                        parent,
                        false
                    )
            )
            TYPE_BLACK_AMBIENT -> SettingsWithSwitchViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(
                        R.layout.item_settings_switch,
                        parent,
                        false
                    )
            )
            TYPE_BACKGROUND_COMPLICATION -> SettingsBackgroundComplicationViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(
                        R.layout.item_settings_background_complication,
                        parent,
                        false
                    )
            )
            else -> {
                throw IllegalArgumentException("viewType: $viewType is not supported")
            }
        }
    }

    override fun getItemCount(): Int {
        return 6
    }

    override fun getItemViewType(position: Int) =
        when (position) {
            0 -> TYPE_TITLE
            1 -> TYPE_PREVIEW
            2 -> TYPE_COLOR_LEFT
            3 -> TYPE_COLOR_RIGHT
            4 -> TYPE_BLACK_AMBIENT
            5 -> TYPE_BACKGROUND_COMPLICATION
            else -> throw IllegalArgumentException("Unsupported View Type position: $position")
        }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        when (viewHolder.itemViewType) {
            TYPE_TITLE ->
                (viewHolder as TitleViewHolder).bind(
                    title
                )
            TYPE_PREVIEW -> {
                val context = viewHolder.itemView.context
                val width = (context as Activity).window.decorView.width.toFloat()
                val height = context.resources.getDimension(R.dimen.item_watch_preview_height)
                (viewHolder as WatchPreviewViewHolder).bind(
                    Point(width / 2, height / 2),
                )
            }
            TYPE_COLOR_LEFT ->
                (viewHolder as ColorPickerViewHolder).setData(
                    R.string.wear_left_background_color,
                    BACKGROUND_LEFT_COLOR_PICKER_REQUEST_CODE,
                    colorStorage.getBackgroundLeftColor(),
                    false
                )
            TYPE_COLOR_RIGHT ->
                (viewHolder as ColorPickerViewHolder).setData(
                    R.string.wear_right_background_color,
                    BACKGROUND_RIGHT_COLOR_PICKER_REQUEST_CODE,
                    colorStorage.getBackgroundRightColor(),
                    false
                )
            TYPE_BLACK_AMBIENT ->
                (viewHolder as SettingsWithSwitchViewHolder).bind(
                    R.string.wear_black_ambient_background,
                    dataStorage.hasBlackAmbientBackground()
                ) {
                    dataStorage.setHasBlackAmbientBackground(it)
                    notifyDataSetChanged()
                }
            TYPE_BACKGROUND_COMPLICATION -> (viewHolder as SettingsBackgroundComplicationViewHolder).bind(
                backgroundComplicationTitle,
                backgroundComplicationDescription,
                backgroundComplicationIcon
            ) {
                openBackgroundComplicationPicker()
            }
        }
    }

    fun updateBackgroundComplicationView(title: String, description: String?, providerIcon: Icon?) {
        backgroundComplicationTitle = title
        backgroundComplicationDescription = description
        backgroundComplicationIcon = providerIcon
        notifyItemChanged(5)
    }
}