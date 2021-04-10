package net.kosev.mvidemo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import net.kosev.mvidemo.databinding.FragmentCalculatorBinding

@AndroidEntryPoint
class CalculatorFragment : Fragment() {

    private val viewModel: CalculatorViewModel by viewModels()
    private lateinit var binding: FragmentCalculatorBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCalculatorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.viewModel = viewModel

        viewModel.state.observe(viewLifecycleOwner) { updateUi(it) }
        viewModel.effect.observe(viewLifecycleOwner, EventObserver { applyEffect(it) })
    }

    override fun onResume() {
        super.onResume()
        viewModel.onEvent(CalculatorEvent.ScreenLoad)
    }

    private fun updateUi(state: CalculatorState) =
        when (state) {
            CalculatorState.Error -> showErrorState()
            CalculatorState.Loading -> showLoadingState()
            is CalculatorState.Success -> showSuccessState(state)
        }

    private fun showLoadingState() {
        binding.content.visibility = View.GONE
        binding.loading.visibility = View.VISIBLE
    }

    private fun showSuccessState(state: CalculatorState.Success) {
        binding.content.visibility = View.VISIBLE
        binding.loading.visibility = View.GONE
        binding.state = state
    }

    private fun showErrorState() {
        TODO("Not yet implemented")
    }

    private fun applyEffect(effect: CalculatorEffect) =
        when (effect) {
            CalculatorEffect.NavigateToSettings -> navigateToSettings()
        }

    private fun navigateToSettings(): Unit =
        findNavController().navigate(R.id.action_calculatorFragment_to_settingsFragment)

}
