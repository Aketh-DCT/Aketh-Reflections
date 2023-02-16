package gr.aketh.echoes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

class BatteryFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        //Set the code for Battery fragment




        return inflater.inflate(R.layout.fragment_battery,container,false)
        //return super.onCreateView(inflater, container, savedInstanceState)
    }

}