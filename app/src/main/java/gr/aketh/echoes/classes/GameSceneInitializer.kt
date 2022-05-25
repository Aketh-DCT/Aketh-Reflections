package gr.aketh.echoes.classes

import android.graphics.Color
import android.location.Location
import android.util.Log
import android.widget.Toast
import androidx.constraintlayout.motion.widget.Debug
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Circle
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import gr.aketh.echoes.classes.JsonUtilities.jsonArrayToMutableMap
import org.json.JSONArray
import org.json.JSONObject

class GameSceneInitializer(json: JSONObject) {
    lateinit var json: JSONObject
    var jsonMapped = mutableMapOf<String, Any?>()
    lateinit var jsonList: MutableList<MutableMap<String, Any?>>;

    init {
        Log.d("Inita", "Initizalized correctly")

        this.json = json
        jsonList = jsonArrayToMutableMap(json)//Gets list of stuff

        Log.d("jsonList", jsonList.toString())


    }


    fun addCirclesToMap(googleMap: GoogleMap) {
        //Iterate through the list of circles
        for (circle in jsonList) {
            try {
                Log.d("helloWorld", "Workes")
                val currentCircle = googleMap.addCircle(
                    CircleOptions().clickable(true).center(
                        LatLng(
                            circle["circle_center_lat"] as Double,
                            circle["circle_center_lon"] as Double
                        )
                    ).radius(circle["circle_radius"] as Double)
                        .strokeColor(Color.parseColor(circle["circle_color"] as String))
                        .fillColor(Color.parseColor(circle["circle_color"] as String))
                );

                //currentCircle.isClickable = true

                googleMap.setOnCircleClickListener {
                    it.fillColor = Color.BLUE

                }



                Log.d("helloWorld", "Workes 2")
            } catch (e: ClassCastException) {
                Log.e("error", e.printStackTrace().toString())
            }

        }
    }

    fun locationCallbackFunc(locationResult: LocationResult) {
        //All the stuff that need to be done

        for (circle in jsonList)
        {
            /*

            val distance = FloatArray(2)
            val distance2 = FloatArray(2)
            Location.distanceBetween(
                locationResult.lastLocation.latitude,
                locationResult.lastLocation.longitude,
                circleOptions.getCenter().latitude,
                circleOptions.getCenter().longitude,
                distance
            )
            Location.distanceBetween(
                locationResult.lastLocation.latitude,
                locationResult.lastLocation.longitude,
                circleOptions2.getCenter().latitude,
                circleOptions2.getCenter().longitude,
                distance2
            )
            if (distance[0] > circleOptions.getRadius()) {
                if (hasStarted) {
                    mediaPlayer.seekTo(0)
                    mediaPlayer.pause()
                }
                Toast.makeText(
                    baseContext,
                    "Outside, distance from center: " + distance[0] + " radius: " + circleOptions.getRadius(),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                //mMap.setMyLocationEnabled(true);
                mMap.uiSettings.isMyLocationButtonEnabled = true
                if (!mediaPlayer.isPlaying()) {
                    hasStarted = true
                    Log.e("marios", "beginning")
                    mediaPlayer.start()
                }

                //Toast.makeText(getBaseContext(), "Inside, distance from center: " + distance[0] + " radius: " + circleOptions.getRadius() , Toast.LENGTH_SHORT).show();
            }
            if (distance2[0] > circleOptions2.getRadius()) {
                if (hasStarted2) {
                    mediaPlayer2.seekTo(0)
                    mediaPlayer2.pause()
                    mVideoView.pause()
                    mVideoView.seekTo(0)
                    mVideoView.setVisibility(View.INVISIBLE)
                }
                Toast.makeText(
                    baseContext,
                    "Outside, distance from center: " + distance[0] + " radius: " + circleOptions.getRadius(),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                //mMap.setMyLocationEnabled(true);
                mMap.uiSettings.isMyLocationButtonEnabled = true
                if (!mediaPlayer2.isPlaying()) {
                    hasStarted2 = true
                    Log.e("marios2", "beginning")
                    mediaPlayer2.start()
                    mVideoView.start()
                    mVideoView.setVisibility(View.VISIBLE)
                }

                //Toast.makeText(getBaseContext(), "Inside, distance from center: " + distance[0] + " radius: " + circleOptions.getRadius() , Toast.LENGTH_SHORT).show();
            }
            */
        }


    }
}
