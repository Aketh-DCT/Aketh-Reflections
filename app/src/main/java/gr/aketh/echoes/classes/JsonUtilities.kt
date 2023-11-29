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

            var title_of = content.getString("title");

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
                val quiz_letters = typeData.getString("letters")

                //Load all of them into the dict data for cleaner look
                val dict_quiz_data = mutableMapOf<String,Any?>()

                dict_quiz_data["question"] = quiz_question
                dict_quiz_data["answers"] = quiz_answers_temp
                dict_quiz_data["correct_answer"] = quiz_correct_answers
                dict_quiz_data["points"] = quiz_points
                dict_quiz_data["letters"] = quiz_letters


                //Load to the main dict
                dict["data"] = dict_quiz_data


            }

            //Slizing puzzle init
            else if(content_data_type=="slidingPuzzle")
            {
                val typeData = content.getJSONObject("data")
                //Read the quiz data so I can load it?
                val pieces = arrayOf<Array<Int>>()
                val sliding_puzzle_pieces = typeData.getJSONArray("pieces")
                val sliding_puzzle_letters = typeData.getString("letters")
                Log.d("PIECES --------", sliding_puzzle_pieces.toString())

                val dict_quiz_data = mutableMapOf<String,Any?>()
                dict_quiz_data["letters"] = sliding_puzzle_letters

                dict["data"] = dict_quiz_data
            }

            else if(content_data_type=="camera")
            {
                val typeData = content.getJSONObject("data")
                //Read the quiz data so I can load it?
                val pieces = arrayOf<Array<Int>>()

                val sliding_puzzle_letters = typeData.getString("letters")

                val dict_quiz_data = mutableMapOf<String,Any?>()
                dict_quiz_data["letters"] = sliding_puzzle_letters

                dict["data"] = dict_quiz_data
            }
            else if(content_data_type=="justAnswer")
            {
                val typeData = content.getJSONObject("data")
                val sliding_puzzle_letters = typeData.getString("letters")
                val dict_quiz_data = mutableMapOf<String,Any?>()
                dict_quiz_data["letters"] = sliding_puzzle_letters

                dict["data"] = dict_quiz_data
            }
            else if(content_data_type=="wordSearch"){
                val typeData = content.getJSONObject("data")


                val url = "file:///android_asset/Content/WordSearch/index.html"

                val sliding_puzzle_letters = typeData.getString("letters")
                val dict_quiz_data = mutableMapOf<String,Any?>()
                dict_quiz_data["letters"] = sliding_puzzle_letters

                dict["data"] = dict_quiz_data
                dict["gameUrl"] = url
            }
            else if(content_data_type=="puzzleV2"){
                val typeData = content.getJSONObject("data")


                val url = "file:///android_asset/Content/puzzle.html"

                val sliding_puzzle_letters = typeData.getString("letters")
                val dict_quiz_data = mutableMapOf<String,Any?>()
                dict_quiz_data["letters"] = sliding_puzzle_letters

                dict["data"] = dict_quiz_data
                dict["gameUrl"] = url
            }
            else if(content_data_type=="matchPairs")
            {
                val typeData = content.getJSONObject("data")


                val url = "file:///android_asset/Content/MatchPairs/matching_pairs.html"

                val sliding_puzzle_letters = typeData.getString("letters")
                val dict_quiz_data = mutableMapOf<String,Any?>()
                dict_quiz_data["letters"] = sliding_puzzle_letters

                dict["data"] = dict_quiz_data
                dict["gameUrl"] = url
            }
            else if(content_data_type=="qrCode")
            {
                val typeData = content.getJSONObject("data")


                //val url = "file:///android_asset/Content/MatchPairs/matching_pairs.html"

                val sliding_puzzle_letters = typeData.getString("letters")
                val dict_quiz_data = mutableMapOf<String,Any?>()
                dict_quiz_data["letters"] = sliding_puzzle_letters

                dict["data"] = dict_quiz_data
                //dict["gameUrl"] = url
            }
            else if(content_data_type=="AR"){

                //I DO NOT PASS THIS INFO LATER, TODO
                val typeData = content.getJSONObject("data")

                val detection_image = typeData["img_src"]
                val detection_obj = typeData["obj_src"]


                //Quiz stuff just in case

                val quiz_question = typeData.getString("question")
                val quiz_answers = arrayOf<String>()
                val quiz_correct_answers = typeData.getInt("correct_answer")
                val quiz_points = typeData.getInt("points")
                val quiz_answers_temp = typeData.getJSONArray("answers")
                val quiz_letters = typeData.getString("letters")

                //Load all of them into the dict data for cleaner look
                val dict_quiz_data = mutableMapOf<String,Any?>()

                dict_quiz_data["question"] = quiz_question
                dict_quiz_data["answers"] = quiz_answers_temp
                dict_quiz_data["correct_answer"] = quiz_correct_answers
                dict_quiz_data["points"] = quiz_points
                dict_quiz_data["letters"] = quiz_letters

                val sliding_puzzle_letters = typeData.getString("letters")



                dict["img_src"] = detection_image
                dict["obj_src"] = detection_obj

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
            dict["title"] = title_of

            dict["running"] = false
            dict["finished"] = false
            dict["started"] = false


            dictList.add(dict)//Adds to dictionary
        }

        //rMapList

        //HERE!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        //Log.d("Is this true", rMapList!!::class.simpleName.toString())


        return dictList
    }

    //Convert json to usable functions and stuff
}