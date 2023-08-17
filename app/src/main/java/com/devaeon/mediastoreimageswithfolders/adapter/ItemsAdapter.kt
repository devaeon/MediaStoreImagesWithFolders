package com.devaeon.mediastoreimageswithfolders.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.devaeon.mediastoreimageswithfolders.R
import com.devaeon.mediastoreimageswithfolders.adapter.helper.getDiffUtilCallBack
import com.devaeon.mediastoreimageswithfolders.databinding.ItemGridBinding
import com.devaeon.mediastoreimageswithfolders.databinding.ItemSectionHeaderBinding
import com.devaeon.mediastoreimageswithfolders.extensions.formatDate
import com.devaeon.mediastoreimageswithfolders.extensions.formatSizeThousand
import com.devaeon.mediastoreimageswithfolders.model.AbsListItems
import com.devaeon.mediastoreimageswithfolders.model.DateGroupedItems
import com.devaeon.mediastoreimageswithfolders.model.ListItems

private const val TAG = "ItemsAdapterLogs"

class ItemsAdapter(private val itemClick: (item: ListItems) -> Unit) : ListAdapter<AbsListItems, RecyclerView.ViewHolder>(getDiffUtilCallBack()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)

        return when (viewType) {
            AbsListItems.TYPE_DATE -> HeaderViewHolder(ItemSectionHeaderBinding.inflate(layoutInflater, parent, false))
            AbsListItems.TYPE_GENERAL -> ViewHolder(ItemGridBinding.inflate(layoutInflater, parent, false))
            else -> throw IllegalStateException("view not found")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val items = getItem(position)
        when (holder.itemViewType) {
            AbsListItems.TYPE_DATE -> (holder as HeaderViewHolder).bind(item = items as DateGroupedItems)
            AbsListItems.TYPE_GENERAL -> (holder as ViewHolder).bind(item = items as ListItems)
        }
    }

    override fun getItemCount(): Int {

        return currentList.size
    }

    override fun getItemViewType(position: Int): Int = currentList[position].type


    inner class ViewHolder(private val binding: ItemGridBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ListItems) {

            binding.apply {
                root.setOnClickListener { itemClick.invoke(item) }

                Log.i(TAG, "totalListSize: ${item.inneListSize}")

                itemName.text = item.name
                itemDateCreated.text = item.dateCreated.formatDate()
                itemSize.text = item.size.formatSizeThousand()

                Glide.with(root.context)
                    .load(item.imageUri)
                    .apply(RequestOptions().fitCenter())
                    .into(image)
            }
        }
    }

    inner class HeaderViewHolder(private val headerBinding: ItemSectionHeaderBinding) : RecyclerView.ViewHolder(headerBinding.root) {
        fun bind(item: DateGroupedItems) {
            headerBinding.apply {
                title.text = item.date
                itemCount.text = root.context.getString(R.string.itemCount, item.itemCount.toString())
                groupedListSize.text = item.listGroupSize.formatSizeThousand()
            }
        }
    }
}

