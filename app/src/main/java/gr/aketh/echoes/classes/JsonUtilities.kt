package gr.aketh.echoes.classes

import android.content.Context
import android.util.Log
import androidx.collection.arrayMapOf
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.io.InputStream
import java.nio.charset.Charset

object JsonUtilities
{
    //Object structure that contains things i need for reading Json
    lateinit var json: JSONObject
    fun loadJSONFromAsset(context: Context): String?
    {//Loads a json file
        var jsonString: String

        try
        {
            //Choose the correct json file
            var inStream: InputStream = context.assets.open("Content/mainContent_2.json")
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
        //Online code i got to convert the json object to map
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



        //Created all the correct variables that is reads from my json file
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

            val cid = properties.getInt("id")

            Log.d("IDAA",cid.toString())

            //Other stuff music
            val content = circleProperties.getJSONObject("content")

            val musicSoundName = content.getString("sound")

            var content_data_type = content.getString("type")

            //quiz stuff
            if(content_data_type=="quiz")
            {
                val typeData = content.getJSONObject("data")
                //Read the quiz data so I can load it?
                val quiz_question = typeData.getString("question")
                val quiz_answers = arrayOf<String>()
                val quiz_correct_answers = typeData.getInt("correct_answer")
                val quiz_points = typeData.getInt("points")
                val quiz_answers_temp = typeData.getJSONArray("answers")

                //Load all of them into the dict data for cleaner look
                val dict_quiz_data = mutableMapOf<String,Any?>()

                dict_quiz_data["question"] = quiz_question
                dict_quiz_data["answers"] = quiz_answers_temp
                dict_quiz_data["correct_answer"] = quiz_correct_answers
                dict_quiz_data["points"] = quiz_points


                //Load to the main dict
                dict["data"] = dict_quiz_data


            }

            //Combining to one
            dict["circle_center_lat"] = circle_center_lat
            dict["circle_center_lon"] = circle_center_lon
            dict["circle_radius"] = circle_radius
            dict["circle_color"] = circle_color
            dict["cid"] = cid
            dict["sound"] = musicSoundName
            dict["type"] = content_data_type

            dict["running"] = false


            dictList.add(dict)//Adds to dictionary
        }

        //rMapList

        //HERE!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        //Log.d("Is this true", rMapList!!::class.simpleName.toString())


        return dictList
    }

    //Convert json to usable functions and stuff
}