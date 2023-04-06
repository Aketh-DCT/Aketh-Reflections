package gr.aketh.echoes.classes

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton


class CustomButton : AppCompatButton {

    public var startX: Float = 0.0F
    public var startY: Float = 0.0F

    constructor(context: Context?) : super(context!!) {}
    constructor(context: Context?, attrs: AttributeSet?) : super(context!!, attrs) {}

    override fun onTouchEvent(event: MotionEvent): Boolean {
        super.onTouchEvent(event)

        return false
    }

    // Because we call this from onTouchEvent, this code will be executed for both
    // normal touch events and for when the system calls this using Accessibility
    override fun performClick(): Boolean {
        super.performClick()
        //doSomething()
        return true
    }

    public fun doSomething() {
        //Toast.makeText(context, "did something", Toast.LENGTH_SHORT).show()
    }
}