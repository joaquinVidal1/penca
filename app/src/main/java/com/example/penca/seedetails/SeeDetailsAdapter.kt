package com.example.penca.seedetails

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.penca.R
import com.example.penca.databinding.ListItemDetailsMatchBinding
import com.example.penca.domain.entities.MatchEvent
import com.example.penca.domain.entities.MatchEventKind
import com.example.penca.domain.entities.MatchTeams

class SeeDetailsAdapter(private var eventsList: List<MatchEvent>) :
    RecyclerView.Adapter<ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return MatchEventViewHolder(
            ListItemDetailsMatchBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = eventsList[position]
        when (holder) {
            is MatchEventViewHolder -> holder.bind(item)
        }

    }

    override fun getItemCount(): Int {
        return eventsList.size
    }

    inner class MatchEventViewHolder(private var binding: ListItemDetailsMatchBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(event: MatchEvent) {
            val minutText = event.minute.toString() + "'"
            binding.minutEvent.text = minutText
            binding.eventLogo.setImageResource(
                if (event.kind == MatchEventKind.Goal) {
                    R.drawable.goal_vector
                } else {
                    if (event.kind == MatchEventKind.YellowCard) {
                        R.drawable.yellow_card
                    } else {
                        R.drawable.red_card
                    }
                }
            )
            if (event.team == MatchTeams.Local) {
                binding.nameAwayPlayer.visibility = View.INVISIBLE
                binding.nameHomePlayer.visibility = View.VISIBLE
                binding.nameHomePlayer.text = event.playerName
            } else {
                binding.nameHomePlayer.visibility = View.INVISIBLE
                binding.nameAwayPlayer.visibility = View.VISIBLE
                binding.nameAwayPlayer.text = event.playerName
            }
        }
    }
}