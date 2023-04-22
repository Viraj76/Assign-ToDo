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
import com.example.assigntodo.models.Boss
import com.example.assigntodo.models.Employees
import com.example.assigntodo.notification.NotificationData
import com.example.assigntodo.notification.PushNotification
import com.example.assigntodo.notification.api.ApiUtilities
import com.example.assigntodo.utils.Config
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class EmployeeMainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var worksAdapter: WorksAdapter
    private var buttonText = "Completed"
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
                                        }
                                        Config.hideDialog()
                                        worksAdapter.setWorkList(empWork)

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
                        } else {
                            Config.hideDialog()
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
        worksAdapter = WorksAdapter(this, ::onButtonClicked, buttonText)
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
                    .setPositiveButton("Yes") { dialogInterface, which ->
                        FirebaseAuth.getInstance().signOut()
                        val intent = Intent(this, SignInActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                    .setNegativeButton("No") { dialogInterface, which ->
                        alertDialog.dismiss()
                    }
                    .show()
                    .setCancelable(false)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun onButtonClicked(work: AssignedWork) {
        val builder = AlertDialog.Builder(this@EmployeeMainActivity)
        val alertDialog = builder.create()
        builder
            .setTitle("Completed Work")
            .setIcon(R.drawable.check_circle_black_24dp)
            .setMessage("Are you sure you completed this work?")
            .setPositiveButton("Yes") { dialogInterface, which ->
                deletingWork(work)
                sendNotification(work.workTitle)
            }
            .setNegativeButton("No") { dialogInterface, which ->
                alertDialog.dismiss()
            }
            .show()
            .setCancelable(false)

    }

//    val bossShPre = getSharedPreferences("Boss Id", MODE_PRIVATE)
    val bossId = "7eJG7n8GgaMIcnOqVQ5Sc3evJ5q1"
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
    private fun sendNotification(workTitle: String?) {
        FirebaseDatabase.getInstance().getReference("Bosses").child(bossId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                        val bossData = snapshot.getValue(Boss::class.java)
                        Log.d("hhh",bossData.toString())
                        val notificationData = PushNotification(
                            NotificationData("WORK COMPLETED", workTitle!!),
                            bossData!!.fcmToken!!
                        )
                        ApiUtilities.api.sendNotification(notificationData)
                            .enqueue(object : Callback<PushNotification> {
                                override fun onResponse(
                                    call: Call<PushNotification>,
                                    response: Response<PushNotification>
                                ) {
                                    if (response.isSuccessful) {
                                        Toast.makeText(
                                            this@EmployeeMainActivity,
                                            "Notification Sent",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    } else
                                        Toast.makeText(
                                            this@EmployeeMainActivity,
                                            response.errorBody().toString(),
                                            Toast.LENGTH_SHORT
                                        ).show()
                                }

                                override fun onFailure(call: Call<PushNotification>, t: Throwable) {
                                    Toast.makeText(
                                        this@EmployeeMainActivity,
                                        "Something went wrong",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            })


                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@EmployeeMainActivity, error.message, Toast.LENGTH_SHORT)
                        .show()
                }
            })
    }

    private fun deletingWork(work: AssignedWork) {
        work.isExpandable = !work.isExpandable
        Config.showDialog(this)
        var getRoom = ""
        val currentUser = FirebaseAuth.getInstance().currentUser?.uid
        FirebaseDatabase.getInstance().getReference("Works")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (allWorks in snapshot.children) {
                        if (allWorks.key!!.contains(currentUser!!)) {
                            getRoom = allWorks.key!!.toString()
                            FirebaseDatabase.getInstance().getReference("Works").child(getRoom)
                                .addListenerForSingleValueEvent(object : ValueEventListener {
                                    override fun onDataChange(snapshot: DataSnapshot) {
                                        for (userWork in snapshot.children) {
                                            val getUserWork =
                                                userWork.getValue(AssignedWork::class.java)
                                            if (getUserWork == work) {
                                                userWork.ref.removeValue()
                                                Toast.makeText(
                                                    this@EmployeeMainActivity,
                                                    "Work, Completed!",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }
                                    }

                                    override fun onCancelled(error: DatabaseError) {
                                        TODO("Not yet implemented")
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
}