package com.example.penca.seedetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.penca.R
import com.example.penca.databinding.FragmentSeeDetailsBinding
import com.example.penca.domain.entities.Bet
import com.example.penca.domain.entities.BetResult
import com.example.penca.domain.entities.BetStatus
import com.example.penca.domain.entities.Header.Companion.getHeaderText
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SeeDetailsFragment : Fragment() {
    private val viewModel: SeeDetailsViewModel by viewModels()
    private lateinit var binding: FragmentSeeDetailsBinding
    private lateinit var adapter: SeeDetailsAdapter
    private val manager = LinearLayoutManager(activity)
    private lateinit var bet: Bet

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val args: SeeDetailsFragmentArgs by navArgs()
        bet = viewModel.getBetByMatchId(args.matchId)!!
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_see_details, container, false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (bet.match.events == null) {
            binding.matchEvents.visibility = View.INVISIBLE
            binding.nothingFoundText.visibility = View.VISIBLE
        } else {
            binding.matchEvents.layoutManager = manager
            binding.matchEvents.visibility = View.VISIBLE
            binding.nothingFoundText.visibility = View.INVISIBLE
            adapter = SeeDetailsAdapter(bet.match.events!!)
            binding.matchEvents.adapter = adapter
            (binding.matchEvents.adapter as SeeDetailsAdapter).notifyDataSetChanged()
        }
        binding.awayTeamImage.setImageResource(bet.match.awayTeam.image)
        binding.homeTeamImage.setImageResource(bet.match.homeTeam.image)
        binding.awayTeamName.text = bet.match.awayTeam.name
        binding.homeTeamName.text = bet.match.homeTeam.name
        binding.matchResult.text =
            bet.match.goalsLocal.toString() + " - " + bet.match.goalsAway.toString()
        binding.dateText.text = getHeaderText(bet.match.date)
        val statusText = binding.statusText
        if (bet.status == BetStatus.Pending) {
            statusText.text = context!!.getString(R.string.played_with_no_result)
            statusText.background =
                ContextCompat.getDrawable(
                    context!!,
                    R.drawable.list_item_status_box_not_done
                )
            binding.dateText.background =
                ContextCompat.getDrawable(
                    context!!,
                    R.color.color_background_bet_header_not_done
                )

        } else {
            if (bet.result == BetResult.Wrong) {
                statusText.text = context!!.getString(R.string.missed)
                statusText.background =
                    ContextCompat.getDrawable(
                        context!!,
                        R.drawable.list_item_status_box_incorrect
                    )
                binding.dateText.background =
                    ContextCompat.getDrawable(
                        context!!,
                        R.color.color_background_bet_header_missed
                    )
            } else {
                statusText.text = context!!.getString(R.string.accerted)
                statusText.background =
                    ContextCompat.getDrawable(
                        context!!,
                        R.drawable.list_item_status_box_correct
                    )

                binding.dateText.background =
                    ContextCompat.getDrawable(
                        context!!,
                        R.color.color_background_bet_header_accerted
                    )
            }
        }
    }

}