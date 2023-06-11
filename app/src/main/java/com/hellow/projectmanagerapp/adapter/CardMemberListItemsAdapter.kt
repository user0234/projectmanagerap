package com.hellow.projectmanagerapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hellow.projectmanagerapp.R
import com.hellow.projectmanagerapp.databinding.ItemCardSelectedMemberBinding
import com.hellow.projectmanagerapp.databinding.ItemLabelColorBinding
import com.hellow.projectmanagerapp.model.SelectedMembers

open class CardMemberListItemsAdapter(
    private val context: Context,
    private var list: ArrayList<SelectedMembers>,
    private val assignMembers: Boolean
) : RecyclerView.Adapter<CardMemberListItemsAdapter.MyViewHolder>() {

    private var onClickListener: OnClickListener? = null

    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    interface OnClickListener {
        fun onClick()
    }


    class MyViewHolder(itemBinding: ItemCardSelectedMemberBinding) : RecyclerView.ViewHolder(itemBinding.root) {
        val binding = itemBinding
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemCardSelectedMemberBinding.inflate(LayoutInflater.from(parent.context),parent,false)

        return CardMemberListItemsAdapter.MyViewHolder(
            binding
        )
    }


    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val model = list[position]

        if (holder is MyViewHolder) {

            if (position == list.size - 1 && assignMembers) {
                holder.binding.ivAddMember.visibility = View.VISIBLE
                holder.binding.ivSelectedMemberImage.visibility = View.GONE
            } else {
                holder.binding.ivAddMember.visibility = View.GONE
                holder.binding.ivSelectedMemberImage.visibility = View.VISIBLE

                Glide
                    .with(context)
                    .load(model.image)
                    .centerCrop()
                    .placeholder(R.drawable.ic_user_place_holder)
                    .into(holder.binding.ivSelectedMemberImage)
            }

            holder.binding.root.setOnClickListener {
                if (onClickListener != null) {
                    onClickListener!!.onClick()
                }
            }
        }
    }
}