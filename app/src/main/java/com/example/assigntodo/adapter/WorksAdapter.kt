package com.example.assigntodo.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.assigntodo.R
import com.example.assigntodo.databinding.WorkItemViewBinding
import com.example.assigntodo.models.AssignedWork

class WorksAdapter : RecyclerView.Adapter<WorksAdapter.WorksViewHolder>() {

    private var worksList  = ArrayList<AssignedWork>()
    fun setWorkList(worksList:ArrayList<AssignedWork>){
        this.worksList = worksList
        notifyDataSetChanged()
    }
    class WorksViewHolder(val binding:WorkItemViewBinding):ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorksViewHolder {
        return WorksViewHolder(WorkItemViewBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: WorksViewHolder, position: Int) {
        val work = worksList[position]
        holder.binding.apply {
            cvTitle.text = work.workTitle
            cvSubtitle.text = work.workDesc
            cvDate.text = work.workLastDate
        }
        when(work.workPriority){
            "1"->
                holder.binding.cvOval.setBackgroundResource(R.drawable.green_oval)
            "2"->
                holder.binding.cvOval.setBackgroundResource(R.drawable.yellow_oval)
            "3"->
                holder.binding.cvOval.setBackgroundResource(R.drawable.red_oval)
        }
    }

    override fun getItemCount(): Int {
        return worksList.size
    }


}