package hs.project.imagefilter.activity.filtered_image

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import hs.project.imagefilter.R
import hs.project.imagefilter.activity.edit_image.EditImageActivity
import hs.project.imagefilter.databinding.ActivityFilterImagedBinding

class FilterImagedActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFilterImagedBinding
    private lateinit var fileUri: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFilterImagedBinding.inflate(layoutInflater)
        setContentView(binding.root)

        displayFilteredImage()
        setListener()
    }

    private fun displayFilteredImage(){
        intent.getParcelableExtra<Uri>(EditImageActivity.KEY_FILTERED_IMAGE_URI)?.let {
            imageUri ->
            fileUri = imageUri
            binding.ivFilteredImg.setImageURI(imageUri)
        }
    }

    private fun setListener(){
        binding.ivBack.setOnClickListener { onBackPressed() }

        binding.fabShare.setOnClickListener{
            with(Intent(Intent.ACTION_SEND)){
                putExtra(Intent.EXTRA_STREAM,fileUri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                type = "image/*"
                startActivity(this)
            }
        }
    }
}