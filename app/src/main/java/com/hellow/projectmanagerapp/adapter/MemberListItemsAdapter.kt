package com.hellow.projectmanagerapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hellow.projectmanagerapp.R
 import com.hellow.projectmanagerapp.databinding.ItemMemberBinding
import com.hellow.projectmanagerapp.model.User
import com.hellow.projectmanagerapp.utils.Constants

class MemberListItemsAdapter(val context: Context, private val list: ArrayList<User>):
    RecyclerView.Adapter<MemberListItemsAdapter.MyViewHolder>() {

    private var onClickListener: OnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemMemberBinding.inflate(LayoutInflater.from(parent.context),parent,false)

        return MemberListItemsAdapter.MyViewHolder(
            binding
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }


    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val model = list[position]



            Glide
                .with(context)
                .load(model.image)
                .centerCrop()
                .placeholder(R.drawable.ic_user_place_holder)
                .into(holder.binding.ivMemberImage)

        holder.binding.tvMemberName.text = model.name
        holder.binding.tvMemberEmail.text = model.email

            if (model.selected) {
                holder.binding.ivSelectedMember.visibility = View.VISIBLE
            } else {
                holder.binding.ivSelectedMember.visibility = View.GONE
            }

            holder.binding.root.setOnClickListener {

                if (onClickListener != null) {
                    if (model.selected) {
                        onClickListener!!.onClick(position, model, Constants.UN_SELECT)
                    } else {
                        onClickListener!!.onClick(position, model, Constants.SELECT)
                    }
                }
            }

    }

    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    interface OnClickListener  {
        fun onClick(position: Int, user: User, action: String)
    }

    class MyViewHolder(val binding: ItemMemberBinding) : RecyclerView.ViewHolder(binding.root)

}