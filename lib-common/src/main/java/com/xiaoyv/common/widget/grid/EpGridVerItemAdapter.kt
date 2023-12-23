package com.xiaoyv.common.widget.grid

import com.xiaoyv.common.api.parser.entity.MediaChapterEntity
import com.xiaoyv.common.databinding.ViewEpVerItemBinding
import com.xiaoyv.common.helper.callback.IdDiffItemCallback
import com.xiaoyv.widget.binder.BaseQuickBindingHolder
import com.xiaoyv.widget.binder.BaseQuickDiffBindingAdapter

/**
 * Class: [EpGridVerItemAdapter]
 *
 * @author why
 * @since 12/22/23
 */
class EpGridVerItemAdapter :
    BaseQuickDiffBindingAdapter<MediaChapterEntity, ViewEpVerItemBinding>(IdDiffItemCallback()) {

    override fun BaseQuickBindingHolder<ViewEpVerItemBinding>.converted(item: MediaChapterEntity) {
        EpGridHelper.converted(context, item, binding.tvEp, binding.tvEpType)
    }
}