package gr.aketh.echoes.classes

import android.content.Context
import java.io.IOException
import java.io.InputStream
import java.nio.charset.Charset

object JsonUtilities
{
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

    //Convert json to usable functions and stuff
}