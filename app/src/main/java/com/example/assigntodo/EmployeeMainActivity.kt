package com.example.assigntodo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.assigntodo.adapter.WorksAdapter
import com.example.assigntodo.databinding.ActivityMainBinding
import com.example.assigntodo.models.AssignedWork
import com.example.assigntodo.utils.Config
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase

class EmployeeMainActivity : AppCompatActivity() {
    private lateinit var binding:ActivityMainBinding
    private lateinit var worksAdapter: WorksAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prepareRvForMainActivity()
        showingWorksOfEmp()
    }

    private fun showingWorksOfEmp() {
        Config.showDialog(this)
        var getRoom =""
        val currentUser = FirebaseAuth.getInstance().currentUser?.uid
        FirebaseDatabase.getInstance().getReference("Works")
                .addValueEventListener(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for(work  in snapshot.children){
                            if(work.key!!.contains(currentUser!!)){
                                getRoom = work.key!!.toString()
                                FirebaseDatabase.getInstance().getReference("Works").child(getRoom)
                                    .addValueEventListener(object : ValueEventListener{
                                        override fun onDataChange(snapshot: DataSnapshot) {
                                            val empWork = ArrayList<AssignedWork>()
                                            for(works in snapshot.children){
                                                empWork.add(works.getValue(AssignedWork::class.java)!!)
                                                Log.d("ggg",works.getValue(AssignedWork::class.java)!!.toString())
                                            }
                                            worksAdapter.setWorkList(empWork)
                                            Config.hideDialog()
                                        }

                                        override fun onCancelled(error: DatabaseError) {
                                            Config.hideDialog()
                                            Toast.makeText(this@EmployeeMainActivity,error.message,Toast.LENGTH_SHORT).show()
                                        }

                                    })
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Config.hideDialog()
                        Toast.makeText(this@EmployeeMainActivity,error.message,Toast.LENGTH_SHORT).show()
                    }

            })



    }


    private fun prepareRvForMainActivity() {
        worksAdapter = WorksAdapter(this)
        binding.workRV.apply {
            adapter = worksAdapter
        }
    }
}