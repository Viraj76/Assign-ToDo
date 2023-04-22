package com.example.assigntodo

import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.assigntodo.databinding.ActivityAssignBinding
import com.example.assigntodo.models.AssignedWork
import com.example.assigntodo.models.Boss
import com.example.assigntodo.models.Employees
import com.example.assigntodo.notification.NotificationData
import com.example.assigntodo.notification.PushNotification
import com.example.assigntodo.notification.api.ApiUtilities
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class AssignActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAssignBinding
    private var priority: String = "1"
    private lateinit var workRoom : String
    private lateinit var currentUserId : String
    private lateinit var empId : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAssignBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.greenOval.setImageResource(R.drawable.done)

        binding.toolBar.apply {
            title = "Assign Work"
            setSupportActionBar(this)
        }
        currentUserId = FirebaseAuth.getInstance().currentUser?.uid.toString()
        prioritySelection()
        userSelectingDate()
        binding.Done.setOnClickListener { workAssigned() }
//        val bossIdShRef = getSharedPreferences("Boss Id", MODE_PRIVATE)
//        bossIdShRef.edit().apply{
//            putString("BossId",currentUserId)
//            apply()
//        }
    }


    private fun prioritySelection() {
        binding.apply {
            greenOval.setOnClickListener {
                Toast.makeText(this@AssignActivity,"Priority : Low",Toast.LENGTH_SHORT).show()
                priority = "3"
                binding.greenOval.setImageResource(R.drawable.done)
                binding.yellowOval.setImageResource(0)
                binding.redOval.setImageResource(0)
            }
            yellowOval.setOnClickListener {
                Toast.makeText(this@AssignActivity,"Priority : Medium",Toast.LENGTH_SHORT).show()
                priority = "2"
                binding.greenOval.setImageResource(0)
                binding.yellowOval.setImageResource(R.drawable.done)
                binding.redOval.setImageResource(0)
            }
            redOval.setOnClickListener {
                Toast.makeText(this@AssignActivity,"Priority : High",Toast.LENGTH_SHORT).show()
                priority = "1"
                binding.greenOval.setImageResource(0)
                binding.yellowOval.setImageResource(0)
                binding.redOval.setImageResource(R.drawable.done)
            }
        }
    }



    private fun workAssigned() {
        binding.apply {
            val workTitle = etTitle.text.toString()
            val workSubTitle = etSubtitle.text.toString()
            val workDesc = etWorkDesc.text.toString()
            val date = binding.tvDate.text.toString()

            if(workTitle.isEmpty()) Toast.makeText(this@AssignActivity,"Enter title please",Toast.LENGTH_SHORT).show()
            else if(workDesc.isEmpty()) Toast.makeText(this@AssignActivity,"Enter description of the work please",Toast.LENGTH_SHORT).show()
            else if(binding.tvDate.text.toString() == "Last Date") Toast.makeText(this@AssignActivity,"Select a last date",Toast.LENGTH_SHORT).show()
            else{
                val assignedWork = AssignedWork(workTitle,workSubTitle,workDesc,priority,date)

                empId = intent.getStringExtra("EmpId").toString()
                val empName = intent.getStringExtra("EmpName")
                workRoom = currentUserId + empId
                FirebaseDatabase.getInstance().getReference("Works").child(workRoom).push()
                    .setValue(assignedWork)
                    .addOnCompleteListener {
                        if(it.isSuccessful){
                            sendNotification(workTitle)
                            Toast.makeText(this@AssignActivity,"Work has been assigned",Toast.LENGTH_SHORT).show()
                            val intent = Intent(this@AssignActivity,WorkActivity::class.java)
                            intent.putExtra("EmpId",empId)
                            intent.putExtra("EmpName",empName)
                            startActivity(intent)
                            finish()
                        }
                        else{
                            Toast.makeText(this@AssignActivity,it.exception.toString(),Toast.LENGTH_SHORT).show()
                        }
                    }
                    .addOnFailureListener {
                        Toast.makeText(this@AssignActivity,it.message.toString(),Toast.LENGTH_SHORT).show()
                    }
            }
            }
    }


    private fun sendNotification(workTitle: String) {
        FirebaseDatabase.getInstance().getReference("Employees").child(empId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val receiverData = snapshot.getValue(Employees::class.java)
                    if(snapshot.exists()){
                        FirebaseDatabase.getInstance().getReference("Bosses").child(currentUserId)
                            .addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    val senderUser = snapshot.getValue(Boss::class.java)
                                    val notificationData = PushNotification(NotificationData("Work Assigned",workTitle), receiverData!!.fcmToken!!)
                                    ApiUtilities.api.sendNotification(notificationData).enqueue(object :
                                        Callback<PushNotification> {
                                        override fun onResponse(
                                            call: Call<PushNotification>,
                                            response: Response<PushNotification>
                                        ) {
                                            if(response.isSuccessful){
                                                Toast.makeText(this@AssignActivity,"Notification Sent", Toast.LENGTH_SHORT).show()
                                            }
                                            else
                                                Toast.makeText(this@AssignActivity,response.errorBody().toString(), Toast.LENGTH_SHORT).show()
                                        }

                                        override fun onFailure(call: Call<PushNotification>, t: Throwable) {
                                            Toast.makeText(this@AssignActivity,"Something went wrong", Toast.LENGTH_SHORT).show()
                                        }

                                    })
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    TODO("Not yet implemented")
                                }

                            })

                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@AssignActivity,error.message, Toast.LENGTH_SHORT).show()
                }
            })
    }
    private fun userSelectingDate() {
        val myCalender = Calendar.getInstance()
        val datePicker = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            myCalender.apply {
                set(Calendar.YEAR, year)
                set(Calendar.MONTH, month)
                set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateLable(myCalender)
            }
        }
        binding.datePicker.setOnClickListener {
            DatePickerDialog(this,datePicker,myCalender.get(Calendar.YEAR),myCalender.get(Calendar.MONTH),myCalender.get(Calendar.DAY_OF_MONTH)).show()
        }

    }
    private fun updateLable(myCalender: Calendar) {
        val myFormat = "dd-MM-yyyy"
        val sdf = SimpleDateFormat(myFormat, Locale.UK)
        binding.tvDate.text = sdf.format(myCalender.time)
    }
}