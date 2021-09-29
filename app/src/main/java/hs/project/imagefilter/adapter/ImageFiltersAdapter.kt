package hs.project.imagefilter.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import hs.project.imagefilter.R
import hs.project.imagefilter.data.ImageFilter
import hs.project.imagefilter.databinding.ItemContainerFilterBinding
import hs.project.imagefilter.listener.ImageFilterListener

class ImageFiltersAdapter(
    private val imageFilters: List<ImageFilter>,
    private val imageFilterListener: ImageFilterListener
) : RecyclerView.Adapter<ImageFiltersAdapter.ImageFilterViewHolder>() {

    private var selPosition = 0  // 현재 선택된 위치 값
    private var prevPosition = 0  // 이전에 선택된 위치 값

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageFilterViewHolder {
        val binding =
            ItemContainerFilterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ImageFilterViewHolder(binding)
    }

    override fun getItemCount() = imageFilters.size

    override fun onBindViewHolder(holder: ImageFilterViewHolder, position: Int) {
        with(holder) {
            with(imageFilters[position]) {
                binding.ivRoundedPreview.setImageBitmap(filterPreview)
                binding.tvFilterName.text = name
                binding.root.setOnClickListener {
                    if (position != selPosition) {
                        imageFilterListener.onFilterSelected(this)
                        prevPosition = selPosition
                        selPosition = position

                        with(this@ImageFiltersAdapter){
                            notifyItemChanged(prevPosition, Unit)
                            notifyItemChanged(selPosition, Unit)
                        }


                    }

                }
            }
            binding.tvFilterName.setTextColor(
                ContextCompat.getColor(
                    binding.tvFilterName.context,
                    if (selPosition == position)
                        R.color.primaryDark
                    else
                        R.color.primaryText
                )
            )
        }
    }

    inner class ImageFilterViewHolder(val binding: ItemContainerFilterBinding) :
        RecyclerView.ViewHolder(binding.root)
}