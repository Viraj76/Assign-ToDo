package com.example.assigntodo.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.assigntodo.WorkActivity
import com.example.assigntodo.databinding.EmpItemViewBinding
import com.example.assigntodo.models.Employees

class BossMainActivityAdapter(val context: Context):RecyclerView.Adapter<BossMainActivityAdapter.EmployeesViewHolder>() {

    private var empList   =  ArrayList<Employees>()
    fun setEmpList(empList : ArrayList<Employees>){
        this.empList = empList
        notifyDataSetChanged()
    }

    class EmployeesViewHolder(val binding: EmpItemViewBinding):ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmployeesViewHolder {
        return EmployeesViewHolder(EmpItemViewBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: EmployeesViewHolder, position: Int) {
        val empData = empList[position]
        holder.binding.apply {
            Glide.with(holder.itemView).load(empData.empImage).into(userImage)
            userName.text = empData.empName
        }
        holder.itemView.setOnClickListener {
            val intent  = Intent(context,WorkActivity::class.java)
            intent.putExtra("EmpName",empData.empName)
            intent.putExtra("EmpId",empData.empId)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return empList.size
    }


}