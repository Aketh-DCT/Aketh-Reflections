package gr.aketh.echoes.classes

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION_CODES
import android.util.Log
import android.widget.Toast
import com.google.ar.core.Anchor
import com.google.ar.core.ArCoreApk
import com.google.ar.core.ArCoreApk.Availability
import com.google.ar.core.ArCoreApk.InstallStatus
import com.google.ar.core.AugmentedImageDatabase
import com.google.ar.core.Config
import com.google.ar.core.Session
import com.google.ar.core.exceptions.UnavailableException
import com.google.ar.core.exceptions.UnavailableUserDeclinedInstallationException
//import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.rendering.ModelRenderable
//import com.google.ar.sceneform.rendering.Renderable
//import com.google.ar.sceneform.ux.ArFragment
//import com.google.ar.sceneform.ux.TransformableNode
import gr.aketh.echoes.R
import java.lang.Error
import java.util.concurrent.CompletableFuture


//Helper class to initialise all the arcore features
class ArCoreClass(applicationContext: Context, applicationActivity: Activity) {

    //Basic attributes for class
    private lateinit var applicationContext: Context
    private lateinit var applicationActivity: Activity
    private lateinit var mSession: Session
    private lateinit var imageDatabase: AugmentedImageDatabase
    private lateinit var config: Config

    private var lampPostRenderable: ModelRenderable? = null
    private val selectedObject: Uri? = null

    private val MIN_OPENGL_VERSION = 3.0
    init {

        //intialise all the basic attributes to variables
        this.applicationContext = applicationContext
        this.applicationActivity = applicationActivity

    }


    fun maybeEnableArButton()
    {
        //Check the availability and enables AR if needed

        ArCoreApk.getInstance().checkAvailabilityAsync(this.applicationContext){
            availability ->
            if(availability.isSupported) {
                //enable ar
            }
            else {
                //distable ar
            }
        }
    }

    //Check if google services ar is installed
    fun checkGoogleServicesArInstalled()
    {

        var mUserRequestedInstall = true
        try {
            if(!this::mSession.isInitialized)
            {
                when(ArCoreApk.getInstance().requestInstall(this.applicationActivity, mUserRequestedInstall)){
                    ArCoreApk.InstallStatus.INSTALLED ->{
                        //Success can create session
                    }
                    ArCoreApk.InstallStatus.INSTALL_REQUESTED ->{
                        // When this method returns `INSTALL_REQUESTED`:
                        // 1. ARCore pauses this activity.
                        // 2. ARCore prompts the user to install or update Google Play
                        //    Services for AR (market://details?id=com.google.ar.core).
                        // 3. ARCore downloads the latest device profile data.
                        // 4. ARCore resumes this activity. The next invocation of
                        //    requestInstall() will either return `INSTALLED` or throw an
                        //    exception if the installation or update did not succeed.
                        mUserRequestedInstall = false
                        return
                    }
                }
            }
        }
        catch (e: UnavailableUserDeclinedInstallationException)
        {
            // Display an appropriate message to the user and return gracefully.
            //Toast.makeText(this.applicationContext, "TODO: handle exception"+e, Toast.LENGTH_LONG).show()
            return
        }

    }

    // Verify that ARCore is installed and using the current version.
    fun isARCoreSupportedAndUpToDate(): Boolean {
        return when (ArCoreApk.getInstance().checkAvailability(this.applicationContext)) {
            Availability.SUPPORTED_INSTALLED -> true
            Availability.SUPPORTED_APK_TOO_OLD, Availability.SUPPORTED_NOT_INSTALLED -> {
                try {
                    // Request ARCore installation or update if needed.
                    when (ArCoreApk.getInstance().requestInstall(this.applicationActivity, true)) {
                        InstallStatus.INSTALL_REQUESTED -> {
                            Log.i("tag", "ARCore installation requested.")
                            false
                        }
                        InstallStatus.INSTALLED -> true
                    }
                } catch (e: UnavailableException) {
                    Log.e("TAG", "ARCore not installed", e)
                    false
                }
            }

            Availability.UNSUPPORTED_DEVICE_NOT_CAPABLE ->{
                // This device is not supported for AR.
                false
            }

            Availability.UNKNOWN_CHECKING -> {
                // ARCore is checking the availability with a remote query.
                // This function should be called again after waiting 200 ms to determine the query result.
                false
            }
            Availability.UNKNOWN_ERROR, Availability.UNKNOWN_TIMED_OUT -> {
                // There was an error checking for AR availability. This may be due to the device being offline.
                // Handle the error appropriately.
                false
            }
        }
    }

