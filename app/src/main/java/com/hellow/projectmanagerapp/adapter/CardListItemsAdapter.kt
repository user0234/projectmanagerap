package com.hellow.projectmanagerapp.adapter

import android.content.Context
import android.graphics.Color
 import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hellow.projectmanagerapp.activities.TaskListActivity
import com.hellow.projectmanagerapp.databinding.ItemCardBinding
 import com.hellow.projectmanagerapp.model.Card
import com.hellow.projectmanagerapp.model.SelectedMembers

open class CardListItemsAdapter(
    private val context: Context,
    private var list: ArrayList<Card>
) : RecyclerView.Adapter<CardListItemsAdapter.MyViewHolder>() {

    private var onClickListener: OnClickListener? = null

    class MyViewHolder(itemBinding: ItemCardBinding) : RecyclerView.ViewHolder(itemBinding.root) {
        val binding = itemBinding
    }

    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    interface OnClickListener {
        fun onClick(cardPosition: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemCardBinding.inflate(LayoutInflater.from(parent.context),parent,false)

       return MyViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }


    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val model = list[position]

        if (model.labelColor.isNotEmpty()) {
            holder.binding.viewLabelColor.visibility = View.VISIBLE
            holder.binding.viewLabelColor.setBackgroundColor(Color.parseColor(model.labelColor))
        } else {
            holder.binding.viewLabelColor.visibility = View.GONE
        }

        holder.binding.tvCardName.text = model.name

        if ((context as TaskListActivity).mAssignedMembersDetailList.size > 0) {
            val selectedMembersList: ArrayList<SelectedMembers> = ArrayList()

            for (i in context.mAssignedMembersDetailList.indices) {
                for (j in model.assignedTo) {
                    if (context.mAssignedMembersDetailList[i].id == j) {
                        val selectedMember = SelectedMembers(
                            context.mAssignedMembersDetailList[i].id,
                            context.mAssignedMembersDetailList[i].image
                        )

                        selectedMembersList.add(selectedMember)
                    }
                }
            }

            if (selectedMembersList.size > 0) {

                if (selectedMembersList.size == 1 && selectedMembersList[0].id == model.createdBy) {
                    holder.binding.rvCardSelectedMembersList.visibility = View.GONE
                } else {
                    holder.binding.rvCardSelectedMembersList.visibility = View.VISIBLE

                    holder.binding.rvCardSelectedMembersList.layoutManager =
                        GridLayoutManager(context, 4)
                    val adapter = CardMemberListItemsAdapter(context, selectedMembersList, false)
                    holder.binding.rvCardSelectedMembersList.adapter = adapter
                    adapter.setOnClickListener(object :
                        CardMemberListItemsAdapter.OnClickListener {
                        override fun onClick() {
                            if (onClickListener != null) {
                                onClickListener!!.onClick(position)
                            }
                        }
                    })
                }
            } else {
                holder.binding.rvCardSelectedMembersList.visibility = View.GONE
            }
        }

        holder.itemView.setOnClickListener {
            if (onClickListener != null) {
                onClickListener!!.onClick(position)
            }
        }
    }

}