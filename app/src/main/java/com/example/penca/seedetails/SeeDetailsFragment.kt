package com.example.penca.seedetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.example.penca.R
import com.example.penca.databinding.FragmentSeeDetailsBinding
import com.example.penca.domain.entities.*
import com.example.penca.domain.entities.Header.Companion.getHeaderText
import com.example.penca.network.entities.SeeDetailsBet
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SeeDetailsFragment : Fragment() {
    private val viewModel: SeeDetailsViewModel by viewModels()
    private lateinit var binding: FragmentSeeDetailsBinding
    private lateinit var adapter: SeeDetailsAdapter
    private val manager = LinearLayoutManager(activity)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val args: SeeDetailsFragmentArgs by navArgs()
        viewModel.getBetByMatchId(args.matchId)
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_see_details, container, false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.loadingContents.observe(viewLifecycleOwner) {
            if (it) {
                binding.contentLoading.show()
                cleanScreen()
            } else {
                binding.contentLoading.hide()
                setContents(viewModel.bet)
            }
        }

        viewModel.networkError.observe(viewLifecycleOwner){
            if (it){
                cleanScreen()
                binding.contentLoading.hide()
                binding.nothingFoundText.visibility = View.VISIBLE
            }
        }

    }

    private fun cleanScreen() {
        binding.dateText.visibility = View.GONE
        binding.statusText.visibility = View.GONE
        binding.homeTeamName.text = ""
        binding.awayTeamName.text = ""
        binding.matchResult.text = " - "
        setBetEvents(listOf())
    }

    private fun setContents(bet: SeeDetailsBet) {
        if (bet.events.isEmpty()) {
            binding.matchEvents.visibility = View.INVISIBLE
            binding.nothingFoundText.visibility = View.VISIBLE
        } else {
            setBetEvents(bet.events.map { it.getMatchEventFromNetworkEvent() })
        }

        setLogos(bet.homeTeamLogo, bet.awayTeamLogo, R.drawable.cs_nycl_blankplaceholder)
        binding.awayTeamName.text = bet.awayTeamName
        binding.homeTeamName.text = bet.homeTeamName
        binding.matchResult.text =
            bet.homeTeamGoals.toString() + " - " + bet.awayTeamGoals.toString()
        binding.dateText.text = getHeaderText(bet.getLocalDate())

        val statusText = binding.statusText
        val dateText = binding.dateText
        statusText.visibility = View.VISIBLE
        dateText.visibility = View.VISIBLE
        if (bet.getStatusFromString() == BetStatus.Pending) {
            setPendingBet(statusText, dateText)
        } else {
            if (bet.result == BetResult.Wrong) {
                setWrongBet(statusText, dateText)
            } else {
                setAccertedBet(statusText, dateText)
            }
        }

    }

    private fun setAccertedBet(statusText: TextView, dateText: TextView) {
        statusText.text = context!!.getString(R.string.accerted)
        statusText.background =
            ContextCompat.getDrawable(
                context!!,
                R.drawable.list_item_status_box_correct
            )

        dateText.background =
            ContextCompat.getDrawable(
                context!!,
                R.color.color_background_bet_header_accerted
            )
    }

    private fun setWrongBet(statusText: TextView, dateText: TextView) {
        statusText.text = context!!.getString(R.string.missed)
        statusText.background =
            ContextCompat.getDrawable(
                context!!,
                R.drawable.list_item_status_box_incorrect
            )
        dateText.background =
            ContextCompat.getDrawable(
                context!!,
                R.color.color_background_bet_header_missed
            )
    }

    private fun setPendingBet(statusText: TextView, dateText: TextView) {
        statusText.text = context!!.getString(R.string.played_with_no_result)
        statusText.background =
            ContextCompat.getDrawable(
                context!!,
                R.drawable.list_item_status_box_not_done
            )
        dateText.background =
            ContextCompat.getDrawable(
                context!!,
                R.color.color_background_bet_header_not_done
            )
    }

    private fun setLogos(homeTeamLogo: String, awayTeamLogo: String, onError: Int) {
        val circularProgressDrawable = CircularProgressDrawable(context!!)
        circularProgressDrawable.strokeWidth = 5f
        circularProgressDrawable.centerRadius = 30f
        circularProgressDrawable.start()

        Glide.with(binding.homeTeamImage.context)
            .load(homeTeamLogo)
            .placeholder(circularProgressDrawable)
            .error(onError)
            .fitCenter()
            .into(binding.homeTeamImage)

        Glide.with(binding.awayTeamImage.context)
            .load(awayTeamLogo)
            .placeholder(circularProgressDrawable)
            .error(onError)
            .fitCenter()
            .into(binding.awayTeamImage)
    }

    private fun setBetEvents(events: List<MatchEvent>) {
        binding.matchEvents.layoutManager = manager
        binding.matchEvents.visibility = View.VISIBLE
        binding.nothingFoundText.visibility = View.INVISIBLE
        adapter = SeeDetailsAdapter(events)
        binding.matchEvents.adapter = adapter
        (binding.matchEvents.adapter as SeeDetailsAdapter).notifyDataSetChanged()
    }

}