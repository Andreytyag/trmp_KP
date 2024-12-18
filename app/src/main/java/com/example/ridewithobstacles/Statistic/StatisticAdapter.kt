package com.example.ridewithobstacles.Statistic

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ridewithobstacles.ApiService.DataModels.RecordDto
import com.example.ridewithobstacles.R

class StatisticAdapter(): RecyclerView.Adapter<StatisticAdapter.RecordsViewHolder>() {

    private var records: List<RecordDto> = emptyList()

    class RecordsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tv_name)
        val tvMaxPoints: TextView = itemView.findViewById(R.id.tv_max_points)
        val tvAvgPoints: TextView = itemView.findViewById(R.id.tv_avg_points)
        val tvMaxCoins: TextView = itemView.findViewById(R.id.tv_max_coins)
        val tvAvgCoins: TextView = itemView.findViewById(R.id.tv_avg_coins)
        //val tvGamesPlayed: TextView = itemView.findViewById(R.id.tv_games_played)
    }

    fun setData(newList: List<RecordDto>) {
        records = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): StatisticAdapter.RecordsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.statistic_element, parent, false)
        return RecordsViewHolder(view)
    }

    override fun onBindViewHolder(holder: StatisticAdapter.RecordsViewHolder, position: Int) {
        val record = records[position]
        holder.tvName.text = record.username
        holder.tvMaxPoints.text = record.scoreCollected.toString()
        holder.tvAvgPoints.text = record.avgScore.toString()
        holder.tvMaxCoins.text = record.moneyCollected.toString()
        holder.tvAvgCoins.text = record.avgMoney.toString()
    }

    override fun getItemCount(): Int {
        return records.size
    }
}