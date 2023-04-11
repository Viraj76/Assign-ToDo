package com.example.assigntodo.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.assigntodo.R
import com.example.assigntodo.databinding.WorkEachItemBinding
import com.example.assigntodo.databinding.WorkItemViewBinding
import com.example.assigntodo.models.AssignedWork

class WorksAdapter(val  context: Context) : RecyclerView.Adapter<WorksAdapter.WorksViewHolder>() {
    private var worksList  = ArrayList<AssignedWork>()
    fun setWorkList(worksList:ArrayList<AssignedWork>){
        this.worksList = worksList
        notifyDataSetChanged()
    }
    class WorksViewHolder(val binding:WorkEachItemBinding):ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorksViewHolder {
        return WorksViewHolder(WorkEachItemBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: WorksViewHolder, position: Int) {
        val work = worksList[position]
        holder.binding.apply {
            cvTitle.text = work.workTitle
//            cvSubtitle.text = work.workDesc
            cvDate.text = work.workLastDate
            workDesc.text = work.workDesc

        }
        when(work.workPriority){
            "1"->
                holder.binding.cvOval.setBackgroundResource(R.drawable.red_oval)
            "2"->
                holder.binding.cvOval.setBackgroundResource(R.drawable.yellow_oval)
            "3"->
                holder.binding.cvOval.setBackgroundResource(R.drawable.green_oval)
        }
        holder.binding.cvOval.setOnClickListener {
            when(work.workPriority){
                "1"->
                    Toast.makeText(context,"Priority : High",Toast.LENGTH_SHORT).show()
                "2"->
                    Toast.makeText(context,"Priority : Medium",Toast.LENGTH_SHORT).show()
                "3"->
                    Toast.makeText(context,"Priority : Low",Toast.LENGTH_SHORT).show()
            }
        }
        val isExpandable: Boolean = work.isExpandable
        holder.binding.workDesc.visibility = if(isExpandable) View.VISIBLE else View.GONE
        holder.binding.workDescT.visibility = if(isExpandable) View.VISIBLE else View.GONE
        holder.binding.constraintLayout.setOnClickListener {
            isAnyItemExpanded(position)
            work.isExpandable = !work.isExpandable
            notifyItemChanged(position,Unit)
        }
    }

    private fun isAnyItemExpanded(position: Int) {
        val temp = worksList.indexOfFirst {
            it.isExpandable
        }
        if(temp >= 0 && temp !=position){
            worksList[temp].isExpandable = false
            notifyItemChanged(temp,0)
        }
    }

    override fun onBindViewHolder(
        holder: WorksViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if(payloads.isNotEmpty() && payloads[0]==0) {
            holder.binding.workDesc.visibility = View.GONE
        }
        else{
            super.onBindViewHolder(holder, position, payloads)
        }
    }

    override fun getItemCount(): Int {
        return worksList.size
    }


}