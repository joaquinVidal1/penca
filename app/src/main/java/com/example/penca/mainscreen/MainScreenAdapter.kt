package com.example.penca.mainscreen

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.penca.R
import com.example.penca.databinding.*
import com.example.penca.domain.entities.*
import java.lang.ClassCastException


class MainScreenAdapter(
    val context: Context,
    val fragmentActivity: FragmentActivity,
    var screenItems: List<ScreenItem>,
    private val onModifyLocalListener: (Bet) -> Unit,
    private val onModifyAwayListener: (Bet) -> Unit,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM_VIEW_TYPE_BET -> BetViewHolder(
                ListItemBetBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            ITEM_VIEW_TYPE_CARROUSEL -> ScreenCarrouselViewHolder(
                ListItemCarrouselBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            ITEM_VIEW_TYPE_HEADER_DATE -> HeaderViewHolder(
                ListItemHeaderMainScreenBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            else -> throw ClassCastException("Unknow viewType $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = screenItems[position]
        when (holder) {
            is BetViewHolder -> {
                holder.bind(
                    (item as ScreenItem.ScreenBet).bet, onModifyLocalListener, onModifyAwayListener
                )
            }
            is HeaderViewHolder -> {
                holder.bind(item as ScreenItem.ScreenHeader)
            }
            is ScreenCarrouselViewHolder -> {
                holder.bind(
                    item as ScreenItem.ScreenCarrousel
                )
            }
        }
    }

    override fun getItemCount(): Int {
        return screenItems.size
    }

    override fun getItemViewType(position: Int): Int {
        return when (screenItems[position]) {
            is ScreenItem.ScreenCarrousel -> ITEM_VIEW_TYPE_CARROUSEL
            is ScreenItem.ScreenHeader -> ITEM_VIEW_TYPE_HEADER_DATE
            is ScreenItem.ScreenBet -> ITEM_VIEW_TYPE_BET
        }
    }

    inner class BetViewHolder(private var binding: ListItemBetBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val statusText = binding.matchStatusText
        fun bind(
            bet: Bet,
            onEditLocalResult: (Bet) -> Unit,
            onEditAwayResult: (Bet) -> Unit
        ) {
            binding.matchStatusText.text = bet.match.status.toString()
            binding.imageLocalTeam.setImageResource(bet.match.homeTeam.image)
            binding.nameLocalTeam.text = bet.match.homeTeam.name
            binding.imageAwayTeam.setImageResource(bet.match.awayTeam.image)
            binding.nameAwayTeam.text = bet.match.awayTeam.name
            if (bet.match.status == MatchStatus.Pending) {
                bindPendingMatch(onEditLocalResult, onEditAwayResult, bet)
            } else {
                binding.finalScore.visibility = View.VISIBLE
                binding.finalScore.text =
                    bet.match.goalsLocal.toString() + " - " + bet.match.goalsAway.toString()
                if (bet.status == BetStatus.Done) {
                    binding.awayTeamScoreBet.text = bet.awayGoalsBet.toString()
                    binding.localTeamScoreBet.text = bet.homeGoalsBet.toString()
                    if (bet.result == BetResult.Wrong) {
                        bindPlayedMatchWrongBet()
                    } else {
                        bindPlayedMatchCorrectBet()
                    }
                } else {
                    binding.awayTeamScoreBet.text = ""
                    binding.localTeamScoreBet.text = ""
                    bindPlayedMatchWithoutBet()
                }
                View.VISIBLE
            }
        }

        private fun bindPlayedMatchWithoutBet() {
            statusText.text = context.getString(R.string.played_with_no_result)
            statusText.setBackgroundColor(
                ContextCompat.getColor(
                    context,
                    R.color.color_background_bet_status_not_done
                )
            )
            binding.headerBet.background = ContextCompat.getDrawable(
                context,
                R.drawable.background_list_item_bet_header_not_done
            )
        }

        private fun bindPlayedMatchCorrectBet() {
            statusText.text = context.getString(R.string.accerted)
            statusText.setBackgroundColor(
                ContextCompat.getColor(
                    context,
                    R.color.color_background_bet_status_accerted
                )
            )
            binding.headerBet.background =
                ContextCompat.getDrawable(
                    context,
                    R.drawable.background_list_item_bet_header_correct
                )
        }

        private fun bindPlayedMatchWrongBet() {
            statusText.text = context.getString(R.string.missed)
            statusText.setBackgroundColor(
                ContextCompat.getColor(
                    context,
                    R.color.color_background_bet_status_missed
                )
            )
            binding.headerBet.background =
                ContextCompat.getDrawable(
                    context,
                    R.drawable.background_list_item_bet_header_wrong
                )


        }

        private fun bindPendingMatch(onEditLocalResult: (Bet) -> Unit,
                                     onEditAwayResult: (Bet) -> Unit, bet: Bet ) {
            binding.headerBet.background =
                ContextCompat.getDrawable(
                    context,
                    R.drawable.background_list_item_bet_header_pending

            )
            statusText.setBackgroundColor(
                ContextCompat.getColor(
                    context,
                    R.color.color_background_bet_status_pending
                )
            )
            binding.finalScore.visibility = View.INVISIBLE
            binding.localTeamScoreBet.setOnClickListener { onEditLocalResult(bet) }
            binding.awayTeamScoreBet.setOnClickListener { onEditAwayResult(bet) }
            binding.seeDetailsButton.visibility = View.GONE
        }
    }

    inner class HeaderViewHolder(private var binding: ListItemHeaderMainScreenBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(header: ScreenItem.ScreenHeader) {
            binding.headerTextDate.text = header.header.text
        }
    }

    inner class ScreenCarrouselViewHolder(private var binding: ListItemCarrouselBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(carrousel: ScreenItem.ScreenCarrousel) {
            binding.carrousel.adapter = carrousel.adapter
            binding.viewPageIndicator.setUpWithViewPager2(binding.carrousel)
            binding.carrousel.setPageTransformer(ZoomOutPageTransformer())
        }
    }

}
