package com.app.signage91.componentfragments

import android.content.Context
import android.util.AttributeSet
import android.widget.AbsoluteLayout
import android.widget.LinearLayout
import com.app.signage91.RSSFeedCompoundSettingsModel
import com.app.signage91.RSSFeedSettingsModel
import com.app.signage91.TextViewSettingModel
import com.app.signage91.VideoListSettingsModel
import com.app.signage91.VideoSettingsModel
import com.app.signage91.components.ImageListCompoundComponent
import com.app.signage91.components.ImageViewComponent
import com.app.signage91.components.RSSFeedViewComponent
import com.app.signage91.components.RSSFeedViewCompoundComponents
import com.app.signage91.components.TextViewComponent
import com.app.signage91.components.VideoListCompoundComponent
import com.app.signage91.components.VideoViewComponent
import com.app.signage91.components.YoutubeViewComponent
import com.app.signage91.helpers.addLog
import com.app.signage91.helpers.getHeightByPercent
import com.app.signage91.helpers.getWidthByPercent
import com.app.signage91.models.CoordinatesModel
import com.app.signage91.models.ImageListCompoundModel
import com.app.signage91.models.ImageViewModel
import com.app.signage91.models.YoutubeViewModel

class AssetComponents @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, assets: List<Any>, coordinatesModel: CoordinatesModel
) : LinearLayout(context, attrs) {

    private val assetsList = assets
    private val coordinatesModel = coordinatesModel

    init {
        applyStyles()
        setData()
    }

    private fun setData() {
        assetsList.forEach { asset ->
            this.removeAllViews()
           var duration = 10L
            when (asset) {
                is ImageListCompoundModel -> {
                    context.addLog("Setup started for ImageListCompoundModel.")
                    setUpImageListCompoundComponent(asset)
                    duration = asset.duration
                }
                is VideoListSettingsModel -> {
                    context.addLog("Setup started for VideoListSettingsModel.")
                    setUpVideoListCompoundComponent(asset)
                    duration = asset.duration
                }
                is ImageViewModel -> {
                    context.addLog("Setup started for ImageViewModel.")
                    setUpImageViewComponents(asset)
                    duration = asset.duration
                }
                is VideoSettingsModel -> {
                    context.addLog("Setup started for VideoSettingsModel.")
                    setUpNativeVideoViewComponents(asset)
                    duration = asset.duration
                }
                is YoutubeViewModel -> {
                    context.addLog("Setup started for YoutubeViewModel.")
                    setYoutubeViewComponent(asset)
                    duration = asset.duration
                }
                is TextViewSettingModel -> {
                    context.addLog("Setup started for TextViewSettingModel.")
                    setUpTextViewComponents(asset)
                    duration = asset.duration
                }
                is RSSFeedSettingsModel -> {
                    context.addLog("Setup started for RSSFeedSettingsModel.")
                    setUpRssFeedComponents(asset)
                    duration = asset.duration
                }
                is RSSFeedCompoundSettingsModel -> {
                    context.addLog("Setup started for RSSFeedCompoundSettingsModel.")
                    setUpRssCompoundComponent(asset)
                    duration = asset.duration
                }
            }
            Thread.sleep(duration * 100)
        }
    }

    private fun applyStyles() {
        val height: Int = getHeightByPercent(context, coordinatesModel.height)
        val width: Int = getWidthByPercent(context, coordinatesModel.width)
        val abslayoutParams: AbsoluteLayout.LayoutParams

        if (width != 0 && height != 0) {
            abslayoutParams = AbsoluteLayout.LayoutParams(
                width,
                height,
                getWidthByPercent(
                    context,
                    coordinatesModel.x!!.toDouble()
                ),
                getHeightByPercent(
                    context,
                    coordinatesModel.y!!.toDouble()
                ),
            )
        } else if (width == 0 && height != 0) {
            abslayoutParams = AbsoluteLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                height,
                getWidthByPercent(
                    context,
                    coordinatesModel.x!!.toDouble()
                ),
                getHeightByPercent(
                    context,
                    coordinatesModel.y!!.toDouble()
                ),
            )
        } else if (width != 0 && height == 0) {
            abslayoutParams = AbsoluteLayout.LayoutParams(
                width,
                LinearLayout.LayoutParams.MATCH_PARENT,
                getWidthByPercent(
                    context,
                    coordinatesModel.x!!.toDouble()
                ),
                getHeightByPercent(
                    context,
                    coordinatesModel.y!!.toDouble()
                ),
            )
        } else {
            abslayoutParams = AbsoluteLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT,
                getWidthByPercent(
                    context,
                    coordinatesModel.x!!.toDouble()
                ),
                getHeightByPercent(
                    context,
                    coordinatesModel.y!!.toDouble()
                ),
            )
        }
        this.layoutParams = abslayoutParams
    }

    private fun setUpRssCompoundComponent(
        asset: RSSFeedCompoundSettingsModel
    ) {
        this.apply {
            addView(
                RSSFeedViewCompoundComponents(
                    context,
                    null,
                    asset
                )
            )
        }
    }

    private fun setYoutubeViewComponent(
        youTubeViewModel: YoutubeViewModel
    ) {
        var youtubeViewComponent =
            context?.let { it1 -> YoutubeViewComponent(it1, null, youTubeViewModel) }
        youtubeViewComponent?.let { youtubeViewComponent ->
            this.addView(youtubeViewComponent)
        }
    }

    private fun setUpImageListCompoundComponent(
        asset: ImageListCompoundModel
    ) {
        var imageListCompoundComponent =
            context?.let { it1 -> ImageListCompoundComponent(it1, null, asset) }
        imageListCompoundComponent?.let { imageListComponent ->
            this.addView(imageListComponent)
        }
    }

    private fun setUpVideoListCompoundComponent(
        asset: VideoListSettingsModel
    ) {
        var videoListCompoundComponent =
            context?.let { it1 -> VideoListCompoundComponent(it1, null, asset) }
        videoListCompoundComponent?.let { videoListComponent ->
            this.addView(videoListComponent)
        }
    }

    private fun setUpRssFeedComponents( asset: RSSFeedSettingsModel) {
        this.apply {
            addView(
                RSSFeedViewComponent(
                    context,
                    null,
                    asset
                )
            )
        }
    }

    private fun setUpImageViewComponents(imageViewModel: ImageViewModel) {
        val imageViewComponent: ImageViewComponent? =
            context?.let { it1 -> ImageViewComponent(it1, null, imageViewModel) }
        imageViewComponent?.let { imageViewComponent ->
            this.addView(imageViewComponent)
        }
    }

    private fun setUpNativeVideoViewComponents(asset: VideoSettingsModel) {
        var url = asset.url.split(".")
        this.apply {
            addView(
                VideoViewComponent(
                    context, null, asset
                )
            )
        }
    }

    private fun setUpTextViewComponents( asset: TextViewSettingModel) {
        val listOfString = asset.text.split("*")
        val textViewSettingList = arrayListOf<TextViewSettingModel>().apply {
            add(
                asset
            )
        }

        for (i in textViewSettingList.indices) {
            val textViewSetting = textViewSettingList.get(i)
            this.addView(
                TextViewComponent(
                context
            ).apply {
                layoutParams = setLayoutParamsOfText(textViewSetting)
                setDirection(textViewSetting.direction)
                text = textViewSetting.text
                setTextColor(
                    textViewSetting.fontColor
                )
                textSize = textViewSetting.fontSize
                setBackgroundColor(textViewSetting.background)
                setSpeed(textViewSetting.speed.value)
                setDelayed(0)
            })
        }
    }

    private fun setLayoutParamsOfText(textViewSettingModel: TextViewSettingModel): AbsoluteLayout.LayoutParams {
        val height: Int = getHeightByPercent(context, textViewSettingModel.height)
        textViewSettingModel.heightValue = height
        val width: Int = getWidthByPercent(context, textViewSettingModel.width)
        textViewSettingModel.widthValue = width
        textViewSettingModel.widthValue.let {
            val layoutParams: AbsoluteLayout.LayoutParams
            if (it != 0 && textViewSettingModel.heightValue != 0) {
                layoutParams = AbsoluteLayout.LayoutParams(
                    textViewSettingModel.widthValue!!,
                    textViewSettingModel.heightValue!!,
                    getWidthByPercent(
                        context,
                        textViewSettingModel.xValue!!.toDouble()
                    ),
                    getHeightByPercent(
                        context,
                        textViewSettingModel.yValue!!.toDouble()
                    ),
                )
            } else if (it == 0 && textViewSettingModel.heightValue != 0) {
                layoutParams = AbsoluteLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    textViewSettingModel.heightValue!!,
                    getWidthByPercent(
                        context,
                        textViewSettingModel.xValue!!.toDouble()
                    ),
                    getHeightByPercent(
                        context,
                        textViewSettingModel.yValue!!.toDouble()
                    ),
                )
            } else if (it != 0 && textViewSettingModel.heightValue == 0) {
                layoutParams = AbsoluteLayout.LayoutParams(
                    textViewSettingModel.widthValue!!,
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    getWidthByPercent(
                        context,
                        textViewSettingModel.xValue!!.toDouble()
                    ),
                    getHeightByPercent(
                        context,
                        textViewSettingModel.yValue!!.toDouble()
                    ),
                )
            } else {
                layoutParams = AbsoluteLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    getWidthByPercent(
                        context,
                        textViewSettingModel.xValue!!.toDouble()
                    ),
                    getHeightByPercent(
                        context,
                        textViewSettingModel.yValue!!.toDouble()
                    ),
                )
            }

            return layoutParams
        }
    }

}