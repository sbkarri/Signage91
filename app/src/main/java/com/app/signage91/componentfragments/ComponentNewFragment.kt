package com.app.signage91.componentfragments

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsoluteLayout
import androidx.fragment.app.Fragment
import com.app.signage91.RSSFeedCompoundSettingsModel
import com.app.signage91.RSSFeedSettingsModel
import com.app.signage91.TEXT_VIEW_SCROLLING_SPPED
import com.app.signage91.TextViewSettingModel
import com.app.signage91.URLDataModel
import com.app.signage91.VideoListSettingsModel
import com.app.signage91.VideoSettingsModel
import com.app.signage91.components.TextViewComponent
import com.app.signage91.components.YoutubeViewComponent
import com.app.signage91.databinding.FragmentComponentNewBinding
import com.app.signage91.helpers.getCorrdinatesObject
import com.app.signage91.helpers.getFileName
import com.app.signage91.helpers.setLayoutParamsOfSection
import com.app.signage91.models.AdvertisementObject
import com.app.signage91.models.AssetCoordinatedObject
import com.app.signage91.models.CampaignNewObject
import com.app.signage91.models.CoordinatesModel
import com.app.signage91.models.ImageListCompoundModel
import com.app.signage91.models.ImageViewModel
import com.app.signage91.models.YoutubeViewModel
import com.app.signage91.utils.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.util.Random

class ComponentNewFragment : Fragment() {
    private var _binding: FragmentComponentNewBinding? = null
    private val binding get() = _binding
    var dataSource: JSONObject = JSONObject()
    private var imageFilesList: MutableList<URLDataModel>? = mutableListOf()
    private var videoFilesList: MutableList<URLDataModel>? = mutableListOf()
    private var assetList: MutableList<Any>? = null
    private var campaignList: MutableList<CampaignNewObject>? = mutableListOf()
    private var mediaList: MutableList<AdvertisementObject>? = mutableListOf()
    private var youtubeViewComponent: YoutubeViewComponent? = null
    private var isPause: Boolean = false
    var layout: AbsoluteLayout? = null

