package gr.aketh.echoes

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import gr.aketh.echoes.databinding.FragmentGameDescriptionBinding


class GameDescFragment : Fragment() {
    private lateinit var binding: FragmentGameDescriptionBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view: View = inflater.inflate(R.layout.fragment_game_description,container,false)
        binding = FragmentGameDescriptionBinding.bind(view)


        //Starts activity when you click the button
        binding.buttonStart.setOnClickListener {
            startActivity()
        }


        var sound: MediaPlayer = MediaPlayer.create(this.context, R.raw.test_intro)

        sound.start()

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

}