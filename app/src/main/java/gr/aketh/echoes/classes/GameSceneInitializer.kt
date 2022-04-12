package gr.aketh.echoes.classes

import android.util.Log
import org.json.JSONArray
import org.json.JSONObject

class GameSceneInitializer(json: JSONObject)
{
    lateinit var json: JSONObject
    var jsonMapped = mutableMapOf<String,Any?>()

    init
    {
        Log.d("Inita","Initizalized correctly")

        this.json = json
        jsonArrayToMutableMap(json)
    }

    fun JSONObject.toMap(): Map<String, Any?> = keys().asSequence().associateWith {
        when (val value = this[it])
        {
            is JSONArray ->
            {
                val map = (0 until value.length()).associate { Pair(it.toString(), value[it]) }
                JSONObject(map).toMap().values.toList()
            }
            is JSONObject -> value.toMap()
            JSONObject.NULL -> null
            else            -> value
        }
    }

    fun jsonArrayToMutableMap(jsonObject: JSONObject)
    {

        var rMap = jsonObject.toMap()
        //var rMapList = rMap["data"] as




        /*
        for(i in 0 until jsonArray.length())
        {
            //Circle Properties
            val circleProperties = jsonArray.getJSONObject(i).getJSONObject("circleProperties")
            jsonMapped["circleProperties"]
            //jsonMapped["circleProperties"]["circle_center"]["lat"]
            //Circle Center
            val circle_center = circleProperties.getJSONObject("circle_center")

            //Lat and lon
            val circle_center_lat = circle_center.getDouble("lat")
            val circle_center_lon = circle_center.getDouble("lon")
            //............................................................

            //Circle radius
            val circle_radius = circleProperties.getDouble("circle_radius")

            //Circle color
            val circle_color = circleProperties.getString("circle_color")
        }
        */
        //rMapList

        //HERE!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        //Log.d("Is this true", rMapList!!::class.simpleName.toString())


    }

    fun addCirclesToMap()
    {

    }
}