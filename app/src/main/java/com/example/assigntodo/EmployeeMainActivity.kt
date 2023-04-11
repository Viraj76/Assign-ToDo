package com.example.assigntodo

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.assigntodo.adapter.WorksAdapter
import com.example.assigntodo.auth.SignInActivity
import com.example.assigntodo.databinding.ActivityMainBinding
import com.example.assigntodo.models.AssignedWork
import com.example.assigntodo.utils.Config
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class EmployeeMainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var worksAdapter: WorksAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolBar.apply {
            title = "Works"
            setSupportActionBar(this)
        }

        prepareRvForMainActivity()
        showingWorksOfEmp()
    }

    private fun showingWorksOfEmp() {
        Config.showDialog(this)
        var getRoom = ""
        val currentUser = FirebaseAuth.getInstance().currentUser?.uid
        FirebaseDatabase.getInstance().getReference("Works")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (work in snapshot.children) {
                        if (work.key!!.contains(currentUser!!)) {
                            getRoom = work.key!!.toString()
                            FirebaseDatabase.getInstance().getReference("Works").child(getRoom)
                                .addValueEventListener(object : ValueEventListener {
                                    override fun onDataChange(snapshot: DataSnapshot) {
                                        val empWork = ArrayList<AssignedWork>()
                                        for (works in snapshot.children) {
                                            empWork.add(works.getValue(AssignedWork::class.java)!!)
                                            Log.d(
                                                "ggg",
                                                works.getValue(AssignedWork::class.java)!!
                                                    .toString()
                                            )
                                        }
                                        worksAdapter.setWorkList(empWork)
                                        Config.hideDialog()
                                    }

                                    override fun onCancelled(error: DatabaseError) {
                                        Config.hideDialog()
                                        Toast.makeText(
                                            this@EmployeeMainActivity,
                                            error.message,
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }

                                })
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Config.hideDialog()
                    Toast.makeText(this@EmployeeMainActivity, error.message, Toast.LENGTH_SHORT)
                        .show()
                }

            })


    }


    private fun prepareRvForMainActivity() {
        worksAdapter = WorksAdapter(this)
        binding.workRV.apply {
            adapter = worksAdapter
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_activity, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.logOut -> {
                val builder = AlertDialog.Builder(this)
                val alertDialog = builder.create()
                builder
                    .setTitle("Log Out")
                    .setMessage("Are you sure you want to log out?")
                    .setPositiveButton("Yes"){dialogInterface,which->
                        FirebaseAuth.getInstance().signOut()
                        val intent = Intent(this,SignInActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                    .setNegativeButton("No"){dialogInterface, which->
                        alertDialog.dismiss()
                    }
                    .show()
                    .setCancelable(false)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}