package com.example.assigntodo.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.assigntodo.BossMainActivity
import com.example.assigntodo.EmployeeMainActivity
import com.example.assigntodo.databinding.ActivitySignInBinding
import com.example.assigntodo.utils.Config
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class SignInActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignInBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    private lateinit var existedContractorId : String
    private lateinit var existedClientId : String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener { loggingUser() }
        binding.tvSignUp.setOnClickListener { goingToSignUpActivity() }

    }

    private fun goingToSignUpActivity() {
        val intent = Intent(this, SignUpActivity::class.java)
        startActivity(intent)
    }

    private fun loggingUser() {
        Config.showDialog(this)
        firebaseAuth = FirebaseAuth.getInstance()
        val email = binding.etEmail.text.toString()
        val password = binding.etPassword.text.toString()


        if (email.isNotEmpty() && password.isNotEmpty()) {

            firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {

                        // just current user id who wants to log in
                        val currentUserId = firebaseAuth.currentUser?.uid
                        //initializing existedClientId and existedContractorId
                        existedClientId = "random"
                        existedContractorId = "random"

                        val clientDatabaseReference = FirebaseDatabase.getInstance().getReference("Employees")
                        clientDatabaseReference.addListenerForSingleValueEvent(object :
                            ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                for(childrenSnapshot in snapshot.children){
                                    val clientsIds = childrenSnapshot.child("empId").value.toString()
                                    if(clientsIds == currentUserId){
                                        existedClientId=clientsIds
                                        break
                                    }
                                }
                                if(existedClientId == currentUserId) {
                                    Config.hideDialog()
                                    Toast.makeText(this@SignInActivity, "Signed In Successfully!", Toast.LENGTH_SHORT).show()
                                    val intent = Intent(this@SignInActivity, EmployeeMainActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                }
                            }
                            override fun onCancelled(error: DatabaseError) {
                                Config.hideDialog()
                                Toast.makeText(this@SignInActivity, error.message.toString(), Toast.LENGTH_SHORT).show()
                            }
                        })

                        val contractorsRef = FirebaseDatabase.getInstance().getReference("Bosses")
                        contractorsRef.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                for (childSnapshot in dataSnapshot.children) {
                                    val contractorIds = childSnapshot.child("bossId").value.toString()
                                    if(contractorIds == currentUserId){
                                        existedContractorId=contractorIds
                                        break
                                    }
                                }
                                if( existedContractorId== currentUserId){
                                    Config.hideDialog()
                                    Toast.makeText(this@SignInActivity, "Signed In Successfully!", Toast.LENGTH_SHORT).show()
                                    val intent = Intent(this@SignInActivity, BossMainActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                }
                            }
                            override fun onCancelled(databaseError: DatabaseError) {
                                Toast.makeText(this@SignInActivity, databaseError.message, Toast.LENGTH_SHORT).show()
                            }
                        })
                    }
                    else{
                        Toast.makeText(this, task.exception?.message.toString(), Toast.LENGTH_SHORT).show()
                    }
                }
        }
        else Toast.makeText(this, "Empty fields are not allowed", Toast.LENGTH_SHORT).show()
    }
}
