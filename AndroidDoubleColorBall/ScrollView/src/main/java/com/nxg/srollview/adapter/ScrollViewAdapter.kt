package com.nxg.srollview.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import com.nxg.srollview.R
import com.nxg.srollview.bean.TextBean

/**
 * 纯文字适配器
 */
class ScrollViewTextAdapter(
    private val context: Context,
    @LayoutRes var resource: Int = R.layout.scroll_view_item_text,
    private val dataList: List<TextBean>
) :
    RecyclerView.Adapter<ScrollViewTextAdapter.ScrollViewTextViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScrollViewTextViewHolder {
        return ScrollViewTextViewHolder(
            LayoutInflater.from(context).inflate(resource, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ScrollViewTextViewHolder, position: Int) {
        holder.textView.text = dataList[position].text
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    class ScrollViewTextViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.findViewById(R.id.text_number)
    }
}