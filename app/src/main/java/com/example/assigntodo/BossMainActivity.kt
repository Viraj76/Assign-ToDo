package com.example.assigntodo

import android.os.Binder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.BoringLayout.make
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.assigntodo.adapter.BossMainActivityAdapter
import com.example.assigntodo.databinding.ActivityBossMainBinding
import com.example.assigntodo.models.Boss
import com.example.assigntodo.models.Employees
import com.example.assigntodo.utils.Config
import com.google.firebase.database.*

class BossMainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBossMainBinding
    private lateinit var bossMainActivityAdapter: BossMainActivityAdapter
    private lateinit var empDb : DatabaseReference
    private lateinit var toolBar:Toolbar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBossMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Config.showDialog(this)
        preparerRvForBossMainActivity()
        showingAllEmployees()
    }

    private fun showingAllEmployees() {
        empDb = FirebaseDatabase.getInstance().getReference("Employees")
        empDb .addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val empList = arrayListOf<Employees>()
                for(employees in snapshot.children){
                    val empData = employees.getValue(Employees::class.java)
                    empList.add(empData!!)
                }
                Config.hideDialog()
                bossMainActivityAdapter.setEmpList(empList)
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }

    private fun preparerRvForBossMainActivity() {
        bossMainActivityAdapter = BossMainActivityAdapter(this)
        binding.empRv.apply {
            layoutManager  = LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false)
            adapter = bossMainActivityAdapter
        }
    }
}