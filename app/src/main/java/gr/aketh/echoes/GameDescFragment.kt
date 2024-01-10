package gr.aketh.echoes

import android.app.ProgressDialog
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import gr.aketh.echoes.databinding.FragmentGameDescriptionBinding


class GameDescFragment : Fragment() {
    private lateinit var binding: FragmentGameDescriptionBinding
    private var sound: MediaPlayer? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view: View = inflater.inflate(R.layout.fragment_game_description,container,false)
        binding = FragmentGameDescriptionBinding.bind(view)


        val jsonFile = arguments?.getStringArray("jsonFile")

        //Toast.makeText(context, jsonFile, Toast.LENGTH_SHORT).show()



        //Starts activity when you click the button
        binding.buttonStart.setOnClickListener {



            val intent= Intent(context, GameTemplate::class.java)
            intent.putExtra("jsonFile", jsonFile?.get(0))

            startActivity(intent)

        }

        val soundBytes = mapOf(
            "eng_intro" to R.raw.eng_intro,
            "en_ro_1_palace_of_culture" to R.raw.en_ro_1_palace_of_culture,
            "italian_english_intro" to R.raw.intro_it_en_01
        )


        sound = soundBytes[jsonFile?.get(4)]?.let { MediaPlayer.create(this.context, it) }

        sound?.start()

        //Add scrollability to the textTest2
        var stringResource = resources.getString(R.string.default_gamedesc_text)
        var introText = jsonFile?.get(1) + "\n\n" + stringResource
        binding.introTextFragmentGameDescription.text = introText
        binding.introTextFragmentGameDescription.movementMethod = ScrollingMovementMethod()


        binding.textTitleFragmentGameDescription.text = jsonFile?.get(3)
        jsonFile?.get(2)?.let { binding.imageFragmentGameDescription.setImageResource(it.toInt()) }





        return binding.root
        //return super.onCreateView(inflater, container, savedInstanceState)
    }

    private fun startActivity()
    {
        val intent = Intent(activity, GameTemplate::class.java)
        activity?.startActivity(intent)


    }

    override fun onPause() {
        super.onPause()
        sound?.stop()

    }

    override fun onDestroy() {
        super.onDestroy()
        sound?.stop()
        sound?.release()
    }

}