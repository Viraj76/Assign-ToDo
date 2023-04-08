package com.example.assigntodo.auth

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ProgressBar
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.assigntodo.BossMainActivity
import com.example.assigntodo.R
import com.example.assigntodo.databinding.ActivitySignUpBinding
import com.example.assigntodo.models.Boss
import com.example.assigntodo.models.Employees
import com.example.assigntodo.utils.Config
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding:ActivitySignUpBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var radioGroup: RadioGroup
    private lateinit var databaseReference: DatabaseReference
    private lateinit var sharedPreferences: SharedPreferences
    private  var userPreferences: String?=null
    private lateinit var progressBar: ProgressBar


    private var imageUri : Uri? = null
    private val selectImage = registerForActivityResult(ActivityResultContracts.GetContent()) {
        imageUri = it
        binding.ivUserImage.setImageURI(imageUri)

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseAuth = FirebaseAuth.getInstance()

        binding.apply {
            ivUserImage.setOnClickListener {
                selectImage.launch("image/*")
            }
            radioGroup.setOnCheckedChangeListener { _, checkedId -> storingUserTypeInSharedReferences(checkedId) }
            btnRegister.setOnClickListener { createNewUser() }
            tvSignIn.setOnClickListener { goingToSignInActivity() }
        }
    }

    private fun goingToSignInActivity() {
        val intent = Intent(this, SignInActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun createNewUser() {
        Config.showDialog(this)
        val name = binding.etName.text.toString()
        val email = binding.etEmail.text.toString()
        val password = binding.etPassword.text.toString()
        val confirmPassword = binding.etConfirmPassword.text.toString()
        if(name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty()){
            if(password == confirmPassword){
                uploadUserImage()
            }
            else {
                Config.hideDialog()
                Toast.makeText(this,"Passwords are not matching", Toast.LENGTH_SHORT).show()
            }
        }
        else{
            Config.hideDialog()
            Toast.makeText(this,"Empty fields are not allowed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun uploadUserImage() {
        val currentUserUid  = FirebaseAuth.getInstance().currentUser?.uid.toString()
        val storageReference= FirebaseStorage.getInstance().getReference("Profile")
            .child(currentUserUid)
            .child("Profile.jpg")
        storageReference.putFile(imageUri!!)
            .addOnSuccessListener {
                storageReference.downloadUrl
                    .addOnSuccessListener {
                        storingUserData(it)
                    }
                    .addOnFailureListener{
                        Config.hideDialog()
                        Toast.makeText(this,it.message.toString(),Toast.LENGTH_SHORT).show()
                    }
            }
            .addOnFailureListener{
                Config.hideDialog()
                Toast.makeText(this,it.message.toString(),Toast.LENGTH_SHORT).show()
            }
    }

    private fun storingUserData(image: Uri?) {
        val name = binding.etName.text.toString()
        val email = binding.etEmail.text.toString()
        val password = binding.etPassword.text.toString()
        val confirmPassword = binding.etConfirmPassword.text.toString()
        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        userPreferences = sharedPreferences.getString("user_preference","")

        if(userPreferences.equals("Employee")){
                    databaseReference = FirebaseDatabase.getInstance().getReference("Employees")
                    firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener { task->
                        if(task.isSuccessful){
                            Config.hideDialog()
                            Toast.makeText(this,"Signed Up Successfully!", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this, SignInActivity::class.java)
                            startActivity(intent)
                            val uId = task.result.user?.uid.toString()
                            val userClient = Employees(empId = uId, empName = name, empEmail = email, empPassword = password, empImage = image.toString())
                            databaseReference.child(uId).setValue(userClient)

                        }
                        else{
                            Config.hideDialog()
                            Toast.makeText(this,task.exception.toString(), Toast.LENGTH_SHORT).show()
                        }
                    }
                }

        if(userPreferences.equals("Boss")){
                    databaseReference = FirebaseDatabase.getInstance().getReference("Bosses")
                    firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener { task->
                        if(task.isSuccessful){
                            Config.hideDialog()
                            Toast.makeText(this,"Signed Up Successfully!", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this, SignInActivity::class.java)
                            startActivity(intent)
                            val uId = task.result.user?.uid.toString()
                            val userContractor = Boss(bossId = uId, bossName = name, bossEmail = email, bossPassword = password, bossImage = image.toString())
//                            databaseReference.child(name).setValue(userContractor)
                            databaseReference.child(uId).setValue(userContractor)
                        }
                        else{
                            Config.hideDialog()
                            Toast.makeText(this,task.exception.toString(), Toast.LENGTH_SHORT).show()
                        }
                    }
                }
    }
    private fun storingUserTypeInSharedReferences(checkedId: Int) {
        val radioButton = findViewById<RadioButton>(checkedId)
        val sharedPref = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putString("user_preference", radioButton.text.toString())
        editor.apply()
    }

    override fun onStart() {
        super.onStart()
        val currentUser = FirebaseAuth.getInstance().currentUser
        if(currentUser!= null){
            startActivity(Intent(this,BossMainActivity::class.java))
        }
    }
}