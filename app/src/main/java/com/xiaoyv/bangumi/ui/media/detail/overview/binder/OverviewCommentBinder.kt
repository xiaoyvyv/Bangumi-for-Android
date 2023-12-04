package com.xiaoyv.bangumi.ui.media.detail.overview.binder

import android.content.Context
import android.view.ViewGroup
import com.chad.library.adapter.base.BaseMultiItemAdapter
import com.xiaoyv.bangumi.R
import com.xiaoyv.bangumi.databinding.FragmentOverviewCommentBinding
import com.xiaoyv.bangumi.ui.media.detail.comments.MediaCommentAdapter
import com.xiaoyv.bangumi.ui.media.detail.overview.OverviewAdapter
import com.xiaoyv.common.api.parser.entity.MediaCommentEntity
import com.xiaoyv.common.helper.RecyclerItemTouchedListener
import com.xiaoyv.common.kts.inflater
import com.xiaoyv.common.kts.setOnDebouncedChildClickListener
import com.xiaoyv.widget.binder.BaseQuickBindingHolder

/**
 * Class: [OverviewCommentBinder]
 *
 * @author why
 * @since 11/30/23
 */
class OverviewCommentBinder(
    private val touchedListener: RecyclerItemTouchedListener,
    private val clickItemListener: (MediaCommentEntity) -> Unit,
    private val clickUserListener: (MediaCommentEntity) -> Unit,
) : BaseMultiItemAdapter.OnMultiItemAdapterListener<OverviewAdapter.OverviewItem, BaseQuickBindingHolder<FragmentOverviewCommentBinding>> {
    private val itemAdapter by lazy { MediaCommentAdapter() }

    override fun onBind(
        holder: BaseQuickBindingHolder<FragmentOverviewCommentBinding>,
        position: Int,
        item: OverviewAdapter.OverviewItem?
    ) {
        item ?: return
        holder.binding.rvComment.adapter = itemAdapter
        holder.binding.rvComment.addOnItemTouchListener(touchedListener)
        holder.binding.rvComment.setInitialPrefetchItemCount(item.mediaDetailEntity.comments.size)
        itemAdapter.submitList(item.mediaDetailEntity.comments)
        itemAdapter.setOnDebouncedChildClickListener(R.id.item_comment, block = clickItemListener)
        itemAdapter.setOnDebouncedChildClickListener(R.id.iv_avatar, block = clickUserListener)
    }

    override fun onCreate(
        context: Context,
        parent: ViewGroup,
        viewType: Int
    ) = BaseQuickBindingHolder(
        FragmentOverviewCommentBinding.inflate(context.inflater, parent, false)
    )
}