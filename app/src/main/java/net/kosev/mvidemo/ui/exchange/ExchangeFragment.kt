package net.kosev.mvidemo.ui.exchange

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import net.kosev.mvidemo.databinding.FragmentTradeBinding
import net.kosev.mvidemo.ui.EventObserver
import net.kosev.mvidemo.ui.trade.TradeEvent
import net.kosev.mvidemo.ui.trade.TradeViewModel

@AndroidEntryPoint
class ExchangeFragment : Fragment() {

    private val viewModel: ExchangeViewModel by viewModels()
    private lateinit var binding: FragmentTradeBinding

    override fun onResume() {
        super.onResume()
        //viewModel.onEvent(TradeEvent.ScreenLoad)
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

        //viewModel.state.observe(viewLifecycleOwner) { updateUi(it) }
        //viewModel.effect.observe(viewLifecycleOwner, EventObserver { applyEffect(it) })
    }

    private fun initViewListeners() {
        binding.apply {
            settingButton.setOnClickListener {
                //viewModel.onEvent(TradeEvent.SettingsClick)
            }
            amountField.editText?.addTextChangedListener { text ->
                //viewModel.onEvent(TradeEvent.AmountChange(text.toString()))
            }
            buyButton.setOnClickListener {
                hideKeyboard()
                //viewModel.onEvent(TradeEvent.BuyCryptoClick)
            }
        }
    }

    private fun hideKeyboard() {
        requireActivity().currentFocus?.let { view ->
            val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

}
