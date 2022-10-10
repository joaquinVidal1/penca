package com.example.penca.mainscreen

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.NumberPicker
import android.widget.Toast
import androidx.core.widget.NestedScrollView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.penca.R
import com.example.penca.databinding.FragmentMainScreenBinding
import com.example.penca.domain.entities.Bet
import com.example.penca.domain.entities.ScreenItem
import com.example.penca.mainscreen.carrousel.FeatureCarrouselFragment
import com.example.penca.mainscreen.carrousel.ZoomOutPageTransformer
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainScreenFragment : Fragment() {
    private val viewModel: MainScreenViewModel by viewModels()
    private lateinit var binding: FragmentMainScreenBinding
    private lateinit var adapter: MainScreenAdapter
    private lateinit var layoutManager: LinearLayoutManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_main_screen, container, false
        )
        viewModel.refreshMatches()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mainScreenList = binding.recyclerList
        val nestedScrollView = binding.nestedScrollView


        setObservers()
        setAdapter()
        setSearchItem()
        //setScrollListener(mainScreenList)

        layoutManager = LinearLayoutManager(activity)
        mainScreenList.adapter = adapter
        mainScreenList.layoutManager = layoutManager
        binding.filterButton.setOnClickListener { setFilterDialog(); viewModel.refreshMatches() }

        nestedScrollView.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            if (!nestedScrollView.canScrollVertically(1)) {
                if ((viewModel.noMoreBets.value == null) || viewModel.noMoreBets.value == false)
                viewModel.loadMoreBets()
            }
        })
        setCarrousel()
    }

    private fun setScrollListener(mainScreenList: RecyclerView) {
        mainScreenList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (!recyclerView.canScrollVertically(1)) {
                    viewModel.loadMoreBets()
                }
            }
        })
    }

    private fun setCarrousel() {
        val carrousel = binding.carrousel
        val carrouselAdapter = BannerSlidePagerAdapter(requireActivity())
        carrousel.adapter = carrouselAdapter
        binding.viewPageIndicator.setUpWithViewPager2(binding.carrousel)
        carrousel.setPageTransformer(ZoomOutPageTransformer())
    }

    private fun setFilterDialog() {
        val dialog = viewModel.filter.value?.let {
            FilterDialog(it) { filter: BetFilter ->
                viewModel.onFilterChanged(filter)
            }
        }
        dialog!!.show(childFragmentManager, "Filter dialog")

    }

    private fun setSearchItem() {
        val itemSearch = binding.itemSearch
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

    private fun setAdapter() {
        adapter =
            MainScreenAdapter(requireContext(),
                { bet -> setNumberPickerForBetGoals(bet, TeamKind.Home) },
                { bet -> setNumberPickerForBetGoals(bet, TeamKind.Away) },
                { bet ->
                    this.findNavController().navigate(
                        MainScreenFragmentDirections.actionFragmentMainScreenToSeeDetailsFragment2(
                            bet.match.id
                        )
                    )
                })
    }


    private fun setObservers() {
        viewModel.bets.observe(viewLifecycleOwner) {
            val screenList = mutableListOf<ScreenItem>()
            if (it == null || it.isEmpty()) {
                binding.nothingFoundText.visibility = View.VISIBLE
                binding.recyclerList.visibility = View.INVISIBLE
            } else {
                binding.nothingFoundText.visibility = View.GONE
                binding.recyclerList.visibility = View.VISIBLE
                screenList.addAll(it)
            }
            adapter.submitList(screenList)
        }

        viewModel.loadingContents.observe(viewLifecycleOwner) {
            if (it) {
                binding.recyclerList.visibility = View.INVISIBLE
                binding.contentLoading.visibility = View.VISIBLE
            } else {
                binding.recyclerList.visibility = View.VISIBLE
                binding.contentLoading.visibility = View.GONE
            }
        }

        viewModel.loadingMoreBets.observe(viewLifecycleOwner){
            if (it) {
                binding.loadingMoreBets.visibility = View.VISIBLE
            } else {
                binding.loadingMoreBets.visibility = View.GONE
            }
        }

    }

    private fun setNumberPickerForBetGoals(bet: Bet, teamKind: TeamKind) {
        val builder = AlertDialog.Builder(context)
        val numberPicker = NumberPicker(context)
        numberPicker.minValue = 0
        numberPicker.maxValue = 9
        numberPicker.wrapSelectorWheel = true
        numberPicker.value = if (teamKind == TeamKind.Home) {
            bet.homeGoalsBet ?: 0
        } else {
            bet.awayGoalsBet ?: 0
        }
        builder.setPositiveButton(getString(R.string.confirm)) { _, _ ->
            viewModel.betScoreChanged(bet, numberPicker.value, teamKind)
        }
        builder.setNegativeButton(getString(R.string.cancel))
        { _, _ -> }
        builder.setTitle(getString(R.string.dialog_title))
        builder.setView(numberPicker)
        builder.show()
    }

    private fun hideKeyboard() {
        val manager =
            activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        manager.hideSoftInputFromWindow(view?.windowToken, 0)
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