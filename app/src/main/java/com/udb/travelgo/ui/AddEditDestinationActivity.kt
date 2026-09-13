package com.udb.travelgo.ui

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.udb.travelgo.R
import com.udb.travelgo.databinding.ActivityAddEditDestinationBinding
import com.udb.travelgo.model.Destination
import com.udb.travelgo.repository.DestinationRepository
import kotlinx.coroutines.launch

class AddEditDestinationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddEditDestinationBinding
    private var destinationId: String? = null
    private var existingImageUrl: String = ""
    private var selectedImageUri: Uri? = null
    private var countries: List<String> = emptyList()

    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                selectedImageUri = uri
                binding.imgPlaceholder.visibility = View.GONE
                Glide.with(this).load(uri).centerCrop().into(binding.imgPreview)
                binding.tvImageError.visibility = View.GONE
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddEditDestinationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        binding.toolbar.setNavigationOnClickListener { finish() }

        countries = resources.getStringArray(R.array.countries_array).toList()
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, countries)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerCountry.adapter = spinnerAdapter

        destinationId = intent.getStringExtra(EXTRA_DESTINATION_ID)
        if (destinationId != null) {
            supportActionBar?.title = getString(R.string.title_edit_destination)
            loadDestination(destinationId!!)
        } else {
            supportActionBar?.title = getString(R.string.title_add_destination)
        }

        binding.btnSelectImage.setOnClickListener { pickImageLauncher.launch("image/*") }
        binding.btnSave.setOnClickListener { attemptSave() }
    }

    private fun loadDestination(id: String) {
        setLoading(true)
        lifecycleScope.launch {
            try {
                val destination = DestinationRepository.getDestination(id)
                if (destination != null) {
                    binding.etName.setText(destination.name)
                    binding.etPrice.setText(destination.price.toString())
                    binding.etDescription.setText(destination.description)
                    existingImageUrl = destination.imageUrl
                    if (existingImageUrl.isNotEmpty()) {
                        binding.imgPlaceholder.visibility = View.GONE
                        Glide.with(this@AddEditDestinationActivity)
                            .load(existingImageUrl).centerCrop().into(binding.imgPreview)
                    }
                    val countryIndex = countries.indexOf(destination.country)
                    if (countryIndex >= 0) binding.spinnerCountry.setSelection(countryIndex)
                }
            } catch (e: Exception) {
                Toast.makeText(
                    this@AddEditDestinationActivity,
                    getString(R.string.error_save_failed, e.localizedMessage ?: ""),
                    Toast.LENGTH_LONG
                ).show()
            } finally {
                setLoading(false)
            }
        }
    }

    private fun attemptSave() {
        val name = binding.etName.text?.toString()?.trim().orEmpty()
        val country = binding.spinnerCountry.selectedItem?.toString().orEmpty()
        val priceText = binding.etPrice.text?.toString()?.trim().orEmpty()
        val description = binding.etDescription.text?.toString()?.trim().orEmpty()

        var isValid = true
        binding.tilName.error = null
        binding.tvCountryError.visibility = View.GONE
        binding.tilPrice.error = null
        binding.tilDescription.error = null
        binding.tvImageError.visibility = View.GONE

        if (name.isEmpty()) {
            binding.tilName.error = getString(R.string.error_empty_name)
            isValid = false
        }

        if (binding.spinnerCountry.selectedItemPosition == 0) {
            binding.tvCountryError.text = getString(R.string.error_empty_country)
            binding.tvCountryError.visibility = View.VISIBLE
            isValid = false
        }

        val price = priceText.toDoubleOrNull()
        if (priceText.isEmpty()) {
            binding.tilPrice.error = getString(R.string.error_empty_price)
            isValid = false
        } else if (price == null) {
            binding.tilPrice.error = getString(R.string.error_invalid_price)
            isValid = false
        } else if (price <= 0) {
            binding.tilPrice.error = getString(R.string.error_price_positive)
            isValid = false
        }

        if (description.isEmpty()) {
            binding.tilDescription.error = getString(R.string.error_empty_description)
            isValid = false
        } else if (description.length < 20) {
            binding.tilDescription.error = getString(R.string.error_short_description)
            isValid = false
        }

        val hasImage = selectedImageUri != null || existingImageUrl.isNotEmpty()
        if (!hasImage) {
            binding.tvImageError.text = getString(R.string.error_no_image)
            binding.tvImageError.visibility = View.VISIBLE
            isValid = false
        }

        if (!isValid || price == null) return

        setLoading(true)
        lifecycleScope.launch {
            try {
                var imageUrl = existingImageUrl
                val uriToUpload = selectedImageUri
                if (uriToUpload != null) {
                    imageUrl = DestinationRepository.uploadImage(uriToUpload)
                }

                val destination = Destination(
                    id = destinationId ?: "",
                    name = name,
                    country = country,
                    price = price,
                    description = description,
                    imageUrl = imageUrl,
                    userId = DestinationRepository.currentUserId ?: ""
                )

                if (destinationId != null) {
                    DestinationRepository.updateDestination(destination)
                } else {
                    DestinationRepository.addDestination(destination)
                }

                Toast.makeText(this@AddEditDestinationActivity, R.string.msg_saved, Toast.LENGTH_SHORT).show()
                finish()
            } catch (e: Exception) {
                Toast.makeText(
                    this@AddEditDestinationActivity,
                    getString(R.string.error_save_failed, e.localizedMessage ?: ""),
                    Toast.LENGTH_LONG
                ).show()
            } finally {
                setLoading(false)
            }
        }
    }

    private fun setLoading(loading: Boolean) {
        binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        binding.btnSave.isEnabled = !loading
    }

    companion object {
        const val EXTRA_DESTINATION_ID = "extra_destination_id"
    }
}
