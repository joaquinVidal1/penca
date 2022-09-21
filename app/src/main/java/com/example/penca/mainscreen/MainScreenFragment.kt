package com.example.penca.mainscreen

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.penca.R
import com.example.penca.databinding.FragmentMainScreenBinding
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
        adapter = MainScreenAdapter(requireContext(), requireActivity(), ArrayList(), {}, {})
        binding.recyclerList.adapter = adapter
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mainScreenList = binding.recyclerList
        val carrouselAdapter = BannerSlidePagerAdapter(requireActivity())
        val carrouselFragment = FeatureCarrouselFragment()
        val manager = LinearLayoutManager(activity)
        mainScreenList.layoutManager = manager

        viewModel.bets.observe(viewLifecycleOwner) {
            val screenList = mutableListOf<ScreenItem>(ScreenItem.ScreenCarrousel(carrouselAdapter))
            screenList.addAll(it)
            adapter.screenItems = screenList
            adapter.notifyDataSetChanged()
        }

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