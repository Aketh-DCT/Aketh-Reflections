package gr.aketh.echoes

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import gr.aketh.echoes.classes.GameSceneInitializer
import gr.aketh.echoes.classes.JsonUtilities.loadJSONFromAsset
import gr.aketh.echoes.classes.PermissionUtils
import gr.aketh.echoes.classes.PermissionUtils.PermissionDeniedDialog.Companion.newInstance
import org.json.JSONArray
import org.json.JSONObject
import org.json.JSONTokener


class GameTemplate : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMyLocationClickListener,
    GoogleMap.OnMyLocationButtonClickListener {

    private val PERMISSION_ID = 42
    lateinit var mFusedLocationClient: FusedLocationProviderClient
    lateinit var mMap: GoogleMap
    private var permissionDenied = false

    private lateinit var mLocationRequest: LocationRequest

    private lateinit var locationCallback: LocationCallback

    //Game object
    lateinit var gameSceneObject: GameSceneInitializer

    //Current location (set to aketh)
    var currentLocation: LatLng = LatLng(39.542678, 21.775136)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_template)


        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)


        //Loads all the stuff
        this.gameSceneObject = GameSceneInitializer(jsonParser(loadJSONFromAsset(applicationContext)!!))
        loadJSONFromAsset(applicationContext)?.let { jsonParser(it) }

        //Location Update Stuff
        mLocationRequest = LocationRequest.create().setInterval(1000)
            .setFastestInterval(1000)
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            .setMaxWaitTime(100).setSmallestDisplacement(3F);

        //Location callback stuff
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                //Log.e("working", "yes")
                if (locationResult == null) {
                    return
                }
                //passes arguments so code is clearer
                gameSceneObject.locationCallbackFunc(locationResult)
            }
        }

    }


    override fun onMapReady(googleMap: GoogleMap) {
        //map stuff to show basic button
        mMap = googleMap
        googleMap.setOnMyLocationClickListener(this)
        googleMap.setOnMyLocationButtonClickListener(this)
        enableMyLocation()

        //Marker stuff test
        val tsitsanisMuseum = LatLng(39.550598, 21.769916)


        this.gameSceneObject.addCirclesToMap(googleMap)
        //Circle stuff
        //googleMap.addCircle(CircleOptions().center(
          //  LatLng(39.550598, 21.769916)).
        //    radius(1000.0).
        //    strokeColor(Color.RED).
        //    fillColor(Color.BLUE));

        googleMap.addMarker(
            MarkerOptions()
                .position(tsitsanisMuseum)
                .title("Tsitsanis Museum")
        )
    }

    //enable location layer if fine location permissions been granted
    //why this took so long i honestly don't know, lets assume that life is such
    //that i spend my entire morning just assuming my normal routine
    //and had to code this stuff like 3 times
    //did i learn anything? maybe, did i waste my time?
    //you know i keep writing but i need to continue so....
    //kambatene
    @SuppressLint("MissingPermission")
    private fun enableMyLocation() {
        //1. Check if permission granted
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
            return
        }

        //2. If a permission rationale dialog should be shown(what is rationale dialog?)
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) || ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        ) {
            PermissionUtils.RationaleDialog.newInstance(
                LOCATION_PERMISSION_REQUEST_CODE, true
            ).show(supportFragmentManager, "dialog")
            return
        }

        //3 Request permission
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            LOCATION_PERMISSION_REQUEST_CODE
        )

    }

    override fun onMyLocationButtonClick(): Boolean {
        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT)
            .show()

        //Return false so we dont consume event
        return false
    }

    override fun onMyLocationClick(p0: Location) {
        Toast.makeText(this, "Current location:\n$p0", Toast.LENGTH_LONG)
            .show()
    }

    //Permission result
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {


        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            return
        }

        if (PermissionUtils.isPermissionGranted(
                permissions,
                grantResults,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) || PermissionUtils.isPermissionGranted(
                permissions,
                grantResults,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        ) {
            enableMyLocation()
        } else{
            //Permission was denied
            permissionDenied=true
        }

    }

    override fun onResumeFragments() {
        super.onResumeFragments()

        if(permissionDenied)
        {
            //Permission was not granted, display error dialog.
            showMissingPermissionError()
            permissionDenied = false
        }
    }

    //Error dialog
    private fun showMissingPermissionError()
    {
        newInstance(true).show(supportFragmentManager, "dialog")
    }

    //Convert json file to usable stuff
    private fun jsonParser(json: String): JSONObject {
        var jsonObject = JSONTokener(json).nextValue() as JSONObject

        var jsonArray = jsonObject.getJSONArray("data") as JSONArray

        return jsonObject;


    }


    @SuppressLint("MissingPermission")
    private fun startLocationUpdates(){
        //For map updates
        if(ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            return
        }

        mFusedLocationClient.requestLocationUpdates(mLocationRequest,locationCallback, Looper.getMainLooper())
    }




    companion object {
        /**
         * Request code for location permission request.
         *
         * @see .onRequestPermissionsResult
         */
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }


}