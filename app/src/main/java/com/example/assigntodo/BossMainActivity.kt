package com.example.assigntodo

import android.content.Intent
import android.os.Binder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.BoringLayout.make
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.assigntodo.adapter.BossMainActivityAdapter
import com.example.assigntodo.auth.SignInActivity
import com.example.assigntodo.databinding.ActivityBossMainBinding
import com.example.assigntodo.models.Boss
import com.example.assigntodo.models.Employees
import com.example.assigntodo.utils.Config
import com.google.firebase.auth.FirebaseAuth
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
        binding.toolBar.apply {
            title = "Employees"
            setSupportActionBar(this)
        }
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_activity,menu)
        return  true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {
            R.id.logOut -> {
                val builder = AlertDialog.Builder(this)
                val alertDialog = builder.create()
                builder
                    .setTitle("Log Out")
                    .setMessage("Are you sure you want to log out?")
                    .setPositiveButton("Yes"){dialogInterface,which->
                        FirebaseAuth.getInstance().signOut()
                        val intent = Intent(this, SignInActivity::class.java)
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