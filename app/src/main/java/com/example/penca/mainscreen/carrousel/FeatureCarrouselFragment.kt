package com.example.penca.mainscreen.carrousel

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.penca.R
import com.example.penca.databinding.FragmentFeatureCarrouselPageBinding

class FeatureCarrouselFragment : Fragment() {
    companion object {
        const val ARG_DRAWABLE_ID = "arg_drawable_id"
    }

    lateinit var binding: FragmentFeatureCarrouselPageBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_feature_carrousel_page,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        arguments?.takeIf { it.containsKey(ARG_DRAWABLE_ID) }?.apply {
            binding.bannerImage.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(), getInt(
                        ARG_DRAWABLE_ID
                    )
                )
            )
        }

    }

}


