package gr.aketh.echoes.classes

import GameInterface
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.location.Location
import android.media.MediaPlayer
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.TranslateAnimation
import android.webkit.ConsoleMessage
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.MediaController
import android.widget.PopupWindow
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import android.widget.VideoView
import androidx.camera.core.impl.utils.ContextUtil.getApplicationContext
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Circle
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import gr.aketh.echoes.GameTemplate
import gr.aketh.echoes.R
import gr.aketh.echoes.classes.Const.SWIPETHRESHOLD
import gr.aketh.echoes.classes.JsonUtilities.jsonArrayToMutableMap
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.lang.Error
import kotlin.math.abs


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
    private lateinit var buttonArray: MutableList<MutableList<Float>>
    private lateinit var buttonLayoutArray: MutableList<CustomButton>
    private lateinit var correctLayout : RelativeLayout
    private lateinit var characterLayout: ConstraintLayout
    private lateinit var qrCodeLayout: ConstraintLayout
    private lateinit var webViewLayoutC: ConstraintLayout
    private lateinit var completedActivityLayout: LinearLayout
    private var webviewLayout: WebView

    private lateinit var btnTakePicture: Button
    private lateinit var btnScanBarcode: Button
    private lateinit var barcodeDetector: BarcodeDetector
    private lateinit var cameraSource: CameraSource

    private lateinit var detector: BarcodeDetector
    private var scannedValueQr = ""



    var isSwipeEnabled = true


    private var points = 0

    //Sound Stuff
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var mediaPlayerList: MutableMap<String, MediaPlayer>
    private lateinit var soundeffectsPlayerList: MutableList<MediaPlayer>
    private var hasStarted = false

    private lateinit var allCirclesDebug: MutableList<Circle>


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
        allCirclesDebug = mutableListOf()

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


        mediaPlayerList["cinema"] =
            MediaPlayer.create(applicationContext, getMedia(R.raw.cinema))
        mediaPlayerList["energeia"] =
            MediaPlayer.create(applicationContext, getMedia(R.raw.energeia))
        mediaPlayerList["fotosinthesi"] =
            MediaPlayer.create(applicationContext, getMedia(R.raw.fotosinthesi))
        mediaPlayerList["milos"] =
            MediaPlayer.create(applicationContext, getMedia(R.raw.milos))
        mediaPlayerList["treno"] =
            MediaPlayer.create(applicationContext, getMedia(R.raw.treno))

        mediaPlayerList["voice_1_test"] = MediaPlayer.create(applicationContext, getMedia(R.raw.test_1))
        mediaPlayerList["voice_2_test"] = MediaPlayer.create(applicationContext, getMedia(R.raw.test_2))
        mediaPlayerList["voice_3_test"] = MediaPlayer.create(applicationContext, getMedia(R.raw.test_3))
        mediaPlayerList["voice_4_test"] = MediaPlayer.create(applicationContext, getMedia(R.raw.test_4))
        mediaPlayerList["voice_5_test"] = MediaPlayer.create(applicationContext, getMedia(R.raw.test_5))
        mediaPlayerList["voice_6_test"] = MediaPlayer.create(applicationContext, getMedia(R.raw.test_6))
        mediaPlayerList["voice_7_test"] = MediaPlayer.create(applicationContext, getMedia(R.raw.test_7))
        mediaPlayerList["voice_8_test"] = MediaPlayer.create(applicationContext, getMedia(R.raw.test_8))
        mediaPlayerList["voice_9_test"] = MediaPlayer.create(applicationContext, getMedia(R.raw.test_9))


        //Add sound effect
        soundeffectsPlayerList.add(MediaPlayer.create(applicationContext, getMedia(R.raw.points)))
        soundeffectsPlayerList.add(
            MediaPlayer.create(
                applicationContext,
                getMedia(R.raw.wrong_answer)
            )
        )


        linearLayout1 = linearLayout

        val mediaPlayerP = MediaPlayer.create(applicationContext, getMedia(R.raw.s1))

        //Points text
        pointsTextView = this.activity.findViewById<TextView>(R.id.points)

        //Init the different layouts and do something with the button (MAKE SURE TO PUT THEM AS INCLUDE INSIDE activity_game_template!!!
        quizLayout = this.activity.findViewById<LinearLayout>(R.id.include_quiz_layout)
        var buttonStart = quizLayout.findViewById<Button>(R.id.quiz_bt_answer)
        var quizRadioGroup = quizLayout.findViewById<RadioGroup>(R.id.quiz_rg)

        wrongLayout = this.activity.findViewById<RelativeLayout>(R.id.include_wrong_layout)
        var buttonWrong = wrongLayout.findViewById<Button>(R.id.wrong_bt_close)

        correctLayout = this.activity.findViewById<RelativeLayout>(R.id.include_correct_layout)
        var buttonCorrect = correctLayout.findViewById<Button>(R.id.correct_bt_close)

        cameraLayout = this.activity.findViewById<ConstraintLayout>(R.id.include_camera_layout)
        var buttonDoneCamera = cameraLayout.findViewById<Button>(R.id.camera_bt_done)


        //Sliding Puzzle test
        slidingPuzzleLayout =
            this.activity.findViewById<LinearLayout>(R.id.include_slidingPuzzle_layout)
        var buttonPuzzle8 = slidingPuzzleLayout.findViewById<CustomButton>(R.id.slidingPuzzle_bt_8)
        var buttonPuzzle1 = slidingPuzzleLayout.findViewById<CustomButton>(R.id.slidingPuzzle_bt_1)
        var buttonPuzzle5 = slidingPuzzleLayout.findViewById<CustomButton>(R.id.slidingPuzzle_bt_5)

        var buttonPuzzleEmpty =
            slidingPuzzleLayout.findViewById<CustomButton>(R.id.slidingPuzzle_bt_empty)


        buttonLayoutArray = mutableListOf()//Buttonarray
        var arraySlid: Array<Array<Int>> = arrayOf(
            arrayOf(1, 5, 2),
            arrayOf(4, 3, 6),
            arrayOf(7, 8, -1)
        )

        //Character Layout

        characterLayout = this.activity.findViewById<ConstraintLayout>(R.id.include_character_layout)
        var imageCharacter1 = characterLayout.findViewById<ImageView>(R.id.character_iv_ch1)
        var imageCharacter2 = characterLayout.findViewById<ImageView>(R.id.character_iv_ch2)

        /*
                val characterSheet = BitmapFactory.decodeResource(applicationContext.resources, R.drawable.character_sheet)
                val x = 1880
                val y = 1020
                val width = 715
                val height = 2177

                val croppedBitmap = Bitmap.createBitmap(characterSheet, x, y, width, height)

                val imageViewWidth = imageCharacter1.width // or any desired width for scaling
                val imageViewHeight = imageCharacter1.height // or any desired height for scaling

                val scaledBitmap = Bitmap.createScaledBitmap(croppedBitmap, imageViewWidth, imageViewHeight, true)

                imageCharacter1.setImageBitmap(scaledBitmap)
                imageCharacter2.setImageBitmap(scaledBitmap)
                */

        webViewLayoutC = this.activity.findViewById<ConstraintLayout>(R.id.include_webview_layout)
        webviewLayout = webViewLayoutC.findViewById<WebView>(R.id.webview_web)
        var btX = webViewLayoutC.findViewById<Button>(R.id.webview_bt_x)



        qrCodeLayout = this.activity.findViewById<ConstraintLayout>(R.id.include_qrcode_layout)

        completedActivityLayout = this.activity.findViewById<LinearLayout>(R.id.include_completedActivity_layout)
        var btnCompleteActivity = completedActivityLayout.findViewById<Button>(R.id.completedActivity_bt_answer)

        btnCompleteActivity.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                completedActivityLayout.visibility = View.INVISIBLE
            }
        })

        //Btn for qr
        //btnScanBarcode = this.activity.findViewById<Button>(R.id.btnScanBarcode)
        //btnTakePicture = this.activity.findViewById<Button>(R.id.btnTakePicture)


        //Qr code detector
        //detector = BarcodeDetector.Builder(applicationContext)
        //    .setBarcodeFormats(Barcode.DATA_MATRIX or Barcode.QR_CODE)
        //    .build()

        //if (!detector.isOperational) {
            //txtResultBody.setText("Detector initialisation failed");
           // return;
        //}


        //This starts the QR CODE
        //setupControls()

        /*

        var debugButton = this.activity.findViewById<Button>(R.id.debugButton)
        var cRNEW = jsonList[3]["circle_radius"]
        debugButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                if(cRNEW == jsonList[3]["circle_radius"])
                {
                    jsonList[3]["circle_radius"] = 3
                    allCirclesDebug[3].radius = 1.0;
                }
                else
                {
                    jsonList[3]["circle_radius"] = cRNEW
                    allCirclesDebug[3].radius = cRNEW as Double
                }

            }
        })
        */




        for (x in 1..8) {
            //Finds and loads all the buttons
            val buttonNameId = applicationContext.resources.getIdentifier(
                "slidingPuzzle_bt_$x",
                "id",
                applicationContext.packageName
            )
            val button = slidingPuzzleLayout.findViewById<CustomButton>(buttonNameId)
            val tempBP = button.layoutParams as GridLayout.LayoutParams
            tempBP.rowSpec = tempBP.rowSpec
            tempBP.columnSpec = tempBP.columnSpec
            button.requestLayout()

            button.setOnTouchListener(object : View.OnTouchListener {
                    override fun onTouch(p0: View?, event: MotionEvent?): Boolean {
                        var currentNumberInside = x
                        if (isSwipeEnabled) {


                            when (event?.action) {
                                MotionEvent.ACTION_DOWN -> {
                                    //Temporary solution
                                    buttonArray[0][0] = event.x
                                    buttonArray[0][1] = event.y
                                }

                                MotionEvent.ACTION_MOVE -> {
                                    val dx = event.x - buttonArray[0][0]
                                    val dy = event.y - buttonArray[0][1]
                                    var minusPosition = getCurrentPosition(arraySlid, -1)

                                    if (abs(dx) > SWIPETHRESHOLD || abs(dy) > SWIPETHRESHOLD) {
                                        //Calculate direction

                                        if (abs(dx) > abs(dy)) {
                                            //Left right

                                            if (dx > 0) {

                                                if (findRelativePosition(arraySlid,
                                                     currentNumberInside, -1 ) ==
                                                    RelativePosition.RIGHT && isAdjacentToValue(
                                                        arraySlid,
                                                        currentNumberInside,
                                                        -1
                                                    ))
                                                 {
                                                    //Calculate new position for the button based on the invisible one
                                                    val clickedButtonParams =
                                                        p0!!.layoutParams as GridLayout.LayoutParams
                                                    val emtpyButtonParams =
                                                        buttonPuzzleEmpty.layoutParams as GridLayout.LayoutParams
                                                    val animation = TranslateAnimation(
                                                        Animation.RELATIVE_TO_SELF, 0f,
                                                        Animation.RELATIVE_TO_SELF, 5F,
                                                        Animation.RELATIVE_TO_SELF, 0F,
                                                        Animation.RELATIVE_TO_SELF, 0F

                                                    )

                                                    val tmpS = minusPosition!![0]
                                                    val tmpN = minusPosition[1]

                                                    val oldS = getCurrentPosition(
                                                        arraySlid,
                                                        currentNumberInside
                                                    )

                                                    //val spec = GridLayout.spec(clickedButtonParams., clickedButtonParams.rowSpec.size)

                                                    //clickedButtonParams.rowSpec = emtpyButtonParams.rowSpec
                                                    //clickedButtonParams.columnSpec = emtpyButtonParams.columnSpec
                                                    //var tmpArr: IntArray = calculateNumToRC(tempPos)
                                                    //Button 1

                                                    clickedButtonParams.rowSpec = GridLayout.spec(minusPosition[0], 1.0F)
                                                    clickedButtonParams.columnSpec =
                                                        GridLayout.spec(minusPosition[1],1.0F)


                                                    //Button 2
                                                    emtpyButtonParams.rowSpec =
                                                        GridLayout.spec(minusPosition[0], 1.0F)
                                                    emtpyButtonParams.columnSpec =
                                                        GridLayout.spec(minusPosition[1] - 1, 1.0F)

                                                    //val tempRow = clickedButtonParams.rowSpec
                                                    //val tempColumn = clickedButtonParams.columnSpec
                                                    //clickedButtonParams.rowSpec = emtpyButtonParams.rowSpec
                                                    //clickedButtonParams.columnSpec = emtpyButtonParams.columnSpec
                                                    //emtpyButtonParams.rowSpec = tempRow
                                                    //emtpyButtonParams.columnSpec = tempColumn

                                                    //clickedButtonParams.rowSpec = emtpyButtonParams.rowSpec.also { emtpyButtonParams.rowSpec = clickedButtonParams.rowSpec }
                                                    //clickedButtonParams.columnSpec = emtpyButtonParams.columnSpec.also { emtpyButtonParams.columnSpec = clickedButtonParams.columnSpec }

                                                    Log.d("Button $x", "New button position position col:${minusPosition[0]} row:${minusPosition[1]}")
                                                    Log.d("Button empty", "Emtpy position col:${minusPosition[0]} row:${minusPosition[1]-1}")

                                                    arraySlid = swapAdjacentValues(
                                                        arraySlid,
                                                        currentNumberInside,
                                                        -1
                                                    )

                                                    animation.duration = 300
                                                    //p0!!.startAnimation(animation)


                                                    // Request a layout pass to apply the updated layout parameters
                                                    p0!!.requestLayout()
                                                    buttonPuzzleEmpty.requestLayout()
                                                    //updateLayoutAll()

                                                    isSwipeEnabled = false



                                                    p0?.postDelayed({
                                                        isSwipeEnabled = true
                                                    }, animation.duration)




                                                }
                                            } else {
                                                //minuspositon
                                                // if(calculateRCtoNum(minusPosition[0],minusPosition[1] % 3!=2 && isAdjacentToValue(arraySlid, currentNumberInside, -1))
                                                //Swipe Left
                                                //var minusPosition = getCurrentPosition(arraySlid, -1)
                                                if (findRelativePosition(arraySlid, currentNumberInside ,-1) ==
                                                    RelativePosition.LEFT && isAdjacentToValue(
                                                        arraySlid,
                                                        currentNumberInside,
                                                        -1
                                                    )
                                                ) {
                                                    //Calculate new position for the button based on the invisible one
                                                    val clickedButtonParams =
                                                        p0!!.layoutParams as GridLayout.LayoutParams
                                                    val emtpyButtonParams =
                                                        buttonPuzzleEmpty.layoutParams as GridLayout.LayoutParams
                                                    val animation = TranslateAnimation(
                                                        Animation.RELATIVE_TO_SELF, 0f,
                                                        Animation.RELATIVE_TO_SELF, -5F,
                                                        Animation.RELATIVE_TO_SELF, 0F,
                                                        Animation.RELATIVE_TO_SELF, 0F

                                                    )

                                                    //val tmpS = tempPos
                                                    val tmpN = clickedButtonParams.columnSpec

                                                    //val spec = GridLayout.spec(clickedButtonParams., clickedButtonParams.rowSpec.size)

                                                    //clickedButtonParams.rowSpec = emtpyButtonParams.rowSpec
                                                    //clickedButtonParams.columnSpec = emtpyButtonParams.columnSpec
                                                    //var tmpArr: IntArray = calculateNumToRC(tempPos)

                                                    //Button 1
                                                    clickedButtonParams.rowSpec =
                                                        GridLayout.spec(minusPosition!![0], 1.0F)
                                                    clickedButtonParams.columnSpec =
                                                        GridLayout.spec(minusPosition[1], 1.0F)


                                                    //Button 2
                                                    emtpyButtonParams.rowSpec =
                                                        GridLayout.spec(minusPosition[0], 1.0F)
                                                    emtpyButtonParams.columnSpec =
                                                        GridLayout.spec(minusPosition[1] + 1, 1.0F)

                                                    Log.d("Button $x", "New button position col:${minusPosition[0]} row:${minusPosition[1]}")
                                                    Log.d("Button empty", "Emtpy position col:${minusPosition[0] } row:${minusPosition[1] +1}")

                                                    arraySlid = swapAdjacentValues(
                                                        arraySlid,
                                                        currentNumberInside,
                                                        -1
                                                    )

                                                    animation.duration = 300
                                                    //p0!!.startAnimation(animation)


                                                    // Request a layout pass to apply the updated layout parameters

                                                    p0!!.requestLayout()
                                                    buttonPuzzleEmpty.requestLayout()
                                                    //updateLayoutAll()

                                                    isSwipeEnabled = false



                                                    p0?.postDelayed({
                                                        isSwipeEnabled = true
                                                    }, animation.duration)


                                                }
                                            }
                                        } else {
                                            //top down

                                            if (dy > 0) {
                                                //down
                                                if (findRelativePosition(arraySlid,currentNumberInside ,-1) ==
                                                    RelativePosition.BELOW && isAdjacentToValue(
                                                        arraySlid,
                                                        currentNumberInside,
                                                        -1
                                                    )
                                                ) {
                                                    //Calculate new position for the button based on the invisible one
                                                    val clickedButtonParams =
                                                        p0!!.layoutParams as GridLayout.LayoutParams
                                                    val emtpyButtonParams =
                                                        buttonPuzzleEmpty.layoutParams as GridLayout.LayoutParams
                                                    val animation = TranslateAnimation(
                                                        Animation.RELATIVE_TO_SELF, 0f,
                                                        Animation.RELATIVE_TO_SELF, 0F,
                                                        Animation.RELATIVE_TO_SELF, 0F,
                                                        Animation.RELATIVE_TO_SELF, 5F

                                                    )

                                                    val tmpS = clickedButtonParams.rowSpec
                                                    val tmpN = clickedButtonParams.columnSpec

                                                    //val spec = GridLayout.spec(clickedButtonParams., clickedButtonParams.rowSpec.size)

                                                    //clickedButtonParams.rowSpec = emtpyButtonParams.rowSpec
                                                    //clickedButtonParams.columnSpec = emtpyButtonParams.columnSpec
                                                    //var tmpArr: IntArray = calculateNumToRC(tempPos)
                                                    //Button 1
                                                    clickedButtonParams.rowSpec =
                                                        GridLayout.spec(minusPosition!![0], 1.0F)
                                                    clickedButtonParams.columnSpec =
                                                        GridLayout.spec(minusPosition[1], 1.0F)


                                                    //Button 2
                                                    emtpyButtonParams.rowSpec =
                                                        GridLayout.spec(minusPosition[0] - 1, 1.0F)
                                                    emtpyButtonParams.columnSpec =
                                                        GridLayout.spec(minusPosition[1], 1.0F)


                                                    Log.d("Button $x", "New button position col:${minusPosition[0] } row:${minusPosition[1]}")
                                                    Log.d("Button empty", "Emtpy position col:${minusPosition[0] - 1} row:${minusPosition[1]}")

                                                    arraySlid = swapAdjacentValues(
                                                        arraySlid,
                                                        currentNumberInside,
                                                        -1
                                                    )

                                                    animation.duration = 300
                                                    //p0!!.startAnimation(animation)


                                                    // Request a layout pass to apply the updated layout parameters
                                                    p0!!.requestLayout()
                                                    buttonPuzzleEmpty.requestLayout()
                                                    //updateLayoutAll()

                                                    isSwipeEnabled = false



                                                    p0?.postDelayed({
                                                        isSwipeEnabled = true
                                                    }, animation.duration)


                                                }
                                            } else {
                                                //minuspositon
                                                // if(calculateRCtoNum(minusPosition[0],minusPosition[1] % 3!=2 && isAdjacentToValue(arraySlid, currentNumberInside, -1))
                                                //Swipe Up-----------
                                                //var minusPosition = getCurrentPosition(arraySlid, -1)
                                                if (findRelativePosition(arraySlid,currentNumberInside ,-1 ) ==
                                                    RelativePosition.ABOVE && isAdjacentToValue(
                                                        arraySlid,
                                                        currentNumberInside,
                                                        -1
                                                    )
                                                ) {
                                                    //Calculate new position for the button based on the invisible one
                                                    val clickedButtonParams =
                                                        p0!!.layoutParams as GridLayout.LayoutParams
                                                    val emtpyButtonParams =
                                                        buttonPuzzleEmpty.layoutParams as GridLayout.LayoutParams
                                                    val animation = TranslateAnimation(
                                                        Animation.RELATIVE_TO_SELF, 0f,
                                                        Animation.RELATIVE_TO_SELF, 0F,
                                                        Animation.RELATIVE_TO_SELF, 0F,
                                                        Animation.RELATIVE_TO_SELF, -5F

                                                    )

                                                    val tmpS = clickedButtonParams.rowSpec
                                                    val tmpN = clickedButtonParams.columnSpec

                                                    //val spec = GridLayout.spec(clickedButtonParams., clickedButtonParams.rowSpec.size)

                                                    //clickedButtonParams.rowSpec = emtpyButtonParams.rowSpec
                                                    //clickedButtonParams.columnSpec = emtpyButtonParams.columnSpec
                                                    //var tmpArr: IntArray = calculateNumToRC(tempPos)
                                                    //Button 1
                                                    clickedButtonParams.rowSpec =
                                                        GridLayout.spec(minusPosition!![0], 1.0F)
                                                    clickedButtonParams.columnSpec =
                                                        GridLayout.spec(minusPosition[1], 1.0F)

                                                   // slidingPuzzleLayout.removeView(p0)


                                                    //Button 2
                                                    //val emptyButtonIndex = slidingPuzzleLayout.indexOfChild(buttonPuzzleEmpty)
                                                    emtpyButtonParams.rowSpec =
                                                        GridLayout.spec(minusPosition[0] + 1, 1.0F)
                                                    emtpyButtonParams.columnSpec =
                                                        GridLayout.spec(minusPosition[1], 1.0F)


                                                   // slidingPuzzleLayout.addView(p0,emptyButtonIndex)

                                                    Log.d("Button $x", "New button position col:${minusPosition[0]} row:${minusPosition[1]}")
                                                    Log.d("Button empty", "Emtpy position col:${minusPosition[0] + 1} row:${minusPosition[1]}")

                                                    arraySlid = swapAdjacentValues(
                                                        arraySlid,
                                                        currentNumberInside,
                                                        -1
                                                    )

                                                    animation.duration = 300
                                                    //p0!!.startAnimation(animation)


                                                    // Request a layout pass to apply the updated layout parameters
                                                    p0!!.requestLayout()
                                                    buttonPuzzleEmpty.requestLayout()
                                                    //updateLayoutAll()

                                                    isSwipeEnabled = false



                                                    p0?.postDelayed({
                                                        isSwipeEnabled = true
                                                    }, animation.duration)


                                                }
                                            }
                                        }

                                        if(isArrayEqual(arraySlid))
                                        {
                                            //call some function to win
                                            slidingDone()
                                        }
                                    }
                                }

                            }


                        }

                        return true
                    }


                })
            buttonLayoutArray.add(button)
        }
        //Testing the arrays to see if movement works
        buttonArray = mutableListOf()
        buttonArray.add(mutableListOf(0.0F, 0.0F))

        buttonArray[0][0] = 0.0F
        buttonArray[0][1] = 0.0F
        var tempPos: IntArray = intArrayOf(2, 2);








        /*buttonPuzzle8.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(p0: View?, event: MotionEvent?): Boolean {
                var currentNumberInside = 8
                if (isSwipeEnabled) {


                    when (event?.action) {
                        MotionEvent.ACTION_DOWN -> {
                            //Temporary solution
                            buttonArray[0][0] = event.x
                            buttonArray[0][1] = event.y
                        }

                        MotionEvent.ACTION_MOVE -> {
                            val dx = event.x - buttonArray[0][0]
                            val dy = event.y - buttonArray[0][1]
                            var minusPosition = getCurrentPosition(arraySlid, -1)

                            if (abs(dx) > SWIPETHRESHOLD || abs(dy) > SWIPETHRESHOLD) {
                                //Calculate direction

                                if (abs(dx) > abs(dy)) {
                                    //Left right

                                    if (dx > 0) {
                                        if (((calculateRCtoNum(
                                                minusPosition!![0],
                                                minusPosition[1]
                                            ) % 3) != 0) && isAdjacentToValue(
                                                arraySlid,
                                                currentNumberInside,
                                                -1
                                            )) {
                                            //Calculate new position for the button based on the invisible one
                                            val clickedButtonParams =
                                                p0!!.layoutParams as GridLayout.LayoutParams
                                            val emtpyButtonParams =
                                                buttonPuzzleEmpty.layoutParams as GridLayout.LayoutParams
                                            val animation = TranslateAnimation(
                                                Animation.RELATIVE_TO_SELF, 0f,
                                                Animation.RELATIVE_TO_SELF, 10F,
                                                Animation.RELATIVE_TO_SELF, 0F,
                                                Animation.RELATIVE_TO_SELF, 0F

                                            )

                                            val tmpS = minusPosition[0]
                                            val tmpN = minusPosition[1]

                                            val oldS = getCurrentPosition(arraySlid, currentNumberInside)

                                            //val spec = GridLayout.spec(clickedButtonParams., clickedButtonParams.rowSpec.size)

                                            //clickedButtonParams.rowSpec = emtpyButtonParams.rowSpec
                                            //clickedButtonParams.columnSpec = emtpyButtonParams.columnSpec
                                            //var tmpArr: IntArray = calculateNumToRC(tempPos)
                                            //Button 1
                                            clickedButtonParams.rowSpec =
                                                GridLayout.spec(minusPosition[0])
                                            clickedButtonParams.columnSpec =
                                                GridLayout.spec(minusPosition[1])


                                            //Button 2
                                            emtpyButtonParams.rowSpec = GridLayout.spec(minusPosition[0])
                                            emtpyButtonParams.columnSpec =
                                                GridLayout.spec(minusPosition[1] - 1)

                                            arraySlid = swapAdjacentValues(arraySlid, currentNumberInside, -1)

                                            animation.duration = 300
                                            buttonPuzzle8.startAnimation(animation)


                                            // Request a layout pass to apply the updated layout parameters
                                            buttonPuzzle8.requestLayout()
                                            buttonPuzzleEmpty.requestLayout()
                                            updateLayoutAll()

                                            isSwipeEnabled = false



                                            p0?.postDelayed({
                                                isSwipeEnabled = true
                                            }, animation.duration)


                                        }
                                    } else {
                                        //minuspositon
                                        // if(calculateRCtoNum(minusPosition[0],minusPosition[1] % 3!=2 && isAdjacentToValue(arraySlid, currentNumberInside, -1))
                                        //Swipe Left
                                        //var minusPosition = getCurrentPosition(arraySlid, -1)
                                        if (((calculateRCtoNum(
                                                minusPosition!![0],
                                                minusPosition[1]
                                            ) % 3) != 2) && isAdjacentToValue(
                                                arraySlid,
                                                currentNumberInside,
                                                -1
                                            )
                                        ) {
                                            //Calculate new position for the button based on the invisible one
                                            val clickedButtonParams =
                                                p0!!.layoutParams as GridLayout.LayoutParams
                                            val emtpyButtonParams =
                                                buttonPuzzleEmpty.layoutParams as GridLayout.LayoutParams
                                            val animation = TranslateAnimation(
                                                Animation.RELATIVE_TO_SELF, 0f,
                                                Animation.RELATIVE_TO_SELF, -5F,
                                                Animation.RELATIVE_TO_SELF, 0F,
                                                Animation.RELATIVE_TO_SELF, 0F

                                            )

                                            val tmpS = tempPos
                                            val tmpN = clickedButtonParams.columnSpec

                                            //val spec = GridLayout.spec(clickedButtonParams., clickedButtonParams.rowSpec.size)

                                            //clickedButtonParams.rowSpec = emtpyButtonParams.rowSpec
                                            //clickedButtonParams.columnSpec = emtpyButtonParams.columnSpec
                                            //var tmpArr: IntArray = calculateNumToRC(tempPos)

                                            //Button 1
                                            clickedButtonParams.rowSpec =
                                                GridLayout.spec(tempPos[0])
                                            clickedButtonParams.columnSpec =
                                                GridLayout.spec(tempPos[1])


                                            //Button 2
                                            emtpyButtonParams.rowSpec =
                                                GridLayout.spec(tempPos[0])
                                            emtpyButtonParams.columnSpec =
                                                GridLayout.spec(tempPos[1] + 1)

                                            arraySlid = swapAdjacentValues(arraySlid, currentNumberInside, -1)

                                            animation.duration = 300
                                            buttonPuzzle8.startAnimation(animation)


                                            // Request a layout pass to apply the updated layout parameters

                                            buttonPuzzle8.requestLayout()
                                            buttonPuzzleEmpty.requestLayout()
                                            updateLayoutAll()

                                            isSwipeEnabled = false



                                            p0?.postDelayed({
                                                isSwipeEnabled = true
                                            }, animation.duration)


                                        }
                                    }
                                }
                                else
                                {
                                    //top down

                                    if (dy > 0) {
                                        //down
                                        if ((calculateRCtoNum(
                                                minusPosition!![0],
                                                minusPosition[1]
                                            ) > 2) && isAdjacentToValue(
                                                arraySlid,
                                                currentNumberInside,
                                                -1
                                            )) {
                                            //Calculate new position for the button based on the invisible one
                                            val clickedButtonParams =
                                                p0!!.layoutParams as GridLayout.LayoutParams
                                            val emtpyButtonParams =
                                                buttonPuzzleEmpty.layoutParams as GridLayout.LayoutParams
                                            val animation = TranslateAnimation(
                                                Animation.RELATIVE_TO_SELF, 0f,
                                                Animation.RELATIVE_TO_SELF, 0F,
                                                Animation.RELATIVE_TO_SELF, 0F,
                                                Animation.RELATIVE_TO_SELF, 5F

                                            )

                                            val tmpS = clickedButtonParams.rowSpec
                                            val tmpN = clickedButtonParams.columnSpec

                                            //val spec = GridLayout.spec(clickedButtonParams., clickedButtonParams.rowSpec.size)

                                            //clickedButtonParams.rowSpec = emtpyButtonParams.rowSpec
                                            //clickedButtonParams.columnSpec = emtpyButtonParams.columnSpec
                                            //var tmpArr: IntArray = calculateNumToRC(tempPos)
                                            //Button 1
                                            clickedButtonParams.rowSpec =
                                                GridLayout.spec(minusPosition[0])
                                            clickedButtonParams.columnSpec =
                                                GridLayout.spec(minusPosition[1])


                                            //Button 2
                                            emtpyButtonParams.rowSpec = GridLayout.spec(minusPosition[0] + 1)
                                            emtpyButtonParams.columnSpec =
                                                GridLayout.spec(minusPosition[1] )

                                            arraySlid = swapAdjacentValues(arraySlid, currentNumberInside, -1)

                                            animation.duration = 300
                                            buttonPuzzle8.startAnimation(animation)


                                            // Request a layout pass to apply the updated layout parameters
                                            buttonPuzzle8.requestLayout()
                                            buttonPuzzleEmpty.requestLayout()
                                            updateLayoutAll()

                                            isSwipeEnabled = false



                                            p0?.postDelayed({
                                                isSwipeEnabled = true
                                            }, animation.duration)


                                        }
                                    } else {
                                        //minuspositon
                                        // if(calculateRCtoNum(minusPosition[0],minusPosition[1] % 3!=2 && isAdjacentToValue(arraySlid, currentNumberInside, -1))
                                        //Swipe Up
                                        //var minusPosition = getCurrentPosition(arraySlid, -1)
                                        if ((calculateRCtoNum(
                                                minusPosition!![0],
                                                minusPosition[1]
                                            ) < 6) && isAdjacentToValue(
                                                arraySlid,
                                                currentNumberInside,
                                                -1
                                            )
                                        ) {
                                            //Calculate new position for the button based on the invisible one
                                            val clickedButtonParams =
                                                p0!!.layoutParams as GridLayout.LayoutParams
                                            val emtpyButtonParams =
                                                buttonPuzzleEmpty.layoutParams as GridLayout.LayoutParams
                                            val animation = TranslateAnimation(
                                                Animation.RELATIVE_TO_SELF, 0f,
                                                Animation.RELATIVE_TO_SELF, 0F,
                                                Animation.RELATIVE_TO_SELF, 0F,
                                                Animation.RELATIVE_TO_SELF, -5F

                                            )

                                            val tmpS = clickedButtonParams.rowSpec
                                            val tmpN = clickedButtonParams.columnSpec

                                            //val spec = GridLayout.spec(clickedButtonParams., clickedButtonParams.rowSpec.size)

                                            //clickedButtonParams.rowSpec = emtpyButtonParams.rowSpec
                                            //clickedButtonParams.columnSpec = emtpyButtonParams.columnSpec
                                            //var tmpArr: IntArray = calculateNumToRC(tempPos)
                                            //Button 1
                                            clickedButtonParams.rowSpec =
                                                GridLayout.spec(minusPosition[0])
                                            clickedButtonParams.columnSpec =
                                                GridLayout.spec(minusPosition[1])


                                            //Button 2
                                            emtpyButtonParams.rowSpec =
                                                GridLayout.spec(minusPosition[0] - 1)
                                            emtpyButtonParams.columnSpec =
                                                GridLayout.spec(minusPosition[1] )

                                            arraySlid = swapAdjacentValues(arraySlid, currentNumberInside, -1)

                                            animation.duration = 300
                                            buttonPuzzle8.startAnimation(animation)


                                            // Request a layout pass to apply the updated layout parameters
                                            buttonPuzzle8.requestLayout()
                                            buttonPuzzleEmpty.requestLayout()
                                            updateLayoutAll()

                                            isSwipeEnabled = false



                                            p0?.postDelayed({
                                                isSwipeEnabled = true
                                            }, animation.duration)


                                        }
                                    }
                                }
                            }
                        }

                    }


                }
                return true
            }


        })*/


        //
        /*buttonPuzzle1.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(p0: View?, event: MotionEvent?): Boolean {
                var currentNumberInside = 1
                if (isSwipeEnabled) {


                    when (event?.action) {
                        MotionEvent.ACTION_DOWN -> {
                            //Temporary solution
                            buttonArray[0][0] = event.x
                            buttonArray[0][1] = event.y
                        }

                        MotionEvent.ACTION_MOVE -> {
                            val dx = event.x - buttonArray[0][0]
                            val dy = event.y - buttonArray[0][1]
                            var minusPosition = getCurrentPosition(arraySlid, -1)

                            if (abs(dx) > SWIPETHRESHOLD || abs(dy) > SWIPETHRESHOLD) {
                                //Calculate direction

                                if (abs(dx) > abs(dy)) {
                                    //Left right

                                    if (dx > 0)
                                    {
                                        if (((calculateRCtoNum(
                                                minusPosition!![0],
                                                minusPosition[1]
                                            ) % 3) != 0) && isAdjacentToValue(
                                                arraySlid,
                                                currentNumberInside,
                                                -1
                                            )) {
                                            //Calculate new position for the button based on the invisible one
                                            val clickedButtonParams =
                                                buttonPuzzle1.layoutParams as GridLayout.LayoutParams
                                            val emtpyButtonParams =
                                                buttonPuzzleEmpty.layoutParams as GridLayout.LayoutParams
                                            val animation = TranslateAnimation(
                                                Animation.RELATIVE_TO_SELF, 0f,
                                                Animation.RELATIVE_TO_SELF, 10F,
                                                Animation.RELATIVE_TO_SELF, 0F,
                                                Animation.RELATIVE_TO_SELF, 0F

                                            )

                                            val tmpS = minusPosition[0]
                                            val tmpN = minusPosition[1]

                                            val oldS = getCurrentPosition(arraySlid, currentNumberInside)

                                            //val spec = GridLayout.spec(clickedButtonParams., clickedButtonParams.rowSpec.size)

                                            //clickedButtonParams.rowSpec = emtpyButtonParams.rowSpec
                                            //clickedButtonParams.columnSpec = emtpyButtonParams.columnSpec
                                            //var tmpArr: IntArray = calculateNumToRC(tempPos)
                                            //Button 1
                                            clickedButtonParams.rowSpec =
                                                GridLayout.spec(minusPosition[0])
                                            clickedButtonParams.columnSpec =
                                                GridLayout.spec(minusPosition[1])


                                            //Button 2
                                            emtpyButtonParams.rowSpec = GridLayout.spec(minusPosition[0])
                                            emtpyButtonParams.columnSpec =
                                                GridLayout.spec(minusPosition[1] - 1)

                                            arraySlid = swapAdjacentValues(arraySlid, currentNumberInside, -1)

                                            animation.duration = 300
                                            buttonPuzzle1.startAnimation(animation)


                                            // Request a layout pass to apply the updated layout parameters
                                            buttonPuzzle1.requestLayout()
                                            buttonPuzzleEmpty.requestLayout()

                                            isSwipeEnabled = false



                                            p0?.postDelayed({
                                                isSwipeEnabled = true
                                            }, animation.duration)


                                        }
                                    } else {
                                        //minuspositon
                                        // if(calculateRCtoNum(minusPosition[0],minusPosition[1] % 3!=2 && isAdjacentToValue(arraySlid, currentNumberInside, -1))
                                        //Swipe Left
                                        //var minusPosition = getCurrentPosition(arraySlid, -1)
                                        if (((calculateRCtoNum(
                                                minusPosition!![0],
                                                minusPosition[1]
                                            ) % 3) != 2) && isAdjacentToValue(
                                                arraySlid,
                                                currentNumberInside,
                                                -1
                                            )
                                        ) {
                                            //Calculate new position for the button based on the invisible one
                                            val clickedButtonParams =
                                                buttonPuzzle1.layoutParams as GridLayout.LayoutParams
                                            val emtpyButtonParams =
                                                buttonPuzzleEmpty.layoutParams as GridLayout.LayoutParams
                                            val animation = TranslateAnimation(
                                                Animation.RELATIVE_TO_SELF, 0f,
                                                Animation.RELATIVE_TO_SELF, -5F,
                                                Animation.RELATIVE_TO_SELF, 0F,
                                                Animation.RELATIVE_TO_SELF, 0F

                                            )

                                            val tmpS = tempPos
                                            val tmpN = clickedButtonParams.columnSpec

                                            //val spec = GridLayout.spec(clickedButtonParams., clickedButtonParams.rowSpec.size)

                                            //clickedButtonParams.rowSpec = emtpyButtonParams.rowSpec
                                            //clickedButtonParams.columnSpec = emtpyButtonParams.columnSpec
                                            //var tmpArr: IntArray = calculateNumToRC(tempPos)

                                            Log.d("", "onTouch: ")

                                            //Button 1
                                            clickedButtonParams.rowSpec =
                                                GridLayout.spec(tempPos[0])
                                            clickedButtonParams.columnSpec =
                                                GridLayout.spec(tempPos[1])


                                            //Button 2
                                            emtpyButtonParams.rowSpec =
                                                GridLayout.spec(tempPos[0])
                                            emtpyButtonParams.columnSpec =
                                                GridLayout.spec(tempPos[1] + 1)

                                            arraySlid = swapAdjacentValues(arraySlid, currentNumberInside, -1)

                                            animation.duration = 300
                                            buttonPuzzle1.startAnimation(animation)


                                            // Request a layout pass to apply the updated layout parameters
                                            buttonPuzzle1.requestLayout()
                                            buttonPuzzleEmpty.requestLayout()

                                            isSwipeEnabled = false



                                            p0?.postDelayed({
                                                isSwipeEnabled = true
                                            }, animation.duration)


                                        }
                                    }
                                }
                                else
                                {
                                    //top down

                                    if (dy > 0) {
                                        //down
                                        if ((calculateRCtoNum(
                                                minusPosition!![0],
                                                minusPosition[1]
                                            ) > 2) && isAdjacentToValue(
                                                arraySlid,
                                                currentNumberInside,
                                                -1
                                            )) {
                                            //Calculate new position for the button based on the invisible one
                                            val clickedButtonParams =
                                                buttonPuzzle1.layoutParams as GridLayout.LayoutParams
                                            val emtpyButtonParams =
                                                buttonPuzzleEmpty.layoutParams as GridLayout.LayoutParams
                                            val animation = TranslateAnimation(
                                                Animation.RELATIVE_TO_SELF, 0f,
                                                Animation.RELATIVE_TO_SELF, 0F,
                                                Animation.RELATIVE_TO_SELF, 0F,
                                                Animation.RELATIVE_TO_SELF, 5F

                                            )

                                            val tmpS = clickedButtonParams.rowSpec
                                            val tmpN = clickedButtonParams.columnSpec

                                            //val spec = GridLayout.spec(clickedButtonParams., clickedButtonParams.rowSpec.size)

                                            //clickedButtonParams.rowSpec = emtpyButtonParams.rowSpec
                                            //clickedButtonParams.columnSpec = emtpyButtonParams.columnSpec
                                            //var tmpArr: IntArray = calculateNumToRC(tempPos)
                                            //Button 1
                                            clickedButtonParams.rowSpec =
                                                GridLayout.spec(minusPosition[0])
                                            clickedButtonParams.columnSpec =
                                                GridLayout.spec(minusPosition[1])


                                            //Button 2
                                            emtpyButtonParams.rowSpec = GridLayout.spec(minusPosition[0] + 1)
                                            emtpyButtonParams.columnSpec =
                                                GridLayout.spec(minusPosition[1] )

                                            arraySlid = swapAdjacentValues(arraySlid, currentNumberInside, -1)

                                            animation.duration = 300
                                            buttonPuzzle1.startAnimation(animation)


                                            // Request a layout pass to apply the updated layout parameters
                                            buttonPuzzle1.requestLayout()
                                            buttonPuzzleEmpty.requestLayout()

                                            isSwipeEnabled = false



                                            p0?.postDelayed({
                                                isSwipeEnabled = true
                                            }, animation.duration)


                                        }
                                    } else {
                                        //minuspositon
                                        // if(calculateRCtoNum(minusPosition[0],minusPosition[1] % 3!=2 && isAdjacentToValue(arraySlid, currentNumberInside, -1))
                                        //Swipe Up
                                        //var minusPosition = getCurrentPosition(arraySlid, -1)
                                        if ((calculateRCtoNum(
                                                minusPosition!![0],
                                                minusPosition[1]
                                            ) < 6) && isAdjacentToValue(
                                                arraySlid,
                                                currentNumberInside,
                                                -1
                                            )
                                        ) {
                                            //Calculate new position for the button based on the invisible one
                                            val clickedButtonParams =
                                                buttonPuzzle1.layoutParams as GridLayout.LayoutParams
                                            val emtpyButtonParams =
                                                buttonPuzzleEmpty.layoutParams as GridLayout.LayoutParams
                                            val animation = TranslateAnimation(
                                                Animation.RELATIVE_TO_SELF, 0f,
                                                Animation.RELATIVE_TO_SELF, 0F,
                                                Animation.RELATIVE_TO_SELF, 0F,
                                                Animation.RELATIVE_TO_SELF, -5F

                                            )

                                            val tmpS = clickedButtonParams.rowSpec
                                            val tmpN = clickedButtonParams.columnSpec

                                            //val spec = GridLayout.spec(clickedButtonParams., clickedButtonParams.rowSpec.size)

                                            //clickedButtonParams.rowSpec = emtpyButtonParams.rowSpec
                                            //clickedButtonParams.columnSpec = emtpyButtonParams.columnSpec
                                            //var tmpArr: IntArray = calculateNumToRC(tempPos)
                                            //Button 1
                                            clickedButtonParams.rowSpec =
                                                GridLayout.spec(minusPosition[0])
                                            clickedButtonParams.columnSpec =
                                                GridLayout.spec(minusPosition[1])


                                            //Button 2
                                            emtpyButtonParams.rowSpec =
                                                GridLayout.spec(minusPosition[0] - 1)
                                            emtpyButtonParams.columnSpec =
                                                GridLayout.spec(minusPosition[1] )

                                            arraySlid = swapAdjacentValues(arraySlid, currentNumberInside, -1)

                                            animation.duration = 300
                                            buttonPuzzle1.startAnimation(animation)


                                            // Request a layout pass to apply the updated layout parameters
                                            buttonPuzzle1.requestLayout()
                                            buttonPuzzleEmpty.requestLayout()

                                            isSwipeEnabled = false



                                            p0?.postDelayed({
                                                isSwipeEnabled = true
                                            }, animation.duration)


                                        }
                                    }
                                }
                            }
                        }

                    }


                }
                return true
            }


        })*/


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
                correctLayout.visibility = View.VISIBLE//Sets the new visibility so it works correctly
            }
        })




        btX.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View?)
            {
                webViewLayoutC.visibility = View.GONE
                correctLayout.visibility = View.VISIBLE
            }
        })



        //btnTakePicture = this.activity.findViewById<Button>(R.id.btnTakePicture)






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
                Log.d("hello World", "Workes")
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

                //allCirclesDebug.add(currentCircle)

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

                //If outside the radius it stops the music
                if(!(circle["finished"] as Boolean))
                {
                    if (distance[0] > circle["circle_radius"] as Double) {
                        if (circle["running"] as Boolean) {

                            this.mediaPlayerList[circle["sound"]]?.pause()
                            disableCharacterLayoutVisibility()

                            //handler case
                            val handlerC = circle["handler"] as Handler
                            HandlerManager.pauseHandler(handlerC)

                            circle["running"] = false

                            disableCharacterLayoutVisibility()

                        }


                    }
                    else if (!(circle["running"] as Boolean)) {

                        this.mediaPlayerList[circle["sound"]]?.start()
                        enableCharacterLayoutVisibility()

                        if((circle["title"] as String) == "jewish"){
                            characterLayout.findViewById<ImageView>(R.id.character_iv_ch2).setImageResource(R.drawable.play_and_learn_little_girl)

                        }else{
                            characterLayout.findViewById<ImageView>(R.id.character_iv_ch2).setImageResource(R.drawable.kara)
                        }

                        //Adds a listener to end the sound only if
                        this.mediaPlayerList[circle["sound"]]?.apply {
                            // Check if the OnCompletionListener is already set
                            if (circle["onCompletionListenerSet"] as? Boolean != true) {
                                setOnCompletionListener {
                                    // Set circle["finished"] to true when the playback ends
                                    circle["finished"] = true
                                }
                                // Mark the OnCompletionListener as set
                                circle["onCompletionListenerSet"] = true
                            }
                        }




                        //Read the quiz layout and display it
                        if (circle["type"] == "quiz" && !(circle["running"] as Boolean)) {
                            val handlerC = circle["handler"] as? Handler ?: Handler(Looper.getMainLooper())
                            val mediaPlayer = this.mediaPlayerList[circle["sound"]]
                            if(!(circle["started"] as Boolean)){
                                circle["handler"] = handlerC

                                HandlerManager.subscribeHandler(handlerC, {
                                    this.quizLayout.visibility = View.VISIBLE
                                    disableCharacterLayoutVisibility()
                                    this.showCorrectLayoutWithContent(circle)
                                }, mediaPlayer)

                                val durationC = this.mediaPlayerList[circle["sound"]]!!.duration.toLong()

                                circle["started"] = true
                            }
                            HandlerManager.resumeHandler(handlerC, mediaPlayer)




                        }
                        else if ((circle["type"] == "slidingPuzzle") or (circle["type"] == "puzzleV2")
                            or (circle["type"] == "wordSearch")
                            or (circle["type"] == "justAnswer")
                            or (circle["type"] == "camera") or (circle["type"] == "matchPairs")
                            or (circle["type"] == "qrCode")) {

                            val handlerC = circle["handler"] as? Handler ?: Handler(Looper.getMainLooper())
                            val mediaPlayer = this.mediaPlayerList[circle["sound"]]
                            if(!(circle["started"] as Boolean)) {
                                circle["handler"] = handlerC

                                val mediaPlayer = this.mediaPlayerList[circle["sound"]]
                                HandlerManager.subscribeHandler(handlerC, {
                                    disableCharacterLayoutVisibility()
                                    this.showCorrectLayoutWithContent(circle)
                                }, mediaPlayer)

                                Log.d("SOUND NAME", circle["sound"] as String)
                                val durationC =

                                    this.mediaPlayerList[circle["sound"]]!!.duration.toLong()

                                circle["started"] = true
                            }

                            var tdurationC = this.mediaPlayerList[circle["sound"]]!!.duration.toLong()
                            var tTotal = mediaPlayer?.currentPosition
                            var messageA = "tDuration: $tdurationC\ntTotal: $tTotal"
                            Toast.makeText(applicationContext,messageA,Toast.LENGTH_LONG).show();
                            HandlerManager.resumeHandler(handlerC, mediaPlayer)


                        }

                        circle["running"] = true



                    }
                }
                else if((distance[0] <= (circle["circle_radius"] as Double)) and circle["finished"] as Boolean){
                    //Show that it has finished
                    showCorrectLayoutWithContent(mutableMapOf<String,Any?>().apply { put("type", "finished") })
                }



            } catch (e: Exception) {
                Log.d("FPPP", "Exception occurred: ${e.message}")
                Log.d("FPPP", Log.getStackTraceString(e))
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

                var bt = correctLayout.findViewById<TextView>(R.id.correct_tv_desc)
                bt.text = "Τα γράμματα σου είναι: " + tmpMap["letters"]



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
                    var bt = correctLayout.findViewById<TextView>(R.id.correct_tv_desc)
                    val tmpMap: MutableMap<String, Any?> = circle["data"] as MutableMap<String, Any?>
                    bt.text = "Τα γράμματα σου είναι: "+ tmpMap["letters"]

                }

            }

            "slidingPuzzle" -> {
                slidingPuzzleLayout.visibility = View.VISIBLE
                //qrPuzzle =

                var bt = correctLayout.findViewById<TextView>(R.id.correct_tv_desc)
                val tmpMap: MutableMap<String, Any?> = circle["data"] as MutableMap<String, Any?>
                bt.text = "Τα γράμματα σου είναι: "+ tmpMap["letters"]

            }

            "justAnswer" -> {
                //slidingPuzzleLayout.visibility = View.VISIBLE
                //qrPuzzle =

                var bt = correctLayout.findViewById<TextView>(R.id.correct_tv_desc)
                val tmpMap: MutableMap<String, Any?> = circle["data"] as MutableMap<String, Any?>
                bt.text = "Τα γράμματα σου είναι: "+ tmpMap["letters"]
                correctLayout.visibility = View.VISIBLE

            }

            "puzzleV2", "wordSearch", "matchPairs" -> {
                webViewLayoutC.visibility = View.VISIBLE
                webviewLayout.visibility = View.VISIBLE
                webviewLayout.settings.javaScriptEnabled = true
                webviewLayout.addJavascriptInterface(GameInterface(activity as GameTemplate), "AndroidGameInterface")
                webviewLayout.webViewClient = WebViewClient()
                webviewLayout.webChromeClient = WebChromeClient()



                //webviewLayout.settings.loadWithOverviewMode = true
               // webviewLayout.settings.useWideViewPort = true
                //webviewLayout.settings.setSupportZoom(true)
                //webviewLayout.settings.builtInZoomControls = true
                //webviewLayout.settings.displayZoomControls = false
                //webviewLayout.setInitialScale(140); // Example: Set initial scale to 100%


                webviewLayout.webChromeClient = object : WebChromeClient() {
                    override fun onConsoleMessage(consoleMessage: ConsoleMessage): Boolean {
                        Log.d("WebViewConsole", consoleMessage.message())
                        return true
                    }
                }




                WebView.setWebContentsDebuggingEnabled(true)


                // Enable loading local content
                webviewLayout.settings.allowFileAccess = true
                webviewLayout.settings.allowFileAccessFromFileURLs = true
                webviewLayout.settings.allowUniversalAccessFromFileURLs = true

                Log.d("gameUrl", circle["gameUrl"] as String)
                webviewLayout.loadUrl(circle["gameUrl"] as String)
                //webviewLayout.loadUrl("file:///android_asset/Content/WordSearch/index.html")

            }

            "qrCode" ->{
                qrCodeLayout.visibility = View.VISIBLE
                setupControls()
            }

            "finished" ->{
                completedActivityLayout.visibility = View.VISIBLE
            }
        }
    }

    private fun setupControls() {

        val aniSlide: Animation =
            AnimationUtils.loadAnimation(applicationContext, R.anim.scanner_animation)
        val barcodeLineP = this.activity.findViewById<ConstraintLayout>(R.id.include_qrcode_layout)
        val barcodeLine = barcodeLineP.findViewById<View>(R.id.barcode_line)

        barcodeDetector =
            BarcodeDetector.Builder(applicationContext).setBarcodeFormats(Barcode.ALL_FORMATS).build()

        barcodeLine.startAnimation(aniSlide)

        cameraSource = CameraSource.Builder(applicationContext, barcodeDetector)
            .setRequestedPreviewSize(1920, 1080)
            .setAutoFocusEnabled(true) //you should add this feature
            .build()

        var cameraSurfaceView = this.activity.findViewById<SurfaceView>(R.id.cameraSurfaceView)

        cameraSurfaceView.holder.addCallback(object : SurfaceHolder.Callback {
            @SuppressLint("MissingPermission")
            override fun surfaceCreated(holder: SurfaceHolder) {
                try {
                    //Start preview after 1s delay
                    cameraSource.start(holder)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }

            @SuppressLint("MissingPermission")
            override fun surfaceChanged(
                holder: SurfaceHolder,
                format: Int,
                width: Int,
                height: Int
            ) {
                try {
                    cameraSource.start(holder)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }

            override fun surfaceDestroyed(holder: SurfaceHolder) {
                cameraSource.stop()
            }
        })


        barcodeDetector.setProcessor(object : Detector.Processor<Barcode> {
            override fun release() {
                Toast.makeText(applicationContext, "Scanner has been closed", Toast.LENGTH_SHORT)
                    .show()
            }

            override fun receiveDetections(detections: Detector.Detections<Barcode>) {
                val barcodes = detections.detectedItems
                if (barcodes.size() == 1) {
                    scannedValueQr = barcodes.valueAt(0).rawValue


                    //Don't forget to add this line printing value or finishing activity must run on main thread
                    parentClass.runOnUiThread {
                        cameraSource.stop()
                        Toast.makeText(applicationContext, "value- $scannedValueQr", Toast.LENGTH_SHORT).show()

                        qrCodeLayout.visibility = View.GONE
                        //parentClass.finish()


                    }
                }else
                {
                    Toast.makeText(applicationContext
                        , "value- else", Toast.LENGTH_SHORT).show()

                }
            }
        })
    }

    private fun toggleCharacterLayoutVisibility()
    {
        if(this.characterLayout.visibility == View.VISIBLE)
        {
            this.characterLayout.visibility = View.INVISIBLE
        }
        else
        {
            this.characterLayout.visibility = View.VISIBLE
        }

    }

    private fun enableCharacterLayoutVisibility()
    {
        this.characterLayout.visibility = View.VISIBLE
    }

    private fun disableCharacterLayoutVisibility()
    {
        this.characterLayout.visibility = View.GONE
    }
    private fun slidingDone()
    {
        slidingPuzzleLayout.visibility = View.INVISIBLE
        correctLayout.visibility = View.VISIBLE
    }

    public fun cameraPermissionsDone() {
        this.cameraEnabled = true
    }

    private fun getMedia(mediaName: Int): Uri? {
        return Uri.parse("android.resource://" + applicationContext.packageName.toString() + "/raw/" + mediaName)
    }


    private fun calculateNumToRC(number: Int): IntArray {
        if (number in 0..8) {

            var div: Int = number / 3 //row
            var mod: Int = number % 3 //collumn

            return intArrayOf(div, mod)

        }

        return intArrayOf(-1, -1)


    }

    private fun calculateRCtoNum(row: Int, column: Int): Int {
        if (row in 0..2 && column in 0..2) {
            // Convert row and column to number
            return row * 3 + column
        }
        return -1
    }



    fun isAdjacentToValue(
        matrix: Array<Array<Int>>,
        targetValue: Int,
        adjacentValue: Int
    ): Boolean {
        val rows = matrix.size
        val cols = matrix[0].size

        for (i in 0 until rows) {
            for (j in 0 until cols) {
                if (matrix[i][j] == targetValue) {
                    // Check top element
                    if (i > 0 && matrix[i - 1][j] == adjacentValue) {
                        return true
                    }
                    // Check bottom element
                    if (i < rows - 1 && matrix[i + 1][j] == adjacentValue) {
                        return true
                    }
                    // Check left element
                    if (j > 0 && matrix[i][j - 1] == adjacentValue) {
                        return true
                    }
                    // Check right element
                    if (j < cols - 1 && matrix[i][j + 1] == adjacentValue) {
                        return true
                    }
                }
            }
        }
        return false
    }

    fun getCurrentPosition(matrix: Array<Array<Int>>, targetValue: Int): Array<Int>? {
        val rows = matrix.size
        val cols = matrix[0].size

        for (i in 0 until rows) {
            for (j in 0 until cols) {
                if (matrix[i][j] == targetValue) {
                    return arrayOf(i, j)
                }
            }
        }
        return null
    }

    fun swapAdjacentValues(matrix: Array<Array<Int>>, value1: Int, value2: Int): Array<Array<Int>> {
        val rows = matrix.size
        val cols = matrix[0].size

        var row1 = -1
        var col1 = -1
        var row2 = -1
        var col2 = -1

        // Find the positions of the two values in the matrix
        for (i in 0 until rows) {
            for (j in 0 until cols) {
                if (matrix[i][j] == value1) {
                    row1 = i
                    col1 = j
                } else if (matrix[i][j] == value2) {
                    row2 = i
                    col2 = j
                }
            }
        }

        // Check if both values are found and are adjacent
        if (row1 != -1 && col1 != -1 && row2 != -1 && col2 != -1 &&
            (Math.abs(row1 - row2) == 1 && col1 == col2 || row1 == row2 && Math.abs(col1 - col2) == 1)
        ) {
            // Swap the values using a temporary variable
            val temp = matrix[row1][col1]
            matrix[row1][col1] = matrix[row2][col2]
            matrix[row2][col2] = temp
        }

        return matrix
    }


    fun updateLayoutAll() {
        for (element in buttonLayoutArray) {
            element.requestLayout()


        }
    }

    enum class RelativePosition { ABOVE, BELOW, LEFT, RIGHT, NONE }

    fun findRelativePosition(arr: Array<Array<Int>>, value1: Int, value2: Int): RelativePosition {
        var value1Row = -1
        var value1Col = -1
        var value2Row = -1
        var value2Col = -1

        // Find the indices of value1 and value2 in the array
        for (i in arr.indices) {
            for (j in arr[i].indices) {
                if (arr[i][j] == value1) {
                    value1Row = i
                    value1Col = j
                } else if (arr[i][j] == value2) {
                    value2Row = i
                    value2Col = j
                }
            }
        }
        Log.d("Value1Row Value1Col", "$value1Row : $value1Col")
        Log.d("value2Row value2Col", "$value2Row : $value2Col")

        // Determine the relative position based on the indices
        if (value1Row == value2Row) {
            if (value1Col == value2Col - 1) {
                Log.d("RiGHT", RelativePosition.RIGHT.toString())
                return RelativePosition.RIGHT

            } else if (value1Col == value2Col + 1) {
                Log.d("RiGHT", RelativePosition.LEFT.toString())
                return RelativePosition.LEFT
            }
        } else if (value1Col == value2Col) {
            if (value1Row == value2Row - 1) {
                Log.d("RiGHT", RelativePosition.BELOW.toString())
                return RelativePosition.BELOW
            } else if (value1Row == value2Row + 1) {
                Log.d("RiGHT", RelativePosition.ABOVE.toString())
                return RelativePosition.ABOVE
            }
        }

        return RelativePosition.NONE
    }

    fun isArrayEqual(array: Array<Array<Int>>): Boolean {

        val targetArray = arrayOf(
            arrayOf(1, 2, 3),
            arrayOf(4, 5, 6),
            arrayOf(7, 8, -1)
        )
        if (array.size != targetArray.size || array[0].size != targetArray[0].size) {
            return false
        }

        for (i in array.indices) {
            for (j in array[0].indices) {
                if (array[i][j] != targetArray[i][j]) {
                    return false
                }
            }
        }

        return true
    }



    fun onPause() {

        // Release all the MediaPlayer instances
        for (mediaPlayer in mediaPlayerList.values) {
            mediaPlayer.pause()
        }

        // Clear the mediaPlayerList to avoid any confusion or potential issues
        //mediaPlayerList.clear()
    }

    fun onResume()
    {
        //On resume, check if any of them paused with no reason

    }

    fun onDestroy()
    {
        if (::cameraSource.isInitialized) {
            cameraSource.stop()
        }


    }

    fun onGameCompleted(points: Int) {
        Log.d("POINTS", points.toString())
        this.points += points
        this.pointsTextView.text = "Points: " + this.points
        soundeffectsPlayerList[0].start()

        this.webViewLayoutC.visibility = View.GONE
    }


}

object Const {
    const val SWIPETHRESHOLD: Int = 50

    const val REQUEST_CAMERA_PERMISSION = 200
    const val CAMERA_REQUEST = 101
    const val TAG = "API123"
    const val SAVED_INSTANCE_URI = "uri"
    const val SAVED_INSTANCE_RESULT = "result"
}

object HandlerManager {
    private data class DelayedRunnable(val handler: Handler, val runnable: Runnable)

    private val subscribedHandlers = mutableListOf<DelayedRunnable>()
    private val pausedHandlers = mutableSetOf<Handler>()

    fun subscribeHandler(handler: Handler, runnable: Runnable) {
        subscribeHandler(handler, runnable, null)
    }

    fun subscribeHandler(handler: Handler, runnable: Runnable, mediaPlayer: MediaPlayer?) {
        subscribedHandlers.add(DelayedRunnable(handler, runnable))
        mediaPlayer?.let {
            pausedHandlers.add(handler)
            it.pause()
        }
    }

    fun unsubscribeHandler(handler: Handler) {
        subscribedHandlers.removeAll { it.handler == handler }
        pausedHandlers.remove(handler)
    }

    internal fun pauseHandler(handler: Handler) {
        val delayedRunnable = subscribedHandlers.find { it.handler == handler }
        if (delayedRunnable != null) {
            pausedHandlers.add(handler)
            handler.removeCallbacks(delayedRunnable.runnable)
        }
    }

    internal fun resumeHandler(handler: Handler, mediaPlayer: MediaPlayer?) {
        val delayedRunnable = subscribedHandlers.find { it.handler == handler }
        if (delayedRunnable != null && pausedHandlers.contains(handler)) {
            pausedHandlers.remove(handler)

            Log.d("TIMER",
                mediaPlayer?.duration?.toLong()?.minus(mediaPlayer.currentPosition).toString()
            )
            val remainingDuration = mediaPlayer?.duration?.toLong()?.minus(mediaPlayer.currentPosition) ?: 4000
            handler.postDelayed(delayedRunnable.runnable, remainingDuration)
            mediaPlayer?.start()


        }
    }


    fun pauseAllHandlers() {
        for (delayedRunnable in subscribedHandlers) {
            pauseHandler(delayedRunnable.handler)
        }
    }

    fun resumeAllHandlers() {
        for (delayedRunnable in subscribedHandlers) {
            resumeHandler(delayedRunnable.handler, null)
        }
    }
}
