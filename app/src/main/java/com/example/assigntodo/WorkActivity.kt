package com.example.assigntodo

import android.content.Intent
import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.example.assigntodo.databinding.ActivityWorkBinding
import com.google.firebase.database.R

class WorkActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWorkBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWorkBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.tvBlank.text = "No any work has been assigned to ${intent.getStringExtra("EmpName")}"
        binding.workTb.apply {
            title = intent.getStringExtra("EmpName")
            setSupportActionBar(this)
            setNavigationOnClickListener {
                startActivity(Intent(context,BossMainActivity::class.java))
            }
        }
        binding.fabAssignWork.setOnClickListener {
            binding.tvBlank.visibility = View.GONE;
        }
    }

}