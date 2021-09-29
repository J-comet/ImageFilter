package hs.project.imagefilter.activity.saved_image

import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.content.FileProvider
import hs.project.imagefilter.R
import hs.project.imagefilter.activity.edit_image.EditImageActivity
import hs.project.imagefilter.activity.filtered_image.FilterImagedActivity
import hs.project.imagefilter.adapter.SavedImageAdapter
import hs.project.imagefilter.databinding.ActivitySavedImageBinding
import hs.project.imagefilter.listener.SavedImageListener
import hs.project.imagefilter.util.displayToast
import hs.project.imagefilter.viewmodel.SavedImageViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File

class SavedImageActivity : AppCompatActivity(), SavedImageListener {

    private lateinit var binding: ActivitySavedImageBinding
    private val viewModel: SavedImageViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySavedImageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupObserver()
        setListeners()
        viewModel.loadSavedImages()
    }

    private fun setupObserver() {
        viewModel.savedImageDataState.observe(this, {
            val savedImagesDataState = it ?: return@observe
            binding.savedImageProgressBar.visibility =
                if (savedImagesDataState.isLoading)
                    View.VISIBLE
                else
                    View.GONE
            savedImagesDataState.savedImages?.let { savedImages ->
                SavedImageAdapter(
                    savedImages = savedImages,
                    savedImageListener = this
                ).also { savedImageAdapter ->
                    with(binding.rvSaveList) {
                        this.adapter = savedImageAdapter
                        visibility = View.VISIBLE
                    }
                }

            } ?: kotlin.run {
                savedImagesDataState.error?.let { error ->
                    displayToast(error)
                }
            }
        })
    }

    private fun setListeners() {
        binding.ivBack.setOnClickListener { onBackPressed() }
    }

    override fun onImageClicked(file: File) {
        var fileUri: Any? = null

        // API 24이상일 경우
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            fileUri = FileProvider.getUriForFile(
                applicationContext,
                "${packageName}.provider",
                file
            )
        } else {
            fileUri = Uri.fromFile(file)
        }

        Intent(
            applicationContext,
            FilterImagedActivity::class.java
        ).also {
            filteredImageIntent ->
            filteredImageIntent.putExtra(EditImageActivity.KEY_FILTERED_IMAGE_URI, fileUri)
            startActivity(filteredImageIntent)
        }

    }

}