package net.kosev.mvidemo.ui.exchange

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
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import net.kosev.mvidemo.R
import net.kosev.mvidemo.databinding.FragmentTradeBinding
import net.kosev.mvidemo.ui.trade.TradeEffect
import net.kosev.mvidemo.ui.trade.TradeEvent
import net.kosev.mvidemo.ui.trade.TradeState
import java.math.BigDecimal

@AndroidEntryPoint
class ExchangeFragment : Fragment() {

    private val viewModel: ExchangeViewModel by viewModels()
    private lateinit var binding: FragmentTradeBinding

    override fun onResume() {
        super.onResume()
        viewModel.setEvent(TradeEvent.ScreenLoad)
    }

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

        initViewListeners()

        lifecycleScope.launchWhenStarted {
            viewModel.state.collect { updateUi(it) }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.effect.collect { applyEffect(it) }
        }
    }

    private fun initViewListeners() {
        binding.apply {
            settingButton.setOnClickListener {
                viewModel.setEvent(TradeEvent.SettingsClick)
            }
            amountField.editText?.addTextChangedListener { text ->
                viewModel.setEvent(TradeEvent.AmountChange(text.toString()))
            }
            buyButton.setOnClickListener {
                hideKeyboard()
                viewModel.setEvent(TradeEvent.BuyCryptoClick)
            }
        }
    }

    private fun updateUi(state: TradeState) =
        when (state) {
            TradeState.Error -> showErrorState()
            TradeState.Loading -> showLoadingState()
            is TradeState.Success -> showSuccessState(state)
        }

    private fun showLoadingState() {
        binding.apply {
            content.visibility = View.GONE
            loading.visibility = View.VISIBLE
            error.visibility = View.GONE
        }
    }

    private fun showSuccessState(state: TradeState.Success) {
        binding.apply {
            content.visibility = View.VISIBLE
            loading.visibility = View.GONE
            error.visibility = View.GONE
            this.state = state
            amountField.error = state.noBalanceError?.let { getString(it) }
            if (amountIsZero(state)) {
                amountField.editText?.text?.clear()
            }
        }
    }

    private fun amountIsZero(state: TradeState.Success): Boolean =
        state.amount.compareTo(BigDecimal.ZERO) == 0

    private fun showErrorState() {
        binding.apply {
            content.visibility = View.GONE
            loading.visibility = View.GONE
            error.visibility = View.VISIBLE
        }
    }

    private fun applyEffect(effect: TradeEffect) =
        when (effect) {
            TradeEffect.NavigateToSettings -> navigateToSettings()
            TradeEffect.ShowBuyError -> showBuyErrorDialog()
        }

    private fun navigateToSettings(): Unit =
        findNavController().navigate(R.id.action_exchangeFragment_to_settingsFragment)

    private fun showBuyErrorDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.error_title)
            .setMessage(R.string.error_message)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                viewModel.setEvent(TradeEvent.ScreenLoad)
            }
            .show()
    }

    private fun hideKeyboard() {
        requireActivity().currentFocus?.let { view ->
            val imm =
                requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

}
