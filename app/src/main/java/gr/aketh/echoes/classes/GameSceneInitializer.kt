package gr.aketh.echoes.classes

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.location.Location
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import gr.aketh.echoes.GameTemplate
import gr.aketh.echoes.R
import gr.aketh.echoes.classes.JsonUtilities.jsonArrayToMutableMap
import gr.aketh.echoes.databinding.ActivityGameTemplateBinding
import org.json.JSONArray
import org.json.JSONObject
import kotlin.reflect.typeOf


class GameSceneInitializer(
    json: JSONObject,
    applicationContext: Context,
    linearLayout: RelativeLayout,
    layoutInflaterSum: LayoutInflater,
    activity: Activity,
    parentClass: GameTemplate

) {
    lateinit var json: JSONObject
    var jsonMapped = mutableMapOf<String, Any?>()
    lateinit var jsonList: MutableList<MutableMap<String, Any?>>;
    private lateinit var applicationContext: Context

    //----------------------------------
    private lateinit var activity: Activity
    private lateinit var quizLayout: LinearLayout
    private lateinit var wrongLayout: RelativeLayout
    private lateinit var cameraLayout: ConstraintLayout
    private lateinit var slidingPuzzleLayout: LinearLayout
    private lateinit var pointsTextView: TextView

    private var points = 0

    //Sound Stuff
    private var mediaPlayer: MediaPlayer
    private lateinit var mediaPlayerList: MutableMap<String, MediaPlayer>
    private lateinit var soundeffectsPlayerList: MutableList<MediaPlayer>
    private var hasStarted = false


    private val VIDEO_SAMPLE = "v1"
    private lateinit var mVideoView: VideoView
    private lateinit var mMediaController: MediaController

    var popupWindow: PopupWindow? = null
    var linearLayout1: RelativeLayout? = null

    private lateinit var parentClass: GameTemplate


    //Image stuff
    var cameraEnabled: Boolean = false


    init {
        Log.d("Inita", "Initizalized correctly")

        this.json = json
        jsonList = jsonArrayToMutableMap(json)//Gets list of stuff

        //Load the binding
        this.activity = activity
        this.parentClass = parentClass


        mediaPlayerList = mutableMapOf()
        soundeffectsPlayerList = mutableListOf()

        Log.d("jsonList", jsonList.toString())
        this.applicationContext = applicationContext


        //Add audio
        mediaPlayerList["ascliption"] =
            MediaPlayer.create(applicationContext, getMedia(R.raw.asclipion))
        mediaPlayerList["central_bridge_of_trikala"] =
            MediaPlayer.create(applicationContext, getMedia(R.raw.central_bridge_of_trikala))
        mediaPlayerList["islam_shah_mosque"] =
            MediaPlayer.create(applicationContext, getMedia(R.raw.islam_shah_mosque))
        mediaPlayerList["old_prison"] =
            MediaPlayer.create(applicationContext, getMedia(R.raw.old_prison))
        mediaPlayerList["statue_of_asclepius"] =
            MediaPlayer.create(applicationContext, getMedia(R.raw.statue_of_asclepius))
        mediaPlayerList["synagogue"] =
            MediaPlayer.create(applicationContext, getMedia(R.raw.synagogue))


        //Add sound effect
        soundeffectsPlayerList.add(MediaPlayer.create(applicationContext, getMedia(R.raw.points)))
        soundeffectsPlayerList.add(
            MediaPlayer.create(
                applicationContext,
                getMedia(R.raw.wrong_answer)
            )
        )


        linearLayout1 = linearLayout

        mediaPlayer = MediaPlayer.create(applicationContext, getMedia(R.raw.s1))

        //Points text
        pointsTextView = this.activity.findViewById<TextView>(R.id.points)

        //Init the different layouts and do something with the button (MAKE SURE TO PUT THEM AS INCLUDE INSIDE activity_game_template!!!
        quizLayout = this.activity.findViewById<LinearLayout>(R.id.include_quiz_layout)
        var buttonStart = quizLayout.findViewById<Button>(R.id.quiz_bt_answer)
        var quizRadioGroup = quizLayout.findViewById<RadioGroup>(R.id.quiz_rg)

        wrongLayout = this.activity.findViewById<RelativeLayout>(R.id.include_wrong_layout)
        var buttonWrong = wrongLayout.findViewById<Button>(R.id.wrong_bt_close)

        var correctLayout = this.activity.findViewById<RelativeLayout>(R.id.include_correct_layout)
        var buttonCorrect = correctLayout.findViewById<Button>(R.id.correct_bt_close)

        cameraLayout = this.activity.findViewById<ConstraintLayout>(R.id.include_camera_layout)
        var buttonDoneCamera = cameraLayout.findViewById<Button>(R.id.camera_bt_done)


        slidingPuzzleLayout = this.activity.findViewById<LinearLayout>(R.id.include_slidingPuzzle_layout)



        //When i click it dissapears

        buttonStart.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {

                // Get the checked radio button id from radio group
                var id: Int = quizRadioGroup.checkedRadioButtonId
                if (id != -1) { // If any radio button checked from radio group
                    // Get the instance of radio button using id
                    val radio: RadioButton = quizRadioGroup.findViewById(id)

                    // val tmpMap: MutableMap<String, Any?> = circle["data"] as MutableMap<String, Any?>
                    //val tmpArray: JSONArray = tmpMap["answers"] as JSONArray


                    quizLayout.visibility = View.INVISIBLE
                    //Compare hidden text to text
                    if (radio.text == quizLayout.findViewById<TextView>(R.id.quiz_hiddenVariable).text) {
                        //if its correct
                        soundeffectsPlayerList[0].start()
                        points += 100
                        pointsTextView.text = "Points: " + points

                        correctLayout.visibility = View.VISIBLE
                    } else {
                        //if its wrong
                        soundeffectsPlayerList[1].start()
                        wrongLayout.visibility = View.VISIBLE
                    }

                }


            }
        })



        buttonWrong.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                wrongLayout.visibility = View.INVISIBLE
            }
        })

        buttonCorrect.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                correctLayout.visibility = View.INVISIBLE
            }
        })

        buttonDoneCamera.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                cameraLayout.visibility = View.INVISIBLE
                soundeffectsPlayerList[0].start()
                points += 100
                pointsTextView.text = "Points: " + points
            }
        })


        /*
        var inflater: LayoutInflater = layoutInflaterSum
        var customView: View = inflater.inflate(R.layout.popup_main,null);

        //Instanciate popup window
        popupWindow = PopupWindow(customView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)

        //Finally display it
        Log.d("Popup", popupWindow.toString())
        Log.d("Popup", linearLayout1.toString())
        popupWindow!!.showAtLocation(linearLayout1, Gravity.CENTER, 0, 0)
        */


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


                //Changes color when you click
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
        //Activates any activities or sounds as per the parameters

        for (circle in jsonList) {
            try {
                val distance = FloatArray(2)
                val distance2 = FloatArray(2)
                Location.distanceBetween(
                    locationResult.lastLocation!!.latitude,
                    locationResult.lastLocation!!.longitude,
                    circle["circle_center_lat"] as Double,
                    circle["circle_center_lon"] as Double,
                    distance
                )


                //Here everything is executed
                if (distance[0] > circle["circle_radius"] as Double) {
                    if (circle["running"] as Boolean)
                    {

                        this.mediaPlayerList[circle["sound"]]?.stop()
                    }


                } else if (!(circle["running"] as Boolean)) {

                    this.mediaPlayerList[circle["sound"]]?.start()


                    //Read the quiz layout and display it
                    if (circle["type"] == "quiz" && !(circle["running"] as Boolean)) {
                        this.quizLayout.visibility = View.VISIBLE

                        //Show layout and do calculation
                        this.showCorrectLayoutWithContent(circle)
                    } else if (circle["type"] == "camera" && !(circle["running"] as Boolean)) {
                        //Show layout and do calculation
                        this.showCorrectLayoutWithContent(circle)
                    }
                    else if(circle["type"] == "slidingPuzzle")
                    {
                        //Show layout and do calculation
                        this.showCorrectLayoutWithContent(circle)
                    }

                    circle["running"] = true


                }

            } catch (e: Exception) {
                //Log.d("FPPP",e.printStackTrace().toString())
            }


        }


    }

    private fun showCorrectLayoutWithContent(circle: MutableMap<String, Any?>) {

        //Do depending on what the layout that we want is

        when (circle["type"] as String) {
            "quiz" -> {
                Log.d("Type", "questionTv.text.toString()")
                val questionTv = quizLayout.findViewById<TextView>(R.id.quiz_tv_title)
                var answersRb = arrayOf(
                    quizLayout.findViewById<RadioButton>(R.id.quiz_rb_1),
                    quizLayout.findViewById<RadioButton>(R.id.quiz_rb_2),
                    quizLayout.findViewById<RadioButton>(R.id.quiz_rb_3),
                    quizLayout.findViewById<RadioButton>(R.id.quiz_rb_4)
                )
                val hiddenAnswer = quizLayout.findViewById<TextView>(R.id.quiz_hiddenVariable)


                val tmpMap: MutableMap<String, Any?> = circle["data"] as MutableMap<String, Any?>
                val tmpArray: JSONArray = tmpMap["answers"] as JSONArray

                for (index in 0 until answersRb.size) {//Loop everything and make them invisible. For now..
                    answersRb[index].visibility = View.INVISIBLE
                }


                questionTv.text = tmpMap["question"] as String



                for (index in 0 until tmpArray.length()) {
                    //Loop everything and put text
                    answersRb[index].text = tmpArray[index].toString()
                    answersRb[index].visibility = View.VISIBLE
                }

                Log.d("TypeAAA", (tmpMap["correct_answer"] as Int).toString())

                hiddenAnswer.text = tmpArray[tmpMap["correct_answer"] as Int].toString()


            }
            "camera" -> {
                if (this.cameraEnabled) {
                    parentClass.startCamera()
                    cameraLayout.visibility = View.VISIBLE

                }

            }
            "slidingPuzzle" -> {
                slidingPuzzleLayout.visibility = View.VISIBLE

            }
        }
    }

    public fun cameraPermissionsDone() {
        this.cameraEnabled = true
    }

    private fun getMedia(mediaName: Int): Uri? {
        return Uri.parse("android.resource://" + applicationContext.packageName.toString() + "/raw/" + mediaName)
    }



    fun onPause() {

        // Release all the MediaPlayer instances
        for (mediaPlayer in mediaPlayerList.values) {
            mediaPlayer.release()
        }

        // Clear the mediaPlayerList to avoid any confusion or potential issues
        mediaPlayerList.clear()
    }



}
