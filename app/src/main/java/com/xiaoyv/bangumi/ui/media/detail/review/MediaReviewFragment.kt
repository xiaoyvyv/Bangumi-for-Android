package com.xiaoyv.bangumi.ui.media.detail.review

import android.os.Bundle
import androidx.core.os.bundleOf
import com.xiaoyv.bangumi.R
import com.xiaoyv.bangumi.base.BaseListFragment
import com.xiaoyv.bangumi.helper.RouteHelper
import com.xiaoyv.blueprint.constant.NavKey
import com.xiaoyv.common.api.parser.entity.MediaReviewBlogEntity
import com.xiaoyv.common.kts.setOnDebouncedChildClickListener
import com.xiaoyv.widget.binder.BaseQuickDiffBindingAdapter

/**
 * Class: [MediaReviewFragment]
 *
 * @author why
 * @since 11/24/23
 */
class MediaReviewFragment : BaseListFragment<MediaReviewBlogEntity, MediaReviewViewModel>() {
    override val isOnlyOnePage: Boolean
        get() = false

    override val loadingBias: Float
        get() = 0.3f

    override fun initArgumentsData(arguments: Bundle) {
        viewModel.mediaId = arguments.getString(NavKey.KEY_STRING).orEmpty()
    }

    override fun onCreateContentAdapter(): BaseQuickDiffBindingAdapter<MediaReviewBlogEntity, *> {
        return MediaReviewAdapter()
    }

    override fun initListener() {
        super.initListener()

        contentAdapter.setOnDebouncedChildClickListener(R.id.item_review) {
            RouteHelper.jumpBlogDetail(it.id)
        }

        contentAdapter.setOnDebouncedChildClickListener(R.id.iv_avatar) {
            RouteHelper.jumpUserDetail(it.userId)
        }
    }

    companion object {
        fun newInstance(mediaId: String): MediaReviewFragment {
            return MediaReviewFragment().apply {
                arguments = bundleOf(NavKey.KEY_STRING to mediaId)
            }
        }
    }
}