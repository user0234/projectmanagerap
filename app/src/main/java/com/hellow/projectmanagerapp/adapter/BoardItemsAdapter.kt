package com.hellow.projectmanagerapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hellow.projectmanagerapp.R
import com.hellow.projectmanagerapp.databinding.ItemBoardBinding
import com.hellow.projectmanagerapp.model.Board

open class BoardItemsAdapter(
    private val context: Context,
    private var list: ArrayList<Board>
) : RecyclerView.Adapter<BoardItemsAdapter.MyViewHolder>() {

    private var onClickListener: OnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

         val binding = ItemBoardBinding.inflate(LayoutInflater.from(parent.context),parent,false)

        return  MyViewHolder(
            binding
        )
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val model = list[position]

        Glide
            .with(context)
            .load(model.image)
            .centerCrop()
            .placeholder(R.drawable.ic_board_place_holder)
            .into(holder.binding.ivBoardImage)

        holder.binding.tvName.text = model.name
        holder.binding.tvCreatedBy.text = "Created By : ${model.createdBy}"

        holder.binding.root.setOnClickListener {

            if (onClickListener != null) {
                onClickListener!!.onClick(position, model)
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    interface OnClickListener {
        fun onClick(position: Int, model: Board)
    }

     class MyViewHolder(itemBinding: ItemBoardBinding) : RecyclerView.ViewHolder(itemBinding.root) {
       val binding:ItemBoardBinding = itemBinding

    }
}
