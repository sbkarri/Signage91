package com.app.signage91.componentfragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsoluteLayout
import android.widget.LinearLayout
import com.app.signage91.R
import com.app.signage91.RSSFeedCompoundSettingsModel
import com.app.signage91.RSSFeedSettingsModel
import com.app.signage91.TextViewSettingModel
import com.app.signage91.URLDataModel
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
import com.app.signage91.databinding.FragmentAssetBinding
import com.app.signage91.databinding.FragmentComponentNewBinding
import com.app.signage91.helpers.addLog
import com.app.signage91.helpers.getHeightByPercent
import com.app.signage91.helpers.getWidthByPercent
import com.app.signage91.helpers.setLayoutParamsOfSection
import com.app.signage91.models.CampaignNewObject
import com.app.signage91.models.CoordinatesModel
import com.app.signage91.models.ImageListCompoundModel
import com.app.signage91.models.ImageViewModel
import com.app.signage91.models.YoutubeViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONObject
import javax.inject.Scope

class AssetFragment : Fragment() {

    private var _binding: FragmentAssetBinding? = null
    private val binding get() = _binding
    private var assetList: List<Any>? = null
    private var youtubeViewComponent: YoutubeViewComponent? = null
    private var isPause: Boolean = false
    var layout: AbsoluteLayout? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAssetBinding.inflate(inflater, container, false)
        Log.d("ThisFragment", this.toString())
        return binding?.root
    }

    companion object {
        fun newInstance(assetLists: List<Any>) : AssetFragment {
            val fragment = AssetFragment()
            fragment.assetList = assetLists
            Log.d("Fragment", fragment.toString())
            Log.d("AssetsInstanceList", fragment.assetList!!.toString())
//            fragment.assetslist()
            return fragment
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("OnViewFragment", this.toString())

        assetslist()
//        setAssetsList()
    }

    fun assetslist(){
        assetList.let {
            val secondaryJob = CoroutineScope(Dispatchers.Main).launch {
                setAssetsList()
            }
            secondaryJob.start()
        }
    }

    private suspend fun setAssetsList() {
        Log.d("AssetsLoadingList", this.assetList!!.size.toString())
        Log.d("AssetsLoadingList", this.assetList!!.toString())
        this.assetList!!.forEach { asset ->
            var duration = 10L
            binding!!.assetLayout.removeAllViews()
            layout = AbsoluteLayout(requireActivity())
            layout!!.layoutParams = AbsoluteLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT,
                getWidthByPercent(
                    context,
                    0.0
                ),
                getHeightByPercent(
                    context,
                    0.0
                ),
            )
            when (asset) {
                is ImageListCompoundModel -> {
                    asset.xValue = 0.0
                    asset.yValue = 0.0
                    requireActivity().addLog("Setup started for ImageListCompoundModel.")
                    setUpImageListCompoundComponent(layout!!, asset)
                    duration = asset.duration
                }
                is VideoListSettingsModel -> {
//                    asset.width = 0.0
//                    asset.height = 0.0
                    requireActivity().addLog("Setup started for VideoListSettingsModel.")
                    setUpVideoListCompoundComponent(layout!!, asset)
                    duration = asset.duration
                }
                is ImageViewModel -> {
//                    asset.width = 0.0
//                    asset.height = 0.0
                    requireActivity().addLog("Setup started for ImageViewModel.")
                    setUpImageViewComponents(layout!!, asset)
                    duration = asset.duration
                }
                is VideoSettingsModel -> {
//                    asset.width = 0.0
//                    asset.height = 0.0
                    requireActivity().addLog("Setup started for VideoSettingsModel.")
                    Log.d("Video Model", asset.fileName)
                    setUpNativeVideoViewComponents(layout!!, asset)
                    duration = asset.duration
                }
                is YoutubeViewModel -> {
//                    asset.width = 0.0
//                    asset.height = 0.0
                    requireActivity().addLog("Setup started for YoutubeViewModel.")
                    setYoutubeViewComponent(layout!!, asset)
                    duration = asset.duration
                }
                is TextViewSettingModel -> {
//                    asset.width = 0.0
//                    asset.height = 0.0
                    requireActivity().addLog("Setup started for TextViewSettingModel.")
                    setUpTextViewComponents(layout!!, asset)
                    duration = asset.duration
                }
                is RSSFeedSettingsModel -> {
//                    asset.width = 0.0
//                    asset.height = 0.0
                    requireActivity().addLog("Setup started for RSSFeedSettingsModel.")
                    setUpRssFeedComponents(layout!!, asset)
                    duration = asset.duration
                }
                is RSSFeedCompoundSettingsModel -> {
//                    asset.width = 0.0
//                    asset.height = 0.0
                    requireActivity().addLog("Setup started for RSSFeedCompoundSettingsModel.")
                    setUpRssCompoundComponent(layout!!, asset)
                    duration = asset.duration
                }
            }
            binding!!.assetLayout.addView(layout)
            delay(duration*100)
        }
    }

    private fun setUpRssCompoundComponent(
        layout: AbsoluteLayout,
        asset: RSSFeedCompoundSettingsModel
    ) {
        layout.apply {
            addView(
                RSSFeedViewCompoundComponents(
                    requireContext(),
                    null,
                    asset
                )
            )
        }
    }

    private fun setYoutubeViewComponent(
        layout: AbsoluteLayout,
        youTubeViewModel: YoutubeViewModel
    ) {
        youtubeViewComponent =
            context?.let { it1 -> YoutubeViewComponent(it1, null, youTubeViewModel) }
        youtubeViewComponent?.let { youtubeViewComponent ->
            layout.addView(youtubeViewComponent)
        }
    }

    private fun setUpImageListCompoundComponent(
        layout: AbsoluteLayout,
        asset: ImageListCompoundModel
    ) {
        var imageListCompoundComponent =
            context?.let { it1 -> ImageListCompoundComponent(it1, null, asset) }
        imageListCompoundComponent?.let { imageListComponent ->
            layout.addView(imageListComponent)
        }
    }

    private fun setUpVideoListCompoundComponent(
        layout: AbsoluteLayout,
        asset: VideoListSettingsModel
    ) {
        var videoListCompoundComponent =
            context?.let { it1 -> VideoListCompoundComponent(it1, null, asset) }
        videoListCompoundComponent?.let { videoListComponent ->
            layout.addView(videoListComponent)
        }
    }

    private fun setUpRssFeedComponents(layout: AbsoluteLayout, asset: RSSFeedSettingsModel) {
        layout.apply {
            addView(
                RSSFeedViewComponent(
                    requireContext(),
                    null,
                    asset
                )
            )
        }
    }

    private fun setUpImageViewComponents(layout: AbsoluteLayout, imageViewModel: ImageViewModel) {
        val imageViewComponent: ImageViewComponent? =
            context?.let { it1 -> ImageViewComponent(it1, null, imageViewModel) }
        imageViewComponent?.let { imageViewComponent ->
            layout.addView(imageViewComponent)
        }
    }

    private fun setUpNativeVideoViewComponents(layout: AbsoluteLayout, asset: VideoSettingsModel) {
        var url = asset.url.split(".")
        layout.apply {
            addView(
                VideoViewComponent(
                    requireContext(), null, asset
                )
            )
        }
    }

    private fun setUpTextViewComponents(layout: AbsoluteLayout, asset: TextViewSettingModel) {
        val listOfString = asset.text.split("*")
        val textViewSettingList = arrayListOf<TextViewSettingModel>().apply {
            add(
                asset
            )
        }

        for (i in textViewSettingList.indices) {
            val textViewSetting = textViewSettingList.get(i)
            layout.addView(
                TextViewComponent(
                requireContext()
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

    override fun onResume() {
        super.onResume()
        if (isPause) {
            isPause = false
            youtubeViewComponent?.let {
                it.resumeTimers()
                it.onResume()
                it.setYoutubeView()
            }
        }
//        throw RuntimeException("onResume force exception")
    }

    override fun onPause() {
        super.onPause()
        isPause = true
        youtubeViewComponent?.let {
            it.pauseTimers()
            it.onPause()
        }
    }
}