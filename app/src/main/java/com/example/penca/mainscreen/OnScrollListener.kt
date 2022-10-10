package com.example.penca.mainscreen

import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.penca.domain.entities.ScreenItem
import kotlinx.coroutines.flow.combine

//class OnScrollListener(
////    private val layoutManager: LinearLayoutManager,
////    private val adapter: MainScreenAdapter,
////    private val dataList: MutableList<ScreenItem>,
//    private val viewModel: MainScreenViewModel,
//) : RecyclerView.OnScrollListener() {
//    var previousTotal = 0
//    var loading = true
//    val visibleThreshold = 10
//    var firstVisibleItem = 0
//    var visibleItemCount = 0
//    var totalItemCount = 0
//
//    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
//        super.onScrollStateChanged(recyclerView, newState)
//        if (!recyclerView.canScrollVertically(1)){
//            viewModel.loadMoreBets()
//        }
//    }
//}