package com.nxg.ssq.ui.home

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.nxg.ssq.R

class NumberAdapter(private val context: Context, private val dataList: List<NumberBean>) :
    RecyclerView.Adapter<NumberAdapter.NumberViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NumberViewHolder {
        return NumberViewHolder(
            LayoutInflater.from(context).inflate(R.layout.item_number, parent, false)
        )
    }

    override fun onBindViewHolder(holder: NumberViewHolder, position: Int) {
        holder.textView.text = dataList[position].number
        holder.textView.setOnClickListener {
            Log.i("HomeFragment", "onBindViewHolder: position $position")
        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    class NumberViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.findViewById(R.id.text_number)
    }
}