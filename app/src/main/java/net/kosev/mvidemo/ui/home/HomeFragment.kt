package net.kosev.mvidemo.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import net.kosev.mvidemo.R
import net.kosev.mvidemo.databinding.FragmentHomeBinding
import net.kosev.mvidemo.ui.EventObserver

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private val viewModel: HomeViewModel by viewModels()
    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            liveDataButton.setOnClickListener {
                viewModel.onEvent(HomeEvent.LiveDataDemoClick)
            }
            flowButton.setOnClickListener {
                viewModel.onEvent(HomeEvent.FlowDemoClick)
            }
        }

        viewModel.effect.observe(viewLifecycleOwner, EventObserver { applyEffect(it) })
    }

    private fun applyEffect(effect: HomeEffect): Unit =
        when (effect) {
            HomeEffect.NavigateToFlowDemo ->
                findNavController().navigate(R.id.action_homeFragment_to_exchangeFragment)
            HomeEffect.NavigateToLiveDataDemo ->
                findNavController().navigate(R.id.action_homeFragment_to_tradeFragment)
        }

}