    fun createSession()
    {
        // Create a new ARCore session.
        this.mSession = Session(this.applicationContext)

        //Create a config session
        //val config = Config(this.mSession)
        // Do feature-specific operations here, such as enabling depth or turning on
        // support for Augmented Faces.
        // Configure the session.
        //this.mSession.configure(config)

    }

    fun closeSession()
    {
        this.mSession.close()
    }

    //Loads the image database
    fun loadImageDatabase()
    {
        imageDatabase = this.applicationContext.assets.open("ImageDatabase/myimages.imgdb").use {
            AugmentedImageDatabase.deserialize(this.mSession, it)
        }
        config = Config(mSession)
        config.augmentedImageDatabase = imageDatabase

        mSession.configure(config)
        mSession.resume()
        mSession.update()

        Log.d("Session", mSession.toString());




    }

    fun getmSession(): Session{
        return mSession
    }

    fun requestCameraPermissions(){
        //CameraPermissionHelper
        //  if(!this.applicationContext.)
    }

    /*
     fun placeObject(arFragment: ArFragment, anchor: Anchor, uri: Uri) {
        Log.d("PLACEOBJECT","yes1")

         var modelRenderable: CompletableFuture<ModelRenderable>
         try{
             modelRenderable = ModelRenderable.builder()
                 .setSource(this.applicationContext, uri)
                 .build()
         }catch (e: Exception)
         {
            Log.d("MODELERROR",e.printStackTrace().toString())
         }

         try {
             modelRenderable = ModelRenderable.builder()
                 .setSource(arFragment.requireContext(), uri)
                 .build()
             modelRenderable.thenAccept { modelRenderable: ModelRenderable ->
                 Log.d("PLACEOBJECT","yes2")
                 addNodeToScene(
                     arFragment,
                     anchor,
                     modelRenderable
                 )
                 /*

                 }*/
                 Log.d("PLACEOBJECT","yes3")
             }
             modelRenderable.exceptionally { throwable: Throwable ->
                 Toast.makeText(arFragment.context, "Error:" + throwable.message, Toast.LENGTH_LONG)
                     .show()
                 null}
         }catch (e: Exception)
         {
             Log.d("MODELERROR",e.printStackTrace().toString())
         }




        Log.d("PLACEOBJECT","yes4")
    }

     fun addNodeToScene(arFragment: ArFragment, anchor: Anchor, renderable: Renderable) {
        Log.d("PLACEOBJECT","yes5")
        val anchorNode = AnchorNode(anchor)
        val node = TransformableNode(arFragment.transformationSystem)
        node.renderable = renderable
        node.setParent(anchorNode)
        arFragment.arSceneView.scene.addChild(anchorNode)
        node.select()
    }
*/

    fun checkIsSupportedDeviceOrFinish(activity: Activity): Boolean {
        if (Build.VERSION.SDK_INT < VERSION_CODES.N) {
            Log.e("TEST", "Sceneform requires Android N or later")
            //Toast.makeText(activity, "Sceneform requires Android N or later", Toast.LENGTH_LONG)
              //  .show()
            activity.finish()
            return false
        }
        val openGlVersionString =
            (activity.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager)
                .deviceConfigurationInfo
                .glEsVersion
        if (openGlVersionString.toDouble() < MIN_OPENGL_VERSION) {
            Log.e("TEST", "Sceneform requires OpenGL ES 3.0 later")
            //Toast.makeText(activity, "Sceneform requires OpenGL ES 3.0 or later", Toast.LENGTH_LONG)
              //  .show()
            activity.finish()
            return false
        }
        return true
    }

}