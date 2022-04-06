package gr.aketh.echoes

import android.content.Intent
import android.os.Bundle
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



        binding.buttonStart.setOnClickListener {
            startActivity()
        }

        return binding.root
        //return super.onCreateView(inflater, container, savedInstanceState)
    }

    private fun startActivity()
    {
        val intent = Intent(activity, GameTemplate::class.java)
        activity?.startActivity(intent)
    }

}