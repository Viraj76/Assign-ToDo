package com.example.assigntodo

import android.content.Intent
import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.view.isNotEmpty
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.assigntodo.adapter.WorksAdapter
import com.example.assigntodo.databinding.ActivityWorkBinding
import com.example.assigntodo.models.AssignedWork
import com.example.assigntodo.utils.Config
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class WorkActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWorkBinding
    private lateinit var worksAdapter: WorksAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWorkBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Config.showDialog(this)
        prepareRvForWorksAdapter()

        if(binding.rvWorks.isNotEmpty())  {
            binding.tvBlank.visibility = View.GONE
        }
        val empName = intent.getStringExtra("EmpName")
        val empId = intent.getStringExtra("EmpId")

        binding.tvBlank.text = "No any work has been assigned to $empName"
        binding.workTb.apply {
            title = empName
            setSupportActionBar(this)
            setNavigationOnClickListener {
                startActivity(Intent(context,BossMainActivity::class.java))
            }
        }
        showingAssignedWork(empId)
        binding.fabAssignWork.setOnClickListener {
//            binding.tvBlank.visibility = View.
            val intent =Intent(this,AssignActivity::class.java)
            intent.putExtra("EmpId",empId)
            intent.putExtra("EmpName",empName)
            startActivity(intent)
        }

    }

    private fun prepareRvForWorksAdapter() {
        worksAdapter = WorksAdapter(this)
        binding.rvWorks.apply {
            layoutManager = LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false)
            adapter = worksAdapter
        }
    }

    private fun showingAssignedWork(empId: String?) {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid.toString()
        val  workRoom = currentUserId + empId
        FirebaseDatabase.getInstance().getReference("Works").child(workRoom)
            .addValueEventListener(object : ValueEventListener{
                var works  = arrayListOf<AssignedWork>()
                override fun onDataChange(snapshot: DataSnapshot) {
                    for(work in snapshot.children){
                        val workss = work.getValue(AssignedWork::class.java)
                        works.add(workss!!)
                    }
                    worksAdapter.setWorkList(works)
                    Config.hideDialog()
                }

                override fun onCancelled(error: DatabaseError) {
                    Config.hideDialog()
                    TODO("Not yet implemented")
                }

            })
    }

}