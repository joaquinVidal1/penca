package com.example.penca.mainscreen

import android.os.Bundle
import android.view.*
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.example.penca.R
import com.example.penca.databinding.CustomDialogBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class FilterDialog(
    private var filterSelected: BetFilter,
    private val onFilterButtonClicked: (BetFilter) -> Unit
) : DialogFragment() {
    private lateinit var binding: CustomDialogBinding
    private lateinit var seeAll: Button
    private lateinit var seeAccerted: Button
    private lateinit var seeWrong: Button
    private lateinit var seePending: Button
    private lateinit var seePlayedWithNoResult: Button
    private lateinit var window: Window

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = CustomDialogBinding.inflate(
            inflater, container, false
        )

        window = dialog?.window!!
        dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog!!.setContentView(R.layout.custom_dialog)
        dialog!!.window!!.decorView.setBackgroundResource(android.R.color.transparent)
        val wlp = window.attributes
        wlp.gravity = Gravity.BOTTOM
        wlp.horizontalMargin = 0F
        wlp.width = ViewGroup.LayoutParams.MATCH_PARENT
        window.attributes = wlp
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        seeAll = binding.seeAllText
        seeAccerted = binding.seeAccertedText
        seeWrong = binding.seeWrongText
        seePending = binding.seePendingText
        seePlayedWithNoResult = binding.seeNotBetText

        val unselectedColor =
            ContextCompat.getColor(context!!, R.color.color_text_dialog_not_selected)
        val selectedColor = ContextCompat.getColor(context!!, R.color.color_text_dialog_selected)
        setFilterSelected(selectedColor, unselectedColor)

        seeAll.setOnClickListener {
            onFilterChanged(
                BetFilter.SeeAll,
                selectedColor,
                unselectedColor
            )
        }
        seeAccerted.setOnClickListener {
            onFilterChanged(
                BetFilter.SeeAccerted,
                selectedColor,
                unselectedColor
            )
        }
        seeWrong.setOnClickListener {
            onFilterChanged(
                BetFilter.SeeMissed,
                selectedColor,
                unselectedColor
            )
        }
        seePending.setOnClickListener {
            onFilterChanged(
                BetFilter.SeePending,
                selectedColor,
                unselectedColor
            )
        }
        seePlayedWithNoResult.setOnClickListener {
            onFilterChanged(
                BetFilter.SeePlayedWithNoResult,
                selectedColor,
                unselectedColor
            )
        }

        binding.filterButton.setOnClickListener {
            onFilterButtonClicked(filterSelected)
            this.dismiss()
        }
    }

    private fun setFilterSelected(selectedColor: Int, unselectedColor: Int) {
        seeAll.setTextColor(unselectedColor)
        seeAccerted.setTextColor(unselectedColor)
        seeWrong.setTextColor(unselectedColor)
        seePending.setTextColor(unselectedColor)
        seePlayedWithNoResult.setTextColor(unselectedColor)
        fromFilterToButton(filterSelected).setTextColor(selectedColor)
    }

    private fun onFilterChanged(filter: BetFilter, selectedColor: Int, unselectedColor: Int) {
        fromFilterToButton(filterSelected).setTextColor(unselectedColor)
        filterSelected = filter
        fromFilterToButton(filter).setTextColor(selectedColor)

    }

    private fun fromFilterToButton(filter: BetFilter): Button {
        return when (filter) {
            BetFilter.SeeAll -> seeAll
            BetFilter.SeeAccerted -> seeAccerted
            BetFilter.SeeMissed -> seeWrong
            BetFilter.SeePending -> seePending
            BetFilter.SeePlayedWithNoResult -> seePlayedWithNoResult
        }
    }

}