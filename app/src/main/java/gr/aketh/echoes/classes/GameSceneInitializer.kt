package gr.aketh.echoes.classes

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
import android.view.View
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import gr.aketh.echoes.GameTemplate
import gr.aketh.echoes.R
import gr.aketh.echoes.classes.Const.SWIPETHRESHOLD
import gr.aketh.echoes.classes.JsonUtilities.jsonArrayToMutableMap
import org.json.JSONArray
import org.json.JSONObject
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

    var isSwipeEnabled = true


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
                    if (circle["running"] as Boolean) {

                        this.mediaPlayerList[circle["sound"]]?.stop()
                    }


                } else if (!(circle["running"] as Boolean)) {

                    this.mediaPlayerList[circle["sound"]]?.start()


                    //Read the quiz layout and display it
                    if (circle["type"] == "quiz" && !(circle["running"] as Boolean)) {
                        Handler(Looper.getMainLooper()).postDelayed({
                            // Your Code
                            this.quizLayout.visibility = View.VISIBLE
                        }, this.mediaPlayerList[circle["sound"]]!!.duration.toLong())



                        //Show layout and do calculation
                        this.showCorrectLayoutWithContent(circle)
                    } else if (circle["type"] == "camera" && !(circle["running"] as Boolean)) {
                        //Show layout and do calculation
                        Handler(Looper.getMainLooper()).postDelayed({
                            // Your Code
                            this.showCorrectLayoutWithContent(circle)
                        }, this.mediaPlayerList[circle["sound"]]!!.duration.toLong())

                    } else if (circle["type"] == "slidingPuzzle") {
                        //Show layout and do calculation
                        Handler(Looper.getMainLooper()).postDelayed({
                            // Your Code
                            this.showCorrectLayoutWithContent(circle)
                        }, this.mediaPlayerList[circle["sound"]]!!.duration.toLong())
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

                var bt = correctLayout.findViewById<TextView>(R.id.correct_tv_desc)
                bt.text = "Τα γράμματα σου είναι: "+ tmpMap["letters"]



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
        }
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
            mediaPlayer.release()
        }

        // Clear the mediaPlayerList to avoid any confusion or potential issues
        mediaPlayerList.clear()
    }


}

object Const {
    const val SWIPETHRESHOLD: Int = 50
}