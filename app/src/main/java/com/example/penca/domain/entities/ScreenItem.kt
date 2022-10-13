package com.example.penca.domain.entities

const val ITEM_VIEW_TYPE_HEADER_DATE = -1
const val ITEM_VIEW_TYPE_BET = 0

sealed class ScreenItem(val id: Int) {

    // TODO una posibilidad por lo que DiffUtil no esta funcionando es porque ningun elemento tiene un Id unico
    //  todos los ScreenBet tienen ID 0, y todos los ScreenHeader tienen ID -1
    //  aca hay un tema conceptual sobre el uso de los ids, como están ahora no cumplen ninguna función

    data class ScreenBet(val bet: Bet) : ScreenItem(ITEM_VIEW_TYPE_BET)

    data class ScreenHeader(val header: Header) : ScreenItem(
        ITEM_VIEW_TYPE_HEADER_DATE
    )
}
