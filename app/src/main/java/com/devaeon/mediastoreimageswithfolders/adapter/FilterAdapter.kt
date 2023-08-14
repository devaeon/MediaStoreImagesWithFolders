package com.devaeon.mediastoreimageswithfolders.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.devaeon.mediastoreimageswithfolders.R
import com.devaeon.mediastoreimageswithfolders.adapter.helper.getDiffUtilCallBack
import com.devaeon.mediastoreimageswithfolders.databinding.ItemFilterBinding
import com.devaeon.mediastoreimageswithfolders.model.FolderListWithData
import com.devaeon.mediastoreimageswithfolders.model.ListItems

class FilterAdapter(private val callback: (mediaType: String, title: String, images: ArrayList<ListItems>) -> Unit) :
    ListAdapter<FolderListWithData, FilterAdapter.ViewHolder>(getDiffUtilCallBack()) {

    private var selectedPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemFilterBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.binding.apply {
            name.text = currentItem.folderName

            if (selectedPosition == position) {
                name.setTextColor(Color.WHITE)
                name.background =
                    ContextCompat.getDrawable(root.context, R.drawable.bg_round_on_selection)
            } else {
                name.setTextColor(ContextCompat.getColor(root.context, R.color.textColorSecondary))
                name.background = ContextCompat.getDrawable(root.context, R.drawable.bg_round)
            }
        }
    }

    override fun onCurrentListChanged(
        previousList: MutableList<FolderListWithData>,
        currentList: MutableList<FolderListWithData>
    ) {
        super.onCurrentListChanged(previousList, currentList)
    }

    inner class ViewHolder(val binding: ItemFilterBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                val copyOfLastCheckedPosition: Int = selectedPosition
                selectedPosition = layoutPosition
                notifyItemChanged(copyOfLastCheckedPosition)
                notifyItemChanged(selectedPosition)
                callback.invoke("FolderListWithData", currentList[layoutPosition].folderName, currentList[layoutPosition].images)

            }
        }

    }
}