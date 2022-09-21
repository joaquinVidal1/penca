package com.example.penca.domain.entities

import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.penca.mainscreen.BannerSlidePagerAdapter
import com.example.penca.mainscreen.FeatureCarrouselFragment

const val ITEM_VIEW_TYPE_HEADER_DATE = -1
const val ITEM_VIEW_TYPE_CARROUSEL = -2
const val ITEM_VIEW_TYPE_BET = 0

sealed class ScreenItem(val id: Int) {

    data class ScreenBet(val bet: Bet) : ScreenItem(bet.match.id) {
    }


    class ScreenCarrousel(val adapter: BannerSlidePagerAdapter) :
        ScreenItem(ITEM_VIEW_TYPE_CARROUSEL) {
    }

    data class ScreenHeader(val header: Header) : ScreenItem(
        ITEM_VIEW_TYPE_HEADER_DATE
    ) {
    }
}
