package gr.aketh.echoes

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
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
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import gr.aketh.echoes.classes.CameraPermissionHelper
import gr.aketh.echoes.classes.GameSceneInitializer
import gr.aketh.echoes.classes.JsonUtilities.loadJSONFromAsset
import gr.aketh.echoes.classes.PermissionUtils
import gr.aketh.echoes.classes.PermissionUtils.PermissionDeniedDialog.Companion.newInstance
import gr.aketh.echoes.classes.PermissionUtils.isPermissionGranted
import gr.aketh.echoes.databinding.ActivityGameTemplateBinding
import gr.aketh.echoes.databinding.ActivityMainBinding
import org.json.JSONArray
import org.json.JSONObject
import org.json.JSONTokener
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.collections.ArrayList


typealias LumaListener = (luma: Double) -> Unit


class GameTemplate : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMyLocationClickListener,
    GoogleMap.OnMyLocationButtonClickListener {

    private val PERMISSION_ID = 42
    private var mFusedLocationClient: FusedLocationProviderClient? = null
    private lateinit var mMap: GoogleMap
    private var permissionDenied = false
    private var jsonFileName: String = ""

    private lateinit var mLocationRequest: LocationRequest

    private lateinit var locationCallback: LocationCallback

    //Game object
    var gameSceneObject: GameSceneInitializer? = null

    //Current location (set to aketh)
    var currentLocation: LatLng = LatLng(39.542678, 21.775136)

    private lateinit var viewBinding: ActivityGameTemplateBinding

    private var imageCapture: ImageCapture? = null

    private var videoCapture: VideoCapture<Recorder>? = null
    private var recording: Recording? = null

    private lateinit var cameraExecutor: ExecutorService

    private var doubleBackToExitPressedOnce = false

    private lateinit var permissionList: ArrayList<String>

    private var gameObjectInitialised = false


    //Permission stuff
    val permissionsStr = arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.CAMERA,
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.INTERNET
    )

    private val permissionStrSub = listOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.CAMERA,
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.INTERNET
    )

    var permissionsCount = 0
    var permissionsList: ArrayList<String> = ArrayList()

    var dialog : AlertDialog? = null



    //This was a tough one. To ask multiple permissions
    //You need to first request them, and handle each case
    //When you don't get it etc. This one handles it
    //Do Not touch. Literally, if you touch it dies
    //It is quite complex, i understand 38% of it
    //So don't try please!
    val permissionsLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions(),
        object : ActivityResultCallback<Map<String, Boolean>> {
            override fun onActivityResult(result: Map<String, Boolean>) {
                val list: ArrayList<Any?> = ArrayList(result.values)
                var rejList = arrayListOf<String>()
                permissionsList.clear()
                permissionsCount = 0
                for (i in list.indices) {
                    if (shouldShowRequestPermissionRationale(permissionsStr[i])) {
                        permissionsList.add(permissionsStr[i])
                    } else if (!hasPermission(applicationContext, permissionsStr[i])) {
                        //Toast.makeText(applicationContext, permissionsStr[i], Toast.LENGTH_SHORT)
                           // .show()
                        permissionsCount++
                        Log.d("PERMISSION", permissionsStr[i])
                        rejList.add(permissionsStr[i])
                    }
                }
                if (permissionsList.size > 0) {
                    //Some permissions are denied and can be asked again.
                    askForPermissions(permissionsList)
                } else if (permissionsCount > 0) {
                    //Show alert dialog
                    if(rejList.contains("android.permission.WRITE_EXTERNAL_STORAGE")){
                        //Start Game update
                        initGameCodeAfterPermissions()
                    }
                    else{
                        showPermissionDialog()
                    }


                } else {
                    //All permissions granted. Do your stuff 🤞
                    //Toast.makeText(applicationContext, "All Permissions granted", Toast.LENGTH_SHORT)
                    //    .show()
                    initGameCodeAfterPermissions()
                }
            }


        })

    //Create the array to ask for the above function
    private fun askForPermissions(permissionsList: ArrayList<String>) {
        //val newPermissionStr = arrayOfNulls<String>(permissionsList.size)

        val newPermissionStr = permissionsList.toTypedArray()

        if (newPermissionStr.isNotEmpty()) {
            permissionsLauncher.launch(newPermissionStr)
        } else {
            /* User has pressed 'Deny & Don't ask again' so we have to show the enable permissions dialog
        which will lead them to app details page to enable permissions from there. */
            showPermissionDialog()
        }
    }

    private fun hasPermission(context: Context, permissionStr: String): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            permissionStr
        ) == PackageManager.PERMISSION_GRANTED
    }

    private var alertDialog: AlertDialog? = null
    private fun showPermissionDialog() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle("Permission required")
            .setMessage("Some permissions are need to be allowed to use this app without any problems.")
            .setPositiveButton("Settings") { dialog, which -> dialog.dismiss() }
        if (alertDialog == null) {
            alertDialog = builder.create()
            if (!alertDialog!!.isShowing) {
                alertDialog!!.show()
            }
        }
    }
    //End of permission stuff




    override fun onCreate(savedInstanceState: Bundle?) {
        dialog = setProgressDialog(this, "Loading...")
        dialog?.show()
        // Needed stuff
        super.onCreate(savedInstanceState)
        viewBinding = ActivityGameTemplateBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        jsonFileName = intent.getStringExtra("jsonFile").toString()
        //This does not let the screen sleep
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        //Map fragment, basically find and load it
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        //Set the theme for no action bar
        setTheme(R.style.Theme_Echoes_Aketh_NoActionBar2);
        supportActionBar?.hide();

        //End of needed stuff for basic functionality

    }





    private fun askForCameraPermission() {
        ActivityCompat.requestPermissions(
            this@GameTemplate,
            arrayOf(android.Manifest.permission.CAMERA),
            requestCodeCameraPermission
        )
    }

    private fun hasPermissions(vararg permissions: String): Boolean {
        for (permission in permissions) {
            if (ActivityCompat.checkSelfPermission(applicationContext, permission) != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }

    private fun initGameCodeAfterPermissions(){

        //Loads all the stuff
        this.gameSceneObject = GameSceneInitializer(jsonParser(loadJSONFromAsset(applicationContext, jsonFileName)!!),applicationContext,
            findViewById<RelativeLayout>(R.id.activity_game_layout), this.getSystemService(
                LAYOUT_INFLATER_SERVICE
            ) as LayoutInflater,this,this, supportFragmentManager

        )

        this.gameSceneObject!!.cameraPermissionsDone()



        // Set up the listeners for take photo and video capture buttons
        //viewBinding.includeCameraLayout.imageCaptureButton.setOnClickListener { takePhoto() }

        cameraExecutor = Executors.newSingleThreadExecutor()

        //loadJSONFromAsset(applicationContext)?.let { jsonParser(it) }

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        //Location Update Stuff
        mLocationRequest = LocationRequest.create().setInterval(1000)
            .setFastestInterval(1000)
            .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
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
                gameSceneObject!!.locationCallbackFunc(locationResult)
            }
        }

        val aniSlide: Animation =
            AnimationUtils.loadAnimation(this@GameTemplate, R.anim.scanner_animation)
        //viewBinding.barcodeLine.startAnimation(aniSlide)

        if(::mMap.isInitialized and !gameObjectInitialised)
        {
            this.gameSceneObject!!.addCirclesToMap(mMap)


            startLocationUpdates()

            gameObjectInitialised = true
        }

        dialog?.dismiss()
    }







    override fun onMapReady(googleMap: GoogleMap) {
        //map stuff to show basic button
        mMap = googleMap
        googleMap.setOnMyLocationClickListener(this)
        googleMap.setOnMyLocationButtonClickListener(this)
        //enableMyLocation()

        //Marker stuff test
        val tsitsanisMuseum = LatLng(39.550598, 21.769916)


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







        if(this.gameSceneObject!=null && !gameObjectInitialised)
        {
            this.gameSceneObject!!.addCirclesToMap(googleMap)
            startLocationUpdates()

            gameObjectInitialised = true
        }
        




        //Permission Array

        //permissionsList.addAll(permissionsStr.toList())
        //askForPermissions(permissionsList)
        if (!hasPermissions(*permissionsStr)) {
            ActivityCompat.requestPermissions(this, permissionsStr, PERMISSION_ID)
        }

        Log.d("TEST","GIA")

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
        //Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT)
        //    .show()

        //Return false so we dont consume event
        return false
    }

    override fun onMyLocationClick(p0: Location) {
        //Toast.makeText(this, "Current location:\n$p0", Toast.LENGTH_LONG)
         //   .show()
    }

    //Permission result

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    )
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

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

            var fusedLocationProviderClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
            fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
                // Move the camera to the location of the device
                mMap.moveCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        LatLng(location.latitude, location.longitude), 20f))
            }
        }
        else {
            // Permission was denied. Display an error message
            // Display the missing permission error dialog when the fragments resume.
            permissionDenied = true
        }


        when (requestCode) {
            PERMISSION_ID -> {
                val grantedPermissions = mutableListOf<String>()
                for (i in permissions.indices) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        grantedPermissions.add(permissions[i])
                    }
                }

                if(grantedPermissions.containsAll(permissionStrSub))
                {
                    initGameCodeAfterPermissions()
                }

                // All permissions have been granted, do your stuff here



               //
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

        mFusedLocationClient?.requestLocationUpdates(mLocationRequest,locationCallback, Looper.getMainLooper())
    }
    //CameraX


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
                    //Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, msg)
                }
            }
        )
    }

    private fun captureVideo() {}

    public fun startCamera(){
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
        gameSceneObject?.onDestroy()
    }

    override fun onPause() {
        super.onPause()
        //OnPause do things
        gameSceneObject?.onPause()
    }


    override fun onResume() {
        super.onResume()
        //this.gameSceneObject.onResume()
        //Onresume first check if still inside circle
        // ARCore requires camera permission to operate.
       // if (!CameraPermissionHelper.hasCameraPermission(this)) {
        //    CameraPermissionHelper.requestCameraPermission(this)
       //     return
       // }


    }

    fun onGameCompleted(points: Int) {
        this.gameSceneObject?.onGameCompleted(points)
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
        private const val REQUEST_CAMERA_PERMISSION = 200
        private const val CAMERA_REQUEST = 101
        private const val TAG2 = "API123"
        private const val SAVED_INSTANCE_URI = "uri"
        private const val SAVED_INSTANCE_RESULT = "result"

        private const val requestCodeCameraPermission = 1001
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



    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            return
        }

        this.doubleBackToExitPressedOnce = true
        //Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show()

        Handler(Looper.getMainLooper()).postDelayed(Runnable {
            doubleBackToExitPressedOnce = false
        }, 2000)
    }

    fun setProgressDialog(context: Context, message: String): AlertDialog {
        val llPadding = 30
        val ll = LinearLayout(context)
        ll.orientation = LinearLayout.HORIZONTAL
        ll.setPadding(llPadding, llPadding, llPadding, llPadding)
        ll.gravity = Gravity.CENTER
        var llParam = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        llParam.gravity = Gravity.CENTER
        ll.layoutParams = llParam

        val progressBar = ProgressBar(context)
        progressBar.isIndeterminate = true
        progressBar.setPadding(0, 0, llPadding, 0)
        progressBar.layoutParams = llParam

        llParam = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        llParam.gravity = Gravity.CENTER
        val tvText = TextView(context)
        tvText.text = message
        tvText.setTextColor(Color.parseColor("#000000"))
        tvText.textSize = 20.toFloat()
        tvText.layoutParams = llParam

        ll.addView(progressBar)
        ll.addView(tvText)

        val builder = AlertDialog.Builder(context)
        builder.setCancelable(true)
        builder.setView(ll)

        val dialog = builder.create()
        val window = dialog.window
        if (window != null) {
            val layoutParams = WindowManager.LayoutParams()
            layoutParams.copyFrom(dialog.window?.attributes)
            layoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT
            layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT
            dialog.window?.attributes = layoutParams
        }
        return dialog
    }



}
