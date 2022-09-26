package com.example.penca.seedetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.penca.R
import com.example.penca.databinding.FragmentSeeDetailsBinding
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
        val bet = viewModel.getBetByMatchId(args.matchId)
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_see_details, container, false
        )
        if (bet == null) {
            binding.matchEvents.visibility = View.INVISIBLE
            binding.nothingFoundText.visibility = View.VISIBLE
        } else {
            if (bet.match.events == null) {
                binding.matchEvents.visibility = View.INVISIBLE
                binding.nothingFoundText.visibility = View.VISIBLE
            } else {
                binding.matchEvents.layoutManager = manager
                binding.matchEvents.visibility = View.VISIBLE
                binding.nothingFoundText.visibility = View.INVISIBLE
                adapter = SeeDetailsAdapter(ArrayList())
                binding.matchEvents.adapter = adapter
                (binding.matchEvents.adapter as SeeDetailsAdapter).notifyDataSetChanged()
                adapter.eventsList = bet.match.events
                (binding.matchEvents.adapter as SeeDetailsAdapter).notifyDataSetChanged()
            }
            binding.awayTeamImage.setImageResource(bet.match.awayTeam.image)
            binding.homeTeamImage.setImageResource(bet.match.homeTeam.image)
            binding.awayTeamName.text = bet.match.awayTeam.name
            binding.homeTeamName.text = bet.match.homeTeam.name
            binding.matchResult.text =
                bet.match.goalsLocal.toString() + " - " + bet.match.goalsAway.toString()
        }
        return binding.root
    }
}