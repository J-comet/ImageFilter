package hs.project.imagefilter.adapter

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import hs.project.imagefilter.databinding.ItemContainerSavedImageBinding
import hs.project.imagefilter.listener.SavedImageListener
import java.io.File

class SavedImageAdapter(
    private val savedImages: List<Pair<File,Bitmap>>,
    private val savedImageListener: SavedImageListener)
    : RecyclerView.Adapter<SavedImageAdapter.SavedImageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SavedImageViewHolder {
        val binding = ItemContainerSavedImageBinding.inflate(
            LayoutInflater.from(parent.context),parent,false
        )
        return SavedImageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SavedImageViewHolder, position: Int) {
        with(holder) {
            with(savedImages[position]){
                binding.ivSaved.setImageBitmap(second)  // Pair <first, second>
                binding.ivSaved.setOnClickListener {
                    savedImageListener.onImageClicked(first)
                }
            }
        }
    }

    override fun getItemCount() = savedImages.size

    inner class SavedImageViewHolder(val binding: ItemContainerSavedImageBinding)
        : RecyclerView.ViewHolder(binding.root)
}