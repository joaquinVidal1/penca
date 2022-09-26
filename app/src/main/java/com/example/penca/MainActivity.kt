package com.example.penca

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var toolbar: androidx.appcompat.widget.Toolbar
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setUpToolbar()
        modifyToolbar()
    }

    private fun setUpToolbar() {
        toolbar = findViewById(R.id.topAppBar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        navController = findNavController(R.id.nav_host_fragment)
        toolbar.setupWithNavController(
            navController, AppBarConfiguration(
                setOf(
                    R.id.fragment_main_screen,
                    R.id.seeDetailsFragment
                )
            )
        )
    }

    private fun modifyToolbar() {
        val pencaText = toolbar.findViewById<TextView>(R.id.pencaText)
        val logoImage = toolbar.findViewById<ImageView>(R.id.logoImage)
        val backButton = toolbar.findViewById<ImageView>(R.id.backButton)
        val detailsText = toolbar.findViewById<TextView>(R.id.see_details_text)
        navController.addOnDestinationChangedListener { controller, destination, _ ->
            if (destination.id == R.id.seeDetailsFragment) {
                pencaText.visibility = View.INVISIBLE
                logoImage.visibility = View.INVISIBLE
                backButton.visibility = View.VISIBLE
                detailsText.visibility = View.VISIBLE
                backButton.setOnClickListener { controller.popBackStack() }
            } else {
                if (destination.id == R.id.fragment_main_screen) {
                    pencaText.visibility = View.VISIBLE
                    logoImage.visibility = View.VISIBLE
                    backButton.visibility = View.INVISIBLE
                    detailsText.visibility = View.INVISIBLE
                }
            }
        }
    }
}