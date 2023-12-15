package gr.aketh.echoes

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import gr.aketh.echoes.databinding.ActivityGameTemplateBinding
import gr.aketh.echoes.databinding.FragmentGameDescriptionBinding
import org.json.JSONObject

class GameInfoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_info)

        val gameDescFragment = GameDescFragment()

        val jsonString = intent.getStringExtra("jsonFile")
        val bundle = Bundle()
        bundle.putString("jsonFile", jsonString)
        gameDescFragment.arguments = bundle


        supportFragmentManager.beginTransaction().replace(R.id.activity_game_info_fragment, gameDescFragment).commit()
    }
}