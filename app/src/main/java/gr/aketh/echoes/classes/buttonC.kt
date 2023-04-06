package gr.aketh.echoes.classes

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.appcompat.widget.AppCompatImageButton

class CustomButton : AppCompatImageButton {

    constructor(context: Context) : super(context) {}
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {}
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {}

    override fun onTouchEvent(event: MotionEvent): Boolean {
        super.onTouchEvent(event)
        return false
    }

    override fun performClick(): Boolean {
        super.performClick()
        // doSomething()
        return true
    }

    fun doSomething() {
        // Toast.makeText(context, "did something", Toast.LENGTH_SHORT).show()
    }
}
