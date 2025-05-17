package com.sokoldev.budgo.common.ui.user

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.sokoldev.budgo.R
import com.sokoldev.budgo.caregiver.ui.DashboardActivity
import com.sokoldev.budgo.caregiver.ui.user.CaregiverRegistrationActivity
import com.sokoldev.budgo.common.data.remote.network.ApiResponse
import com.sokoldev.budgo.common.utils.Global
import com.sokoldev.budgo.common.utils.prefs.PreferenceHelper
import com.sokoldev.budgo.common.utils.prefs.PreferenceKeys
import com.sokoldev.budgo.common.viewmodels.UserViewModel
import com.sokoldev.budgo.databinding.ActivityLoginBinding
import com.sokoldev.budgo.patient.ui.home.HomeActivity
import com.sokoldev.budgo.patient.ui.user.UserRegistrationActivity

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var helper: PreferenceHelper
    private lateinit var viewModel: UserViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        helper = PreferenceHelper.getPref(this)
        viewModel = ViewModelProvider(this)[UserViewModel::class.java]



        val isPatientUser = helper.isPatientUser()
        binding.continueButton.setOnClickListener {
            loginUser()
        }
        if (isPatientUser){
            binding.description.text = getString(R.string.patient_login)
        }else{
            binding.description.text = getString(R.string.caregiver_login)
        }

        binding.signup.setOnClickListener {
            if (isPatientUser) {
                val intent = Intent(this@LoginActivity, UserRegistrationActivity::class.java)
                startActivity(intent)
            } else {
                val intent = Intent(this@LoginActivity, CaregiverRegistrationActivity::class.java)
                startActivity(intent)
            }
        }

        binding.forgotPassword.setOnClickListener {
            val intent = Intent(this@LoginActivity, PasswordActivity::class.java)
            startActivity(intent)

        }

        initObserver()

    }


    private fun initObserver() {
        viewModel.apiResponseLogin.observe(this, Observer {
            when (it) {
                is ApiResponse.Success -> {
                    binding.loadingView.visibility = View.GONE
                    binding.loadingView.hide()
                    if (it.data.data != null) {
                        val user = it.data.data.user
                        helper.saveCurrentUser(user)
                        helper.saveStringValue(PreferenceKeys.PREF_USER_TOKEN, it.data.data.token)
                        helper.setUserLogin(true)


                        Log.d("TOOKEN",""+it.data.data.token)
                        Log.d("TOOKEN",user.toString())
                        Global.showMessage(
                            binding.root.rootView,
                            it.data.message,
                            Snackbar.LENGTH_SHORT
                        )
                        val userType = it.data.data.user.type
                        if (userType == "1") {
                            helper.setPatientUser(true)
                            val intent = Intent(this@LoginActivity, HomeActivity::class.java)
                            startActivity(intent)
                        } else {
                            helper.setPatientUser(false)
                            val intent = Intent(this@LoginActivity, DashboardActivity::class.java)
                            startActivity(intent)
                        }

                    } else {
                        Global.showErrorMessage(
                            binding.root.rootView,
                            it.data.message,
                            Snackbar.LENGTH_SHORT
                        )
                    }
                }

                is ApiResponse.Error -> {
                    Global.showErrorMessage(
                        binding.root.rootView,
                        it.errorMessage,
                        Snackbar.LENGTH_SHORT
                    )
                    binding.loadingView.visibility = View.GONE
                    binding.loadingView.hide()
                }

                ApiResponse.Loading -> {
                    Log.d("LoginActivity", "Loading state triggered")
                    binding.loadingView.visibility = View.VISIBLE
                    binding.loadingView.show()

                }
            }

        })
    }

    private fun loginUser() {
        val email = binding.edEmail.text.toString().trim()
        val password = binding.edPassword.text.toString().trim()

        if (email.isEmpty()) {
            binding.edEmail.error = "Please add your email address"
            return
        } else if (password.length < 8) {
            binding.edPassword.error = "Password length should be 8 characters"
            return
        }


        viewModel.loginUser(
            email,
            password
        )

    }


}

