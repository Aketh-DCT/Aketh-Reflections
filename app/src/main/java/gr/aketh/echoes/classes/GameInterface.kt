import android.util.Log
import android.webkit.JavascriptInterface
import gr.aketh.echoes.GameTemplate

class GameInterface(private val gameActivity: GameTemplate) {

    @JavascriptInterface
    fun onGameCompleted(points: Int) {
        gameActivity.runOnUiThread {
            try {
                Log.d("GameInterface", "Received points: $points")
                // Handle the game completion and points here
                gameActivity.onGameCompleted(points)
            } catch (e: Exception) {
                Log.e("GameInterface", "Error invoking onGameCompleted", e)
            }
        }
    }
}
