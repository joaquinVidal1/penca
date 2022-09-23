package com.example.penca.mainscreen

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.NumberPicker
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.penca.R
import com.example.penca.databinding.FragmentMainScreenBinding
import com.example.penca.domain.entities.Bet
import com.example.penca.domain.entities.ScreenItem
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainScreenFragment : Fragment() {
    private val viewModel: MainScreenViewModel by viewModels()
    private lateinit var binding: FragmentMainScreenBinding
    private lateinit var adapter: MainScreenAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_main_screen, container, false
        )

        adapter = MainScreenAdapter(requireContext(), ArrayList(),
            { bet -> setDialog(bet, TeamKind.Local) },
            { bet -> setDialog(bet, TeamKind.Away) },
            { bet -> onSeeDetailsClicked(bet)})

        binding.recyclerList.adapter = adapter
        return binding.root
    }

    private fun onSeeDetailsClicked(bet: Bet) {

    }

    private fun setDialog(bet: Bet, teamKind: TeamKind) {
        val builder = AlertDialog.Builder(context)
        val numberPicker = NumberPicker(context)
        numberPicker.minValue = 0
        numberPicker.maxValue = 500
        numberPicker.wrapSelectorWheel = false
        numberPicker.value = if (teamKind == TeamKind.Local) {
            bet.homeGoalsBet ?: 0
        } else {
            bet.awayGoalsBet ?: 0
        }
        builder.setPositiveButton(getString(R.string.confirm)) { _, _ ->
            viewModel.betScoreChanged(bet.match.id, numberPicker.value, teamKind)
            adapter.screenItems = viewModel.bets.value!!
            adapter.notifyDataSetChanged()
            adapter.notifyItemChanged(adapter.screenItems.indexOfFirst {
                ((it is ScreenItem.ScreenBet) && (it.bet.match.id == bet.match.id))
            })
        }
        builder.setNegativeButton(getString(R.string.cancel))
        { _, _ -> }
        builder.setTitle(getString(R.string.dialog_title))
        builder.setView(numberPicker)
        builder.show()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mainScreenList = binding.recyclerList
        val carrouselAdapter = BannerSlidePagerAdapter(requireActivity())
        val manager = LinearLayoutManager(activity)
        val carrousel = binding.carrousel
        val viewPageIndicator = binding.viewPageIndicator
        val itemSearch = binding.itemSearch

        mainScreenList.layoutManager = manager

        carrousel.adapter = carrouselAdapter
        viewPageIndicator.setUpWithViewPager2(binding.carrousel)
        binding.carrousel.setPageTransformer(ZoomOutPageTransformer())

        viewModel.bets.observe(viewLifecycleOwner) {
            val screenList = mutableListOf<ScreenItem>()
            if (it.isEmpty()) {
                screenList.add(ScreenItem.ScreenNothingFound())
            } else {
                screenList.addAll(it)
            }
            adapter.screenItems = screenList
            adapter.notifyDataSetChanged()
        }

        itemSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                viewModel.onQueryChanged(itemSearch.text.toString())
            }

            override fun afterTextChanged(p0: Editable?) {
            }

        })
        itemSearch.setOnKeyListener { _, keyCode, keyEvent -> //If the keyEvent is a key-down event on the "enter" button
            if (keyEvent.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                hideKeyboard()
                true
            } else false
        }

        binding.searchButton.setOnClickListener { hideKeyboard() }

    }

    private fun hideKeyboard() {
        val manager = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        manager.hideSoftInputFromWindow(view?.windowToken, 0)
    }

    override fun onResume() {
        super.onResume()
        viewModel.getAllItems()
    }

}

class BannerSlidePagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        val fragment = FeatureCarrouselFragment()
        fragment.arguments = Bundle().apply {
            putInt(
                FeatureCarrouselFragment.ARG_DRAWABLE_ID, when (position) {
                    0 -> R.drawable.banner_1
                    1 -> R.drawable.banner_2
                    else -> R.drawable.banner_3
                }
            )
        }
        return fragment
    }
}