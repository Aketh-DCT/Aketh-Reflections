package gr.aketh.echoes

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class TempInfo: AppCompatActivity()
{

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.temporary_item_view)

        var intent: Intent = intent;
        var title: String? = intent.getStringExtra("Title")
        var desc: String? = intent.getStringExtra("Desc")

        var titleView: TextView = findViewById(R.id.tmp_title)
        var descView: TextView = findViewById(R.id.tmp_desc)
       // var titleView: TextView = findViewById(R.id.tmp_title)
    }
}