    companion object {
        fun newInstance(data : JSONObject): ComponentNewFragment {
            val fragment = ComponentNewFragment()
            fragment.dataSource = data
            Log.d("Fragment", fragment.toString())
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentComponentNewBinding.inflate(inflater, container, false)
        Log.d("ThisFragment", this.toString())
        return binding?.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("OnViewFragment", this.toString())

        parseDataObject()
    }

    private fun parseDataObject() {
        if (dataSource.has("data")){
            val dataObject = dataSource.getJSONObject("data")
            if (dataObject.has("sectionDetails")){
                val sectionObject = dataObject.getJSONObject("sectionDetails")
                if (sectionObject.has("hasCampaign") && sectionObject.getBoolean("hasCampaign")){
                    val campaignArray = dataObject.getJSONArray("campaigns")
                    campaignList = mutableListOf()
                    var assestsCoordinatesList = mutableListOf<AssetCoordinatedObject>()
                    for (c in 0 until campaignArray.length()){
                        val campaignObject = campaignArray.getJSONObject(c)
                        val advertisementObject = campaignObject.getJSONObject("advertisementDetails")
                        val assetCoordinateArray = advertisementObject.getJSONArray("assetCoordinates")
                        assestsCoordinatesList = getAssetsCoordinateList(assetCoordinateArray, advertisementObject.getLong("duration"))

                        val advertisement = AdvertisementObject(
                            advertisementObject.getInt("id"),
                            advertisementObject.getLong("duration"),
                            advertisementObject.getString("name"),
                            assestsCoordinatesList
                        )

                        val campaignModel = CampaignNewObject(
                            campaignObject.getInt("id"),
                            campaignObject.getString("name"),
                            campaignObject.getInt("sequence"),
                            advertisement
                        )
                        campaignList!!.add(campaignModel)
                    }
                    // Set data to the layout
                    setCampaignDataAsPerResponse()
                }
                else {
                    val mediaArray = dataObject.getJSONArray("media")
                    mediaList = mutableListOf()
                    var assestsCoordinatesList = mutableListOf<AssetCoordinatedObject>()
                    for (c in 0 until mediaArray.length()){
                        val mediaObject = mediaArray.getJSONObject(c)
                        val assetCoordinateArray = mediaObject.getJSONArray("assetCoordinates")
                        assestsCoordinatesList = getAssetsCoordinateList(assetCoordinateArray, mediaObject.getLong("duration"))

                        val advertisement = AdvertisementObject(
                            mediaObject.getInt("id"),
                            mediaObject.getLong("duration"),
                            mediaObject.getString("name"),
                            assestsCoordinatesList
                        )
                        mediaList!!.add(advertisement)
                    }
                    // Set data to the layout
                    setMediaDataAsPerResponse()
                }
            }
        }
    }

    private fun getAssetsCoordinateList(assetCoordinateArray: JSONArray, dataDuration: Long): MutableList<AssetCoordinatedObject> {
        var assetCoordinateListTemp = mutableListOf<AssetCoordinatedObject>()
        for (ac in 0 until assetCoordinateArray.length()){
            val coordinatesModel = getCorrdinatesObject(assetCoordinateArray.getJSONObject(ac).getJSONObject("coordinates"))
            val assetsArray :JSONArray = assetCoordinateArray.getJSONObject(ac).getJSONArray("assets")
            assetList = mutableListOf()
            for (a in 0 until assetsArray.length()) {
                val assetObject: JSONObject = assetsArray.getJSONObject(a)
                when (assetObject.getString("type")) {
                    Constants.IMAGE -> {
                        var imageViewModel: ImageViewModel = ImageViewModel(
                            assetObject.getString("url"),
                            coordinatesModel.width,
                            coordinatesModel.height,
                            false,
                            "FitXY",
                            getFileName(assetObject, assetObject.getString("url")),
                            0.0,
                            0.0,
                            assetObject.getLong("duration"),
                            assetObject.getBoolean("isPrimary")
                        )
                        assetList!!.add(imageViewModel)
                        var urlDataModel = URLDataModel(
                            assetObject.getString("url"),
                            getFileName(assetObject, assetObject.getString("url"))
                        )
                        imageFilesList!!.add(urlDataModel)
                    }
                    Constants.VIDEO -> {
                        var videoViewModel: VideoSettingsModel = VideoSettingsModel(
                            assetObject.getString("url"),
                            coordinatesModel.width,
                            coordinatesModel.height,
                            getFileName(assetObject, assetObject.getString("url")),
                            0,
                            0,
                            0.0,
                            0.0,
                            assetObject.getLong("duration"),
                            assetObject.getBoolean("isPrimary")
                        )
                        assetList!!.add(videoViewModel)
                        var urlDataModel = URLDataModel(
                            assetObject.getString("url"),
                            getFileName(assetObject, assetObject.getString("url"))
                        )
                        videoFilesList!!.add(urlDataModel)
                    }
                    Constants.YOUTUBE -> {
                        var youtubeViewModel: YoutubeViewModel = YoutubeViewModel(
                            assetObject.getString("url"),
                            coordinatesModel.width,
                            coordinatesModel.height,
                            0,
                            0,
                            0.0,
                            0.0,
                            assetObject.getLong("duration"),
                            assetObject.getBoolean("isPrimary")
                        )
                        assetList!!.add(youtubeViewModel)
                    }
                    Constants.TEXTSCROLL -> {
                        var textViewScrollingSpeed = TEXT_VIEW_SCROLLING_SPPED.LOW
                        if (assetObject.has("speed")) {
                            when (assetObject.getString("speed")) {
                                "LOW" -> textViewScrollingSpeed =
                                    TEXT_VIEW_SCROLLING_SPPED.LOW
                                "HIGH" -> textViewScrollingSpeed =
                                    TEXT_VIEW_SCROLLING_SPPED.HIGH
                                "MEDIUM" -> textViewScrollingSpeed =
                                    TEXT_VIEW_SCROLLING_SPPED.MEDIUM
                            }
                        }
                        var textViewDirection = TextViewComponent.Direction.LEFT
                        if (assetObject.has("direction")) {
                            val direction = assetObject.getString("direction")
                            if (direction == "LEFT_TO_RIGHT") {
                                textViewDirection = TextViewComponent.Direction.RIGHT
                            } else if (direction == "RIGHT_TO_LEFT") {
                                textViewDirection = TextViewComponent.Direction.LEFT
                            } else if (direction == "TOP_TO_BOTTOM") {
                                textViewDirection = TextViewComponent.Direction.DOWN
                            } else if (direction == "BOTTOM_TO_TOP") {
                                textViewDirection = TextViewComponent.Direction.UP
                            }
                        }
                        val textViewSettingModel: TextViewSettingModel = TextViewSettingModel(
                            assetObject.getString("text"),
                            coordinatesModel.width,
                            coordinatesModel.height,
                            textViewDirection,
                            assetObject.getLong("duration"),
                            textViewScrollingSpeed,
                            Color.WHITE,
                            16f,
                            Color.BLACK,
                            0,
                            0,
                            assetObject.getBoolean("isPrimary"),
                            0.0,
                            0.0
                        )
                        assetList!!.add(textViewSettingModel)
                    }
                    Constants.RSS_FEED -> {
                        var rssFeedSettingsModel: RSSFeedSettingsModel = RSSFeedSettingsModel(
                            assetObject.getString("url"),
                            coordinatesModel.width,
                            coordinatesModel.height,
                            0,
                            0,
                            0.0,
                            0.0,
                            assetObject.getLong("duration"),
                            assetObject.getBoolean("isPrimary")
                        )
                        assetList!!.add(rssFeedSettingsModel)
                    }
                    Constants.RSS_IMAGE_FEED -> {
                        var rssFeedCompoundSettingsModel: RSSFeedCompoundSettingsModel =
                            RSSFeedCompoundSettingsModel(
                                assetObject.getString("url"),
                                coordinatesModel.width,
                                coordinatesModel.height,
                                assetObject.getLong("slidingDuration"),
                                0,
                                0,
                                0.0,
                                0.0,
                                assetObject.getLong("duration"),
                                assetObject.getBoolean("isPrimary")
                            )
                        assetList!!.add(rssFeedCompoundSettingsModel)
                    }
                    Constants.IMAGE_LIST -> {
                        var urlList = mutableListOf<URLDataModel>()
                        var urlJSONArray: JSONArray = assetObject.getJSONArray("urls")
                        for (u in 0 until urlJSONArray.length()) {
                            var urlObject = urlJSONArray.getJSONObject(u)
                            var urlDataModel = URLDataModel(
                                urlObject.getString("url"),
                                getFileName(urlObject, urlObject.getString("url"))
                            )
                            urlList.add(urlDataModel)
                            imageFilesList!!.add(urlDataModel)
                        }
                        var imageDuration = (dataDuration / urlList.size).toLong()
                        Log.d("ImageDurationCal", "Camp duration ${dataDuration}  ImageList Size ${urlList.size}")
                        Log.d("ImageDuration", "Image duration $imageDuration")
                        var imageListCompoundModel: ImageListCompoundModel =
                            ImageListCompoundModel(
                                urlList,
                                coordinatesModel.width,
                                coordinatesModel.height,
                                false,
                                "FitXY",
                                "",
                                0.0,
                                0.0,
                                imageDuration,
                                assetObject.getBoolean("isPrimary")
                            )
                        assetList!!.add(imageListCompoundModel)
                    }
                    Constants.VIDEO_LIST -> {
                        var urlList = mutableListOf<URLDataModel>()
                        var urlJSONArray: JSONArray = assetObject.getJSONArray("urls")
                        for (u in 0 until urlJSONArray.length()) {
                            var urlObject = urlJSONArray.getJSONObject(u)
                            var urlDataModel = URLDataModel(
                                urlObject.getString("url"),
                                getFileName(urlObject, urlObject.getString("url"))
                            )
                            urlList.add(urlDataModel)
                            videoFilesList!!.add(urlDataModel)
                        }
                        var videoListSettingsModel: VideoListSettingsModel =
                            VideoListSettingsModel(
                                urlList,
                                coordinatesModel.width,
                                coordinatesModel.height,
                                "",
                                0,
                                0,
                                coordinatesModel.x,
                                coordinatesModel.y,
                                assetObject.getLong("duration"),
                                assetObject.getBoolean("isPrimary")
                            )
                        assetList!!.add(videoListSettingsModel)
                    }
                }
            }
            val assetsCoordinatedModel = AssetCoordinatedObject(
                coordinatesModel,
                assetList!!
            )

            assetCoordinateListTemp.add(assetsCoordinatedModel)
        }
        return assetCoordinateListTemp
    }

    private fun setMediaDataAsPerResponse() {
        mediaList.let {
            val primaryJob = CoroutineScope(Dispatchers.Main).launch {
                setMediaDataRerun()
            }
            primaryJob.start()
        }
    }

    private suspend fun setMediaDataRerun() {
        while (true){
            Log.e("Media", "Primary Job started")
            mediaList?.forEachIndexed { campIndex, media ->
                val assestCoordinateList = media.assetCoordinatesList
                binding!!.mainLayout.removeAllViews()
                Log.d("assetList", assestCoordinateList.toString())
//                setMediaDataToLayout(assestCoordinateList)
                val frameLayout = AbsoluteLayout(requireContext())
                Log.d("MediaFrameLayout", frameLayout.toString())
                frameLayout.id = Random().nextInt(1000)
                val coordinatesModel = CoordinatesModel(
                    0.0,
                    50.0,
                    50.0,
                    25.0
                )
                frameLayout.layoutParams = requireActivity().setLayoutParamsOfSection(requireContext(), coordinatesModel)
                Log.d("MediaFrameLayout", frameLayout.id.toString())
                childFragmentManager.beginTransaction()
                    .add(
                        frameLayout.id,
                        AssetFragment.newInstance(assestCoordinate.assetsList)
                    ).commit()
                delay(media.duration.toLong()*100)
            }
        }
    }

    private fun setMediaDataToLayout(assestCoordinateList: List<AssetCoordinatedObject>) {
            assestCoordinateList.forEachIndexed { index, assestCoordinate ->
                val coordinatesModel = assestCoordinate.coordinates

                val frameLayout = AbsoluteLayout(requireContext())
                Log.d("MediaFrameLayout", frameLayout.toString())
                frameLayout.id = Random().nextInt(1000)
                frameLayout.layoutParams = requireActivity().setLayoutParamsOfSection(requireContext(), coordinatesModel)
                Log.d("MediaFrameLayout", frameLayout.id.toString())
                childFragmentManager.beginTransaction()
                    .add(
                        frameLayout.id,
                        AssetFragment.newInstance(assestCoordinate.assetsList)
                    ).commit()
                binding!!.mainLayout.addView(frameLayout)

               /*    if (assestCoordinateList.size == index+1){
                       Thread{
                        Thread.sleep(campaign.advertisementObject.duration.toLong()*100)
                    }.start()
                    Handler().postDelayed(Runnable {
                        onResume()
                    }, campaign.advertisementObject.duration.toLong()*100)
                    onPause()
                    delay(campaign.advertisementObject.duration.toLong()*100)
//                    Thread.sleep(campaign.advertisementObject.duration.toLong()*100)
//                    break@loop

//                    break@outerLoop
                }

                                var assetsComponent =
                                    context?.let { it1 -> AssetComponents(it1, null, assestCoordinate.assetsList, coordinatesModel) }
                                assetsComponent?.let { assetComponent ->
                                    binding!!.mainLayout.addView(assetComponent)
                                }


                 Thread{
                       //The loop only repeat 4 times to avoid a crash
                       requireActivity().runOnUiThread(java.lang.Runnable {
                           assestCoordinate.assetsList.forEach { asset ->
                               layout = AbsoluteLayout(requireActivity())
                               layout!!.layoutParams = requireActivity().setLayoutParamsOfSection(requireActivity(), coordinatesModel)
                               var duration = 10L
                               when (asset) {
                                   is ImageListCompoundModel -> {
                                       requireActivity().addLog("Setup started for ImageListCompoundModel.")
                                       setUpImageListCompoundComponent(layout!!, asset)
                                       duration = asset.duration
                                   }
                                   is VideoListSettingsModel -> {
                                       requireActivity().addLog("Setup started for VideoListSettingsModel.")
                                       setUpVideoListCompoundComponent(layout!!, asset)
                                       duration = asset.duration
                                   }
                                   is ImageViewModel -> {
                                       requireActivity().addLog("Setup started for ImageViewModel.")
                                       setUpImageViewComponents(layout!!, asset)
                                       duration = asset.duration
                                   }
                                   is VideoSettingsModel -> {
                                       requireActivity().addLog("Setup started for VideoSettingsModel.")
                                       setUpNativeVideoViewComponents(layout!!, asset)
                                       duration = asset.duration
                                   }
                                   is YoutubeViewModel -> {
                                       requireActivity().addLog("Setup started for YoutubeViewModel.")
                                       setYoutubeViewComponent(layout!!, asset)
                                       duration = asset.duration
                                   }
                                   is TextViewSettingModel -> {
                                       requireActivity().addLog("Setup started for TextViewSettingModel.")
                                       setUpTextViewComponents(layout!!, asset)
                                       duration = asset.duration
                                   }
                                   is RSSFeedSettingsModel -> {
                                       requireActivity().addLog("Setup started for RSSFeedSettingsModel.")
                                       setUpRssFeedComponents(layout!!, asset)
                                       duration = asset.duration
                                   }
                                   is RSSFeedCompoundSettingsModel -> {
                                       requireActivity().addLog("Setup started for RSSFeedCompoundSettingsModel.")
                                       setUpRssCompoundComponent(layout!!, asset)
                                       duration = asset.duration
                                   }
                               }
                               binding!!.mainLayout.addView(layout!!)
                                               if (assestCoordinateList.size == index+1) {

                                                   delay(duration * 100)
                                               }
                           }
                       })
                   }.start()*/
            }
    }

    private fun setCampaignDataAsPerResponse() {
        campaignList.let {
            val secondaryJob = CoroutineScope(Dispatchers.Main).launch {
                setCampaignDataRerun()
            }
            secondaryJob.start()
        }
    }

    private suspend fun setCampaignDataRerun() {
        while (true) {
            Log.e("Component", "Secondary Job started")
            setCampaignDataToLayout()
        }
    }

    private suspend fun setCampaignDataToLayout() {
        campaignList?.forEachIndexed { campIndex, campaign ->
            val assestCoordinateList = campaign.advertisementObject!!.assetCoordinatesList
            binding!!.mainLayout.removeAllViews()
            setMediaDataToLayout(assestCoordinateList)
            delay(campaign.advertisementObject.duration.toLong()*100)
        }
    }

    override fun onResume() {
        super.onResume()
        if (isPause) {
            isPause = false
        }
    }

    override fun onPause() {
        super.onPause()
        isPause = true
    }
}