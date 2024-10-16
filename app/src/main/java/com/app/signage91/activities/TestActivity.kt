package com.app.signage91.activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.AbsoluteLayout
import com.app.signage91.componentfragments.ComponentNewFragment
import com.app.signage91.base.BaseActivity
import com.app.signage91.databinding.ActivityTestBinding
import com.app.signage91.helpers.addLog
import com.app.signage91.helpers.getCorrdinatesObject
import com.app.signage91.helpers.getJsonObject
import com.app.signage91.helpers.setLayoutParamsOfSection
import com.app.signage91.models.CoordinatesModel
import com.app.signage91.models.SectionObject
import org.json.JSONObject

class TestActivity : BaseActivity() {
    var sectionDataList:MutableList<SectionObject> = mutableListOf()
    private var binding: ActivityTestBinding? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        binding = ActivityTestBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        getSectionsData()

    }

    private fun getSectionsData() {
        addLog("Parsing Section Data Json started.")
        val responseObject: JSONObject = getJsonObject("sections.json")

        if (responseObject.getInt("statusCode") == 200) {
            // Success scenario
            sectionDataList = mutableListOf()
            parseSectionDataList(sectionDataList, responseObject)
        }
    }

    private fun parseSectionDataList(
        sectionDataList: MutableList<SectionObject>,
        responseObject: JSONObject
    ) {
        if (responseObject.has("data")){
            val dataObject = responseObject.getJSONObject("data")
            if (dataObject.has("sectionDetails")){
                val sectionArray = dataObject.getJSONArray("sectionDetails")
                for (s in 0 until sectionArray.length()){
                    val sectionObject = sectionArray.getJSONObject(s)
                    val coordinateObject: JSONObject = sectionObject.getJSONObject("coordinates")
                    var coordinatesModel: CoordinatesModel = getCorrdinatesObject(coordinateObject)
                    val apiObject = sectionObject.getJSONObject("api")
                    var sectionModel : SectionObject = SectionObject(
                        sectionObject.getString("sectionName"),
                        sectionObject.getString("sectionId"),
                        apiObject.getString("baseUrl"),
                        apiObject.getString("url"),
                        coordinatesModel
                    )
                    sectionDataList.add(sectionModel)
                }
                setSectionsDataInLayout()
            }
        }
    }

    @SuppressLint("ResourceType")
    private fun setSectionsDataInLayout() {
        // As of now adding the sections here
        // Once backend is added, need to add the sections based on the response so that we can pass the data as well
        if (sectionDataList.size > 0){
            for (s in 0 until  sectionDataList.size){
                val sectionObject = sectionDataList[s]
                var dataSource:JSONObject = JSONObject()
                if (sectionObject.sectionName == "Primary Advertisement") {
                    dataSource = getJsonObject("AdvertisementSectionDataSource.json")
                } else if (sectionObject.sectionName == "91Signage Section" ){ //|| sectionObject.sectionName == "Youtube Section"
                    dataSource = getJsonObject("91SignageSectionDataSource.json")
                }
//                else if (sectionObject.sectionName == "Classified Section"){
//                    dataSource = getJsonObject("CollaboratorSectionDataSource.json")
//                }
                val frameLayout = AbsoluteLayout(this)
                Log.d("FrameLayout", frameLayout.toString())
                frameLayout.id = s+1
                frameLayout.layoutParams = setLayoutParamsOfSection(this, sectionObject.coordinates)
                Log.d("FrameLayout", frameLayout.id.toString())
                supportFragmentManager.beginTransaction()
                .add(
                    frameLayout.id,
                    ComponentNewFragment.newInstance(dataSource)
                ).commit()
                binding!!.activityFrame.addView(frameLayout)
            }
        }
    }
}