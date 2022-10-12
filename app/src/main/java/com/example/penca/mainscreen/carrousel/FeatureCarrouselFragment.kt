package com.example.penca.mainscreen.carrousel

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
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
            val circularProgressDrawable = CircularProgressDrawable(context!!)
            circularProgressDrawable.strokeWidth = 5f
            circularProgressDrawable.centerRadius = 30f
            circularProgressDrawable.start()

            Log.i("imageUrl", getString(ARG_DRAWABLE_ID).toString())
            Glide.with(binding.bannerImage.context)
                .load("https://" + getString(ARG_DRAWABLE_ID))
                .error(R.drawable.cs_nycl_blankplaceholder)
                .fitCenter()
                .into(binding.bannerImage)
        }

    }

}


