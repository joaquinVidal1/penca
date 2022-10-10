package com.example.penca.domain.entities

const val ITEM_VIEW_TYPE_HEADER_DATE = -1
const val ITEM_VIEW_TYPE_BET = 0
const val ITEM_VIEW_TYPE_NOTHING_FOUND = -3

sealed class ScreenItem(val id: Int) {

    data class ScreenBet(val bet: Bet) : ScreenItem(ITEM_VIEW_TYPE_BET)

    data class ScreenHeader(val header: Header) : ScreenItem(
        ITEM_VIEW_TYPE_HEADER_DATE
    )
}
