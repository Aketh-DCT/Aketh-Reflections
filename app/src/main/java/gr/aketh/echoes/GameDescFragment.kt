package gr.aketh.echoes

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


        val jsonFile = arguments?.getString("jsonFile")

        Toast.makeText(context, jsonFile, Toast.LENGTH_SHORT).show()



        //Starts activity when you click the button
        binding.buttonStart.setOnClickListener {
            val intent= Intent(context, GameTemplate::class.java)
            intent.putExtra("jsonFile", jsonFile)

            startActivity(intent)

        }


        sound = MediaPlayer.create(this.context, R.raw.eng_intro)

        sound?.start()

        //Add scrollability to the textTest2
        binding.textTest2.movementMethod = ScrollingMovementMethod()





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