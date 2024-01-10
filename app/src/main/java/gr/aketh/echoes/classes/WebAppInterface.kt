package gr.aketh.echoes.classes

import android.content.Context
import android.webkit.JavascriptInterface
import com.google.gson.Gson

class WebAppInterface(private val mContext: Context, private val bitmapStrings: MutableMap<String, List<String>>) {
    @JavascriptInterface
    fun getImages(): String {
        if(bitmapStrings.contains("imgs")){
            return Gson().toJson(bitmapStrings["imgs"])
        }
        return Gson().toJson(listOf("test"))

    }

    @JavascriptInterface
    fun getLanguage(): String{
        if(bitmapStrings.contains("language")){
            return Gson().toJson(bitmapStrings["language"])
        }
        return Gson().toJson(listOf("en"))

    }

    @JavascriptInterface
    fun getWord(): String{
        if(bitmapStrings.contains("word")){
            return Gson().toJson(bitmapStrings["word"])
        }
        return Gson().toJson(listOf("ΥΓΕΙΑ"))

    }


}