package com.devaeon.mediastoreimageswithfolders.adapter.helper

import android.annotation.SuppressLint
import androidx.recyclerview.widget.DiffUtil

fun <T : Any> getDiffUtilCallBack(): DiffUtil.ItemCallback<T> {
    return object : DiffUtil.ItemCallback<T>() {
        override fun areItemsTheSame(oldItem: T, newItem: T): Boolean {
            return oldItem == newItem
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
            return oldItem == newItem
        }

        override fun getChangePayload(oldItem: T, newItem: T): Any? {
            println("DiffUtilCallback: getChangePayload ${oldItem == newItem}")
            return super.getChangePayload(oldItem, newItem)
        }
    }
}