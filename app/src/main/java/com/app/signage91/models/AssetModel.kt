package com.app.signage91.models

data class AssetModel(
    val assetType : String,
    val url : String,
    val duration : Int,
    val mute : Boolean,
    val coordinates : CoordinatesModel
)

data class CoordinatesModel(
    val x : Double,
    val y : Double,
    val width : Double,
    val height : Double
    )

data class CampaignObject (
    val id: Int,
    val campaignName : String,
    val duration:Long,
    val assets : List<Any>
        )

// New Changes
data class SectionObject(
    val sectionName:String,
    val sectionId:String,
    val apiBaseUrl:String,
    val apiEndPoint:String,
    val coordinates: CoordinatesModel
)

data class AdvertisementObject(
    val id : Int,
    val duration: Long,
    val name: String,
    val assetCoordinatesList : List<AssetCoordinatedObject>
)

data class AssetCoordinatedObject(
    val coordinates: CoordinatesModel,
    val assetsList : List<Any>
)

data class CampaignNewObject (
    val id: Int,
    val campaignName : String,
    val sequence:Int,
    val advertisementObject: AdvertisementObject? = null
)