package hs.project.imagefilter.activity.edit_image

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import hs.project.imagefilter.activity.filtered_image.FilterImagedActivity
import hs.project.imagefilter.activity.main.MainActivity
import hs.project.imagefilter.adapter.ImageFiltersAdapter
import hs.project.imagefilter.data.ImageFilter
import hs.project.imagefilter.databinding.ActivityEditImageBinding
import hs.project.imagefilter.listener.ImageFilterListener
import hs.project.imagefilter.util.displayToast
import hs.project.imagefilter.util.hide
import hs.project.imagefilter.util.show
import hs.project.imagefilter.viewmodel.EditImageViewModel
import jp.co.cyberagent.android.gpuimage.GPUImage
import org.koin.androidx.viewmodel.ext.android.viewModel

class EditImageActivity : AppCompatActivity(), ImageFilterListener {

    private lateinit var binding: ActivityEditImageBinding
    private val viewModel: EditImageViewModel by viewModel()
    private lateinit var gpuImage: GPUImage

    // Image bitmaps
    private lateinit var originalBitmap: Bitmap
    private val filteredBitmap = MutableLiveData<Bitmap>()

    companion object{
        const val KEY_FILTERED_IMAGE_URI = "filteredImageUri"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditImageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setListeners()
        setupObservers()
        prepareImagePreview()

    }

    private fun setupObservers() {

        // kotlin 1.4 보다 낮은 버전에서는 observe(this, Observer{} ) Observer 를 꼭 붙여줘야함
        viewModel.imagePreviewDataState.observe(this, Observer {
            val dataState = it ?: return@Observer
            binding.progressBar.visibility =
                if (dataState.isLoading) View.VISIBLE else View.GONE
            dataState.bitmap?.let { bitmap ->
                // For the first time 'filtered image = original image'
                originalBitmap = bitmap
                filteredBitmap.value = bitmap

                with(originalBitmap){
                    gpuImage.setImage(this)
                    binding.ivPreView.show()
                    viewModel.loadImageFilters(this)
                }


            } ?: kotlin.run {
                dataState.error?.let { error -> displayToast(error) }
            }
        })

        // kotlin 1.4 이상 버전에서는 Observer 생략 가능
       viewModel.imageFilterDataState.observe(this, {
            val imageFiltersDataState = it ?: return@observe
            binding.filterProgressBar.visibility =
                if (imageFiltersDataState.isLoading) View.VISIBLE else View.GONE
            imageFiltersDataState.imageFilters?.let { imageFilters ->
                ImageFiltersAdapter(
                    imageFilters = imageFilters,
                    imageFilterListener = this
                ).also { adapter ->
                    binding.rvFilter.adapter = adapter
                }
            } ?: kotlin.run {
                imageFiltersDataState.error?.let { error ->
                    displayToast(error)
                }
            }
        })


        filteredBitmap.observe(this, {
            bitmap ->
            binding.ivPreView.setImageBitmap(bitmap)
        })

        viewModel.saveFilteredImageDataState.observe(this, {
            val saveFilteredImageDataState = it ?: return@observe
            if (saveFilteredImageDataState.isLoading){
                binding.btnSave.hide(1)
                binding.saveProgressBar.show()
            } else {
                binding.saveProgressBar.hide(1)
                binding.btnSave.show()
            }
            saveFilteredImageDataState.uri?.let {
                savedImageUri ->
                Intent(applicationContext,
                FilterImagedActivity::class.java).also {
                    filteredImageIntent ->
                    filteredImageIntent.putExtra(KEY_FILTERED_IMAGE_URI, savedImageUri)
                    startActivity(filteredImageIntent)
                }
            } ?: kotlin.run {
                saveFilteredImageDataState.error?.let {
                    error ->
                    displayToast(error)
                }
            }

        })
    }

    private fun prepareImagePreview() {
        gpuImage = GPUImage(applicationContext)

        intent.getParcelableExtra<Uri>(MainActivity.KEY_IMAGE_URI)?.let { imageUri ->
            viewModel.prepareImagePreview(imageUri)
        }
    }

    private fun setListeners() {

        binding.ivBack.setOnClickListener { onBackPressed() }

        // 손을 땔 떄까지 imageView 를 길게 누르면 원본 이미지가 표시.
        binding.ivPreView.setOnLongClickListener {
            binding.ivPreView.setImageBitmap(originalBitmap)
            return@setOnLongClickListener false
        }

        binding.ivPreView.setOnClickListener {
            binding.ivPreView.setImageBitmap(filteredBitmap.value)
        }

        binding.btnSave.setOnClickListener {
            filteredBitmap.value?.let {
                bitmap ->
                viewModel.saveFilteredImage(bitmap)
            }
        }

    }

    override fun onFilterSelected(imageFilter: ImageFilter) {
        with(imageFilter){
            with(gpuImage){
                setFilter(filter)
                filteredBitmap.value = bitmapWithFilterApplied
            }
        }
    }
}