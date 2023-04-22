package com.example.assigntodo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
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
    private  var buttonText= "Unassigned"
    private lateinit var empId : String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWorkBinding.inflate(layoutInflater)
        setContentView(binding.root)


        prepareRvForWorksAdapter()


        val empName = intent.getStringExtra("EmpName")
         empId = intent.getStringExtra("EmpId").toString()


        binding.workTb.apply {
            title = empName
            setSupportActionBar(this)
            setNavigationOnClickListener {
                startActivity(Intent(context,BossMainActivity::class.java))
                finish()
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
        worksAdapter = WorksAdapter(this, ::onButtonClicked,buttonText)
        binding.rvWorks.apply {
            layoutManager = LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false)
            adapter = worksAdapter
        }
    }

    private fun showingAssignedWork(empId: String?) {
        Config.showDialog(this)
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid.toString()
        val  workRoom = currentUserId + empId
        FirebaseDatabase.getInstance().getReference("Works").child(workRoom)
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val works  = ArrayList<AssignedWork>()
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

    private fun onButtonClicked(work : AssignedWork){
        val builder = AlertDialog.Builder(this)
        val alertDialog = builder.create()
        builder.apply {
            setTitle("Unassigned Work")
            setIcon(R.drawable.ic_baseline_cancel_24)
            setMessage("Are you sure you want to unassigned this work?")
            setPositiveButton("Yes") { dialogInterface, which ->
                unassignedWork(work)
            }
            setNegativeButton("No") { dialogInterface, which ->
                alertDialog.dismiss()
            }
            show()
            setCancelable(false)
        }


    }

    private fun unassignedWork(work: AssignedWork) {
        work.isExpandable = !work.isExpandable
        Log.d("vvvv",work.toString())
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid.toString()
        val  workRoom = currentUserId + empId
        FirebaseDatabase.getInstance().getReference("Works").child(workRoom)
            .addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    for(allWorks in snapshot.children){
                        val userWork = allWorks.getValue(AssignedWork::class.java)
                        Log.d("vvvv",work.toString())
                        Log.d("vvvv",userWork.toString())
                        if(userWork == work){
                            allWorks.ref.removeValue()
                                // Notify the adapter that the data set has changed
//                                worksAdapter.notifyDataSetChanged()
//
                            Toast.makeText(this@WorkActivity,"Work Unassigned!",Toast.LENGTH_SHORT).show()
                        }
                    }

                }
                override fun onCancelled(error: DatabaseError) {
                    Config.hideDialog()
                    TODO("Not yet implemented")
                }

            })
    }

}