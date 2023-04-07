package gr.aketh.echoes

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.Recorder
import androidx.camera.video.Recording
import androidx.camera.video.VideoCapture
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.viewbinding.ViewBinding
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
import gr.aketh.echoes.databinding.ActivityGameTemplateBinding
import gr.aketh.echoes.databinding.ActivityMainBinding
import org.json.JSONArray
import org.json.JSONObject
import org.json.JSONTokener
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


typealias LumaListener = (luma: Double) -> Unit


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

    private lateinit var viewBinding: ActivityGameTemplateBinding

    private var imageCapture: ImageCapture? = null

    private var videoCapture: VideoCapture<Recorder>? = null
    private var recording: Recording? = null

    private lateinit var cameraExecutor: ExecutorService


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityGameTemplateBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)




        //Map fragment, basically find and load it
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        //Set the theme for no action bar
        setTheme(R.style.Theme_Echoes_Aketh_NoActionBar2);
        supportActionBar?.hide();




        //Loads all the stuff
        this.gameSceneObject = GameSceneInitializer(jsonParser(loadJSONFromAsset(applicationContext)!!),applicationContext,
            findViewById<RelativeLayout>(R.id.activity_game_layout), this.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater,this,this

        )



        //Camera
        this.requestCameraXPermissions()


        // Set up the listeners for take photo and video capture buttons
        viewBinding.includeCameraLayout.imageCaptureButton.setOnClickListener { takePhoto() }

        cameraExecutor = Executors.newSingleThreadExecutor()

        loadJSONFromAsset(applicationContext)?.let { jsonParser(it) }

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        //Location Update Stuff
        mLocationRequest = LocationRequest.create().setInterval(5000)
            .setFastestInterval(5000)
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
                Log.d( "woccc","ussss")
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


        startLocationUpdates()
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

        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                Toast.makeText(this,
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT).show()
                finish()
            }
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
    //CameraX
    private fun requestCameraXPermissions()
    {
        // Request camera permissions
        if (allPermissionsGranted()) {
            this.gameSceneObject.cameraPermissionsDone()
        } else {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }
    }

    private fun takePhoto() {
        // Get a stable reference of the modifiable image capture use case
        val imageCapture = imageCapture ?: return

        // Create time stamped name and MediaStore entry.
        val name = SimpleDateFormat(FILENAME_FORMAT, Locale.GERMAN)
            .format(System.currentTimeMillis())
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            if(Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/CameraX-Image")
            }
        }

        // Create output options object which contains file + metadata
        val outputOptions = ImageCapture.OutputFileOptions
            .Builder(contentResolver,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues)
            .build()

        // Set up image capture listener, which is triggered after photo has
        // been taken
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
                }

                override fun
                        onImageSaved(output: ImageCapture.OutputFileResults){
                    val msg = "Photo capture succeeded: ${output.savedUri}"
                    Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, msg)
                }
            }
        )
    }

    private fun captureVideo() {}

    public fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(viewBinding.includeCameraLayout.viewFinder.surfaceProvider)
                }


            //Image Capture
            imageCapture = ImageCapture.Builder().build()



            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture)

            } catch(exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(this))
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    override fun onPause() {
        super.onPause()
        //OnPause do things
        gameSceneObject.onPause()
    }






    companion object {
        /**
         * Request code for location permission request.
         *
         * @see .onRequestPermissionsResult
         */
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1

        private const val TAG = "CameraXApp"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS =
            mutableListOf (
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO
            ).apply {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                    add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }.toTypedArray()
    }


}