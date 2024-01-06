package gr.aketh.echoes.classes

import android.content.Context
import android.webkit.JavascriptInterface
import com.google.gson.Gson

class WebAppInterface(private val mContext: Context, private val bitmapStrings: List<String>) {
    @JavascriptInterface
    fun getImages(): String {
        return Gson().toJson(bitmapStrings)
    }
}