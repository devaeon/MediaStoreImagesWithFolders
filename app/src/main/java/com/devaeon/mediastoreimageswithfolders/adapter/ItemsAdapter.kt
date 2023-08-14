package com.devaeon.mediastoreimageswithfolders.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.devaeon.mediastoreimageswithfolders.adapter.helper.getDiffUtilCallBack
import com.devaeon.mediastoreimageswithfolders.databinding.ItemFileDirListBinding
import com.devaeon.mediastoreimageswithfolders.databinding.ItemSectionBinding
import com.devaeon.mediastoreimageswithfolders.model.ListItems


class ItemsAdapter(
    private val itemClick: (item: ListItems) -> Unit
) : ListAdapter<ListItems, RecyclerView.ViewHolder>(
    getDiffUtilCallBack()
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)

        return when (viewType) {
            HEADER_VIEW_TYPE -> {
                val headerBinding = ItemSectionBinding.inflate(layoutInflater, parent, false)
                HeaderViewHolder(headerBinding)
            }

            IMAGE_VIEW_TYPE -> {
                val binding = ItemFileDirListBinding.inflate(layoutInflater, parent, false)
                ViewHolder(binding)
            }

            else -> throw IllegalStateException("view not found")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentItem = getItem(position)
        when (holder.itemViewType) {
            HEADER_VIEW_TYPE -> {
                (holder as HeaderViewHolder).headerBinding.apply {
                    root.setOnClickListener {
                        itemClick.invoke(currentItem)
                    }
                }
            }

            IMAGE_VIEW_TYPE -> {
                (holder as ViewHolder).binding.apply {
                    root.setOnClickListener {
                        itemClick.invoke(currentItem)
                    }

                    Glide.with(root.context)
                        .load(currentItem.imageUri)
                        .apply(RequestOptions().centerCrop())
                        .into(images)


                }
            }
        }
    }

    override fun getItemCount(): Int = currentList.size

    override fun getItemViewType(position: Int): Int {
        return IMAGE_VIEW_TYPE
    }


    inner class ViewHolder(val binding: ItemFileDirListBinding) :
        RecyclerView.ViewHolder(binding.root)

    inner class HeaderViewHolder(val headerBinding: ItemSectionBinding) :
        RecyclerView.ViewHolder(headerBinding.root)

    companion object {
        var HEADER_VIEW_TYPE: Int = 0
        var IMAGE_VIEW_TYPE: Int = 1
    }
}