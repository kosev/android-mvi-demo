package net.kosev.mvidemo.ui.trade

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import net.kosev.mvidemo.*
import net.kosev.mvidemo.databinding.FragmentTradeBinding
import net.kosev.mvidemo.ui.EventObserver

@AndroidEntryPoint
class TradeFragment : Fragment() {

    private val viewModel: TradeViewModel by viewModels()
    private lateinit var binding: FragmentTradeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTradeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.settingButton.setOnClickListener {
            viewModel.onEvent(TradeEvent.SettingsClick)
        }
        binding.amountField.editText?.addTextChangedListener { text ->
            viewModel.onEvent(TradeEvent.AmountChange(text.toString()))
        }
        binding.buyButton.setOnClickListener {
            hideKeyboard()
            viewModel.onEvent(TradeEvent.BuyCryptoClick)
        }

        viewModel.state.observe(viewLifecycleOwner) { updateUi(it) }
        viewModel.effect.observe(viewLifecycleOwner, EventObserver { applyEffect(it) })
    }

    override fun onResume() {
        super.onResume()
        viewModel.onEvent(TradeEvent.ScreenLoad)
    }

    private fun updateUi(state: TradeState) =
        when (state) {
            TradeState.Error -> showErrorState()
            TradeState.Loading -> showLoadingState()
            is TradeState.Success -> showSuccessState(state)
        }

    private fun showLoadingState() {
        binding.content.visibility = View.GONE
        binding.loading.visibility = View.VISIBLE
    }

    private fun showSuccessState(state: TradeState.Success) {
        binding.content.visibility = View.VISIBLE
        binding.loading.visibility = View.GONE
        binding.state = state
        binding.amountField.error = state.noBalanceError?.let { getString(it) }
    }

    private fun showErrorState() {
        TODO("Not yet implemented")
    }

    private fun applyEffect(effect: TradeEffect) =
        when (effect) {
            TradeEffect.NavigateToSettings -> navigateToSettings()
            TradeEffect.ShowBuyError -> showBuyErrorDialog()
        }

    private fun navigateToSettings(): Unit =
        findNavController().navigate(R.id.action_calculatorFragment_to_settingsFragment)

    private fun showBuyErrorDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.error_title)
            .setMessage(R.string.error_message)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                viewModel.onEvent(TradeEvent.ScreenLoad)
            }
            .show()
    }

    private fun hideKeyboard() {
        requireActivity().currentFocus?.let { view ->
            val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

}
