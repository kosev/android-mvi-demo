package net.kosev.mvidemo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import net.kosev.mvidemo.databinding.FragmentCalculatorBinding

class CalculatorFragment : Fragment() {

    private lateinit var binding: FragmentCalculatorBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCalculatorBinding.inflate(inflater, container, false)

        binding.settingButton.setOnClickListener {
            findNavController().navigate(R.id.action_calculatorFragment_to_settingsFragment)
        }

        return binding.root
    }

}
