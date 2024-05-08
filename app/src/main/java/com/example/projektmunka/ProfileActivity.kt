package com.example.projektmunka

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.TextView
import com.example.projektmunka.utils.Constants.USER_PROFILE_IMAGE
import android.widget.Toast
import androidx.activity.viewModels
import com.example.projektmunka.databinding.ActivityProfileBinding
import com.example.projektmunka.viewModel.userManagementViewModel.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.io.IOException

@AndroidEntryPoint
class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding

    private val userProfileViewModel: ProfileViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityProfileBinding.inflate(layoutInflater)
        binding.viewModel = userProfileViewModel
        binding.lifecycleOwner = this
        setContentView(binding.root)

        userProfileViewModel.email.observe(this) { newEmail ->
            // Update UI with the newEmail
            // For example, set it to a TextView
            findViewById<TextView>(R.id.editTextEmail)
        }

        binding.ivUserPhoto.setOnClickListener{
            selectImage()
        }
        binding.btnSubmit.setOnClickListener{
            userProfileViewModel.updateUserProfile()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == USER_PROFILE_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    try {
                        // Correctly handle the image selection result
                        val selectedImageUri = data.data
                        val bitmap = MediaStore.Images.Media.getBitmap(
                            contentResolver, selectedImageUri
                        )
                        userProfileViewModel.bitmap = bitmap
                        userProfileViewModel.uploadPhoto()
                        binding.ivUserPhoto.setImageBitmap(bitmap)
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(this, "Canceled", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun selectImage(){
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), USER_PROFILE_IMAGE)
    }
}