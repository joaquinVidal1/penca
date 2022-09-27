package com.example.penca.mainscreen

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.penca.R
import com.example.penca.databinding.*
import com.example.penca.domain.entities.*
import java.lang.ClassCastException


class MainScreenAdapter(
    val context: Context,
    var screenItems: List<ScreenItem>,
    private val onModifyLocalListener: (Bet) -> Unit,
    private val onModifyAwayListener: (Bet) -> Unit,
    private val onSeeDetailsClicked: (Bet) -> Unit
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
            ITEM_VIEW_TYPE_HEADER_DATE -> HeaderViewHolder(
                ListItemHeaderMainScreenBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            ITEM_VIEW_TYPE_NOTHING_FOUND -> NothingFoundViewHolder(
                NothingFoundBinding.inflate(
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
            is NothingFoundViewHolder -> {
                holder.bind()
            }
        }
    }

    override fun getItemCount(): Int {
        return screenItems.size
    }

    override fun getItemViewType(position: Int): Int {
        return when (screenItems[position]) {
            is ScreenItem.ScreenHeader -> ITEM_VIEW_TYPE_HEADER_DATE
            is ScreenItem.ScreenBet -> ITEM_VIEW_TYPE_BET
            is ScreenItem.ScreenNothingFound -> ITEM_VIEW_TYPE_NOTHING_FOUND
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
            binding.localTeamScoreBet.text =
                if (bet.homeGoalsBet == null) {
                    "   "
                } else {
                    bet.homeGoalsBet.toString()
                }
            binding.awayTeamScoreBet.text =
                if (bet.awayGoalsBet == null) {
                    "   "
                } else {
                    bet.awayGoalsBet.toString()
                }
            if (bet.match.status == MatchStatus.Pending) {
                bindPendingMatch(onEditLocalResult, onEditAwayResult, bet)
            } else {
                binding.localTeamScoreBet.isClickable = false
                binding.awayTeamScoreBet.isClickable = false
                binding.finalScore.visibility = View.VISIBLE
                binding.userScore.setBackgroundColor(
                    ContextCompat.getColor(
                        context,
                        R.color.color_background_app
                    )
                )
                binding.entireBetBody.background =
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.background_list_item_bet_body_played
                    )
                binding.seeDetailsButton.visibility = View.VISIBLE
                binding.seeDetailsButton.setOnClickListener { onSeeDetailsClicked(bet) }
                val finalScoreText =
                    bet.match.goalsLocal.toString() + " - " + bet.match.goalsAway.toString()
                binding.finalScore.text = finalScoreText
                if (bet.status == BetStatus.Done) {
                    binding.awayTeamScoreBet.text = bet.awayGoalsBet.toString()
                    binding.localTeamScoreBet.text = bet.homeGoalsBet.toString()
                    if (bet.result == BetResult.Wrong) {
                        bindPlayedMatchWrongBet()
                    } else {
                        bindPlayedMatchCorrectBet()
                    }
                } else {
                    bindPlayedMatchWithoutBet()
                }
                View.VISIBLE
            }
        }

        private fun bindPlayedMatchWithoutBet() {
            statusText.text = context.getString(R.string.played_with_no_result)
            binding.awayTeamScoreBet.text = ""
            binding.localTeamScoreBet.text = ""
            statusText.background =
                ContextCompat.getDrawable(
                    context,
                    R.drawable.list_item_status_box_not_done
                )
            binding.headerBet.background = ContextCompat.getDrawable(
                context,
                R.drawable.background_list_item_bet_header_not_done
            )
        }

        private fun bindPlayedMatchCorrectBet() {
            statusText.text = context.getString(R.string.accerted)
            statusText.background =
                ContextCompat.getDrawable(
                    context,
                    R.drawable.list_item_status_box_correct
                )

            binding.headerBet.background =
                ContextCompat.getDrawable(
                    context,
                    R.drawable.background_list_item_bet_header_correct
                )
        }

        private fun bindPlayedMatchWrongBet() {
            statusText.text = context.getString(R.string.missed)
            statusText.background =
                ContextCompat.getDrawable(
                    context,
                    R.drawable.list_item_status_box_incorrect
                )
            binding.headerBet.background =
                ContextCompat.getDrawable(
                    context,
                    R.drawable.background_list_item_bet_header_wrong
                )
        }

        private fun bindPendingMatch(
            onEditLocalResult: (Bet) -> Unit,
            onEditAwayResult: (Bet) -> Unit,
            bet: Bet
        ) {
            binding.headerBet.background =
                ContextCompat.getDrawable(
                    context,
                    R.drawable.background_list_item_bet_header_pending

                )
            statusText.background =
                ContextCompat.getDrawable(
                    context,
                    R.drawable.list_item_status_box_pending
                )

            binding.userScore.background = ContextCompat.getDrawable(
                context,
                R.drawable.background_score
            )
            binding.finalScore.visibility = View.INVISIBLE
            binding.localTeamScoreBet.isClickable = true
            binding.awayTeamScoreBet.isClickable = true
            binding.localTeamScoreBet.setOnClickListener { onEditLocalResult(bet) }
            binding.awayTeamScoreBet.setOnClickListener { onEditAwayResult(bet) }
            binding.seeDetailsButton.visibility = View.GONE
            binding.entireBetBody.background =
                ContextCompat.getDrawable(
                    context,
                    R.drawable.background_list_item_bet_body_pending
                )
        }
    }

    inner class HeaderViewHolder(private var binding: ListItemHeaderMainScreenBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(header: ScreenItem.ScreenHeader) {
            binding.headerTextDate.text = header.header.text
        }
    }

    inner class NothingFoundViewHolder(binding: NothingFoundBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind() {}
    }

}
