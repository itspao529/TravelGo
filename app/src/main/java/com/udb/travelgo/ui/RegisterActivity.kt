package com.udb.travelgo.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.udb.travelgo.R
import com.udb.travelgo.databinding.ActivityRegisterBinding
import com.udb.travelgo.repository.DestinationRepository
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnRegister.setOnClickListener { attemptRegister() }
        binding.tvGoLogin.setOnClickListener { finish() }
        binding.btnBack.setOnClickListener { finish() }
    }

    private fun attemptRegister() {
        val email = binding.etEmail.text?.toString()?.trim().orEmpty()
        val password = binding.etPassword.text?.toString()?.trim().orEmpty()
        val confirmPassword = binding.etConfirmPassword.text?.toString()?.trim().orEmpty()

        var isValid = true
        binding.tilEmail.error = null
        binding.tilPassword.error = null
        binding.tilConfirmPassword.error = null

        if (email.isEmpty()) {
            binding.tilEmail.error = getString(R.string.error_empty_email)
            isValid = false
        }
        if (password.isEmpty()) {
            binding.tilPassword.error = getString(R.string.error_empty_password)
            isValid = false
        } else if (password.length < 6) {
            binding.tilPassword.error = getString(R.string.error_short_password)
            isValid = false
        }
        if (confirmPassword != password) {
            binding.tilConfirmPassword.error = getString(R.string.error_password_mismatch)
            isValid = false
        }
        if (!isValid) return

        setLoading(true)
        lifecycleScope.launch {
            try {
                DestinationRepository.register(email, password)
                Toast.makeText(this@RegisterActivity, R.string.success_register, Toast.LENGTH_SHORT).show()
                startActivity(Intent(this@RegisterActivity, CatalogActivity::class.java))
                finish()
            } catch (e: Exception) {
                showError(getString(R.string.error_register_failed, e.localizedMessage ?: ""))
            } finally {
                setLoading(false)
            }
        }
    }

    private fun setLoading(loading: Boolean) {
        binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        binding.btnRegister.isEnabled = !loading
    }

    private fun showError(message: String) {
        binding.tvError.text = message
        binding.tvError.visibility = View.VISIBLE
    }
}
