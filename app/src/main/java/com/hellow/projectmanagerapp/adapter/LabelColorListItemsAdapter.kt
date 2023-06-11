package com.hellow.projectmanagerapp.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hellow.projectmanagerapp.databinding.ItemLabelColorBinding
import java.util.ArrayList

class LabelColorListItemsAdapter(
    private val context: Context,
    private var list: ArrayList<String>,
    private val mSelectedColor: String
) :  RecyclerView.Adapter<LabelColorListItemsAdapter.MyViewHolder>() {

    var onItemClickListener: OnItemClickListener? = null



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemLabelColorBinding.inflate(LayoutInflater.from(parent.context),parent,false)

        return MyViewHolder(
            binding
        )
    }

    override fun getItemCount(): Int {
       return list.size
    }


    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = list[position]

        holder.binding.viewMain.setBackgroundColor(Color.parseColor(item))

        if (item == mSelectedColor) {
            holder.binding.ivSelectedColor.visibility = View.VISIBLE
        } else {
            holder.binding.ivSelectedColor.visibility = View.GONE
        }

        holder.itemView.setOnClickListener {

            if (onItemClickListener != null) {
                onItemClickListener!!.onClick(position, item)
            }
        }
    }

    class MyViewHolder(itemBinding: ItemLabelColorBinding) : RecyclerView.ViewHolder(itemBinding.root) {
        val binding = itemBinding
    }

    interface OnItemClickListener {

        fun onClick(position: Int, color: String)
    }

}