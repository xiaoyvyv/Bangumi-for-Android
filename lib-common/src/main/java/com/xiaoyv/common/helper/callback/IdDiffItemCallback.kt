package com.xiaoyv.common.helper.callback

import android.annotation.SuppressLint
import androidx.recyclerview.widget.DiffUtil

/**
 * Class: [IdDiffItemCallback]
 *
 * @author why
 * @since 12/6/23
 */
interface IdEntity : DiffEntity<String>

interface DiffEntity<T> {
    var id: T
}

class IdDiffItemCallback<I, T : DiffEntity<I>> : DiffUtil.ItemCallback<T>() {
    override fun areItemsTheSame(oldItem: T, newItem: T): Boolean {
        return oldItem.id == newItem.id
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
        return oldItem == newItem
    }
}