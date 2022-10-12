package com.example.penca.main

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.penca.R
import com.example.penca.utils.PreferenceHelper
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var preferenceHelper: PreferenceHelper
    val viewModel: MainActivityViewModel by viewModels()

    private lateinit var toolbar: androidx.appcompat.widget.Toolbar
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setUpToolbar()
        modifyToolbar()
        setLogOut()
    }

    private fun setLogOut() {
        viewModel.logOut.observe(this) {
            if (it) {
                supportFragmentManager.popBackStack(
                    R.layout.fragment_log_in,
                    FragmentManager.POP_BACK_STACK_INCLUSIVE
                )
            }
        }
    }

    private fun setUpToolbar() {
        toolbar = findViewById(R.id.topAppBar)
        setSupportActionBar(toolbar)
        navController = findNavController(R.id.nav_host_fragment)
        toolbar.setupWithNavController(navController)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
    }

    private fun modifyToolbar() {
        val pencaText = toolbar.findViewById<TextView>(R.id.pencaText)
        val logoImage = toolbar.findViewById<ImageView>(R.id.logoImage)
        val detailsText = toolbar.findViewById<TextView>(R.id.see_details_text)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            toolbar.title = ""
            if (destination.id == R.id.seeDetailsFragment) {
                toolbar.setNavigationIcon(R.drawable.back_vector)
                pencaText.visibility = View.INVISIBLE
                logoImage.visibility = View.INVISIBLE
                detailsText.visibility = View.VISIBLE
            } else {
                toolbar.navigationIcon = null
                if (destination.id == R.id.fragment_main_screen) {
                    pencaText.visibility = View.VISIBLE
                    logoImage.visibility = View.VISIBLE
                    detailsText.visibility = View.INVISIBLE
                } else {
                    if (destination.id == R.id.logInFragment || destination.id == R.id.registerFragment) {
                        pencaText.visibility = View.VISIBLE
                        logoImage.visibility = View.VISIBLE
                        detailsText.visibility = View.INVISIBLE
                    }
                }
            }
        }
    }
}