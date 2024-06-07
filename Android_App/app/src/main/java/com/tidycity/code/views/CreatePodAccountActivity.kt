package com.tidycity.code.views

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.tidycity.code.R
import com.tidycity.code.database_utils.DatabaseAccess
import com.tidycity.code.dataclasses_prototypes.Prototypes
import com.tidycity.code.firebase_utils.FirebaseDeclarations.firebaseAuth
import com.tidycity.code.firebase_utils.FirebaseDeclarations.firebaseUser
import com.tidycity.code.utilities.Extensions.isInternetEnabled
import com.tidycity.code.utilities.Extensions.toast
import com.tidycity.code.webservices_utils.ExtensionsRetrofit
import kotlinx.coroutines.launch
import java.util.regex.Pattern

class CreatePodAccountActivity : AppCompatActivity() {

    private lateinit var userEmail: String
    private lateinit var userPassword: String
    private lateinit var userName: String
    private lateinit var createAccountInputsArray: Array<EditText>
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var etConfirmPassword: EditText
    private lateinit var etUsername: EditText
    private lateinit var createAccountButton: Button
    private var identical = false

    private val pwdCharContaining: Pattern = Pattern.compile(
        "^" + "(?=.*[0-9])" +         //at least 1 digit
                "(?=.*[a-z])" +         //at least 1 lower case letter
                "(?=.*[A-Z])" +         //at least 1 upper case letter
                "(?=.*[@#$%^&+=_-])" +  //at least 1 special character
                "(?=\\S+$)" +  //no white spaces
                ".{6,}" +  //at least 6 characters
                "$"
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_pod)

        etUsername = findViewById(R.id.etUser)
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPwd)
        etConfirmPassword = findViewById(R.id.etRPwd)
        createAccountButton = findViewById(R.id.btnCreateAccount)

        createAccountInputsArray = arrayOf(etEmail, etPassword, etConfirmPassword, etUsername)
        createAccountButton.setOnClickListener {
            if (!isInternetEnabled()) {
                toast("Internet is disabled")
            } else registerAccount()
        }

//        signInButton.setOnClickListener {
//            startActivity(Intent(this, SignInActivity::class.java))
//            finish()
//        }
    }

    private fun passwordContain(): Boolean =
        pwdCharContaining.matcher(etPassword.text.toString().trim()).matches()

    private fun empty(): Boolean = etEmail.text.toString().trim().isEmpty() &&
            etPassword.text.toString().trim().isEmpty() &&
            etConfirmPassword.text.toString().trim().isEmpty() &&
            etUsername.text.toString().trim().isEmpty()

    private fun identicalPassword(): Boolean {
        if (!passwordContain()) {
            etConfirmPassword.error = getString(R.string.weak_pass)
            identical = false
        } else if (!empty() &&
            etPassword.text.toString().trim() == etConfirmPassword.text.toString().trim()
        ) {
            identical = true
        } else if (empty()) {
            createAccountInputsArray.forEach { input ->
                if (input.text.toString().trim().isEmpty()) {
                    input.error = "${input.contentDescription} is required"
                    identical = false
                }
            }
        } else {
            toast("passwords are not matching !")
        }
        return identical
    }

    private fun registerAccount() {
        if (!empty() && !passwordContain()) {
            // identicalPassword() returns true only  when inputs are not empty and passwords are identical
            userEmail = etEmail.text.toString().trim()
            userPassword = etPassword.text.toString().trim()
            userName = etUsername.text.toString().trim()
            val name= etConfirmPassword.text.toString().trim()

            ExtensionsRetrofit(this@CreatePodAccountActivity)
                .createPodAccount(
                    credentials = Prototypes.CreateSolidParams(
                        email = userEmail,
                        password = userPassword,
                        username = userName,
                        name = name
                    )
                )
        }
    }
}