package gr.aketh.echoes.classes

import android.content.Context
import android.util.Log
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.io.InputStream
import java.nio.charset.Charset

object JsonUtilities
{
    lateinit var json: JSONObject
    fun loadJSONFromAsset(context: Context): String?
    {//Loads a json file
        var jsonString: String

        try
        {
            var inStream: InputStream = context.assets.open("Content/mainContent.json")
            var size: Int = inStream.available()
            var buffer: ByteArray = ByteArray(size)
            inStream.read(buffer)
            inStream.close()
            jsonString = String(buffer, Charset.forName("UTF-8"))

        }
        catch (e: IOException)
        {
            e.printStackTrace()
            return null
        }



        return jsonString
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

    fun jsonArrayToMutableMap(jsonObject: JSONObject): MutableList<MutableMap<String,Any?>>
    {
        //var jsonArray: JSONArray = jsonObject.
        var rMap = jsonObject.toMap()
        var rMapD = rMap["data"] as List<*>;
        //var rMapList = rMap["data"] as rMap
        val jsonArray = jsonObject.getJSONArray("data");
        val dictList : MutableList<MutableMap<String,Any?>> = mutableListOf()
        //val dict = mutableMapOf<String,Any?>()



        //rMapD.forEach{ Log.d("Mapp",it.toString())};

        //Log.d("Mapp",rMap["data"][0].toString());




        for(i in 0 until jsonArray.length())
        {
            val dict = mutableMapOf<String,Any?>()
            //Circle Properties
            val circleProperties = jsonArray.getJSONObject(i).getJSONObject("circleProperties")

            val properties = circleProperties.getJSONObject("properties")
            Log.d("properties",properties.toString())
            //jsonMapped["circleProperties"]
            //jsonMapped["circleProperties"]["circle_center"]["lat"]
            //Circle Center
            val circle_center = properties.getJSONObject("circle_center")

            //Lat and lon
            val circle_center_lat = circle_center.getDouble("lat")
            val circle_center_lon = circle_center.getDouble("lon")
            //............................................................

            //Circle radius
            val circle_radius = properties.getDouble("circle_radius")

            //Circle color
            val circle_color = properties.getString("circle_color")

            //Combining to one
            dict["circle_center_lat"] = circle_center_lat
            dict["circle_center_lon"] = circle_center_lon
            dict["circle_radius"] = circle_radius
            dict["circle_color"] = circle_color

            dictList.add(dict)//Adds to dictionary
        }

        //rMapList

        //HERE!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        //Log.d("Is this true", rMapList!!::class.simpleName.toString())


        return dictList
    }

    //Convert json to usable functions and stuff
}