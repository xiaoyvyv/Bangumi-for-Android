package com.xiaoyv.bangumi.ui.media.detail.overview

import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseMultiItemAdapter
import com.chad.library.adapter.base.BaseMultiItemAdapter.OnItemViewTypeListener
import com.xiaoyv.bangumi.ui.media.detail.overview.binder.OverviewCharacterBinder
import com.xiaoyv.bangumi.ui.media.detail.overview.binder.OverviewCommentBinder
import com.xiaoyv.bangumi.ui.media.detail.overview.binder.OverviewEpBinder
import com.xiaoyv.bangumi.ui.media.detail.overview.binder.OverviewGridBinder
import com.xiaoyv.bangumi.ui.media.detail.overview.binder.OverviewPreviewBinder
import com.xiaoyv.bangumi.ui.media.detail.overview.binder.OverviewRatingBinder
import com.xiaoyv.bangumi.ui.media.detail.overview.binder.OverviewRelativeBinder
import com.xiaoyv.bangumi.ui.media.detail.overview.binder.OverviewSaveBinder
import com.xiaoyv.bangumi.ui.media.detail.overview.binder.OverviewSummaryBinder
import com.xiaoyv.bangumi.ui.media.detail.overview.binder.OverviewTagBinder
import com.xiaoyv.common.api.parser.entity.MediaCommentEntity
import com.xiaoyv.common.api.parser.entity.MediaDetailEntity
import com.xiaoyv.common.config.bean.SampleAvatar
import com.xiaoyv.common.helper.callback.RecyclerItemTouchedListener

/**
 * Class: [OverviewAdapter]
 *
 * @author why
 * @since 11/30/23
 */
class OverviewAdapter(
    touchedListener: RecyclerItemTouchedListener,
    onClickSave: (Item, Int) -> Unit,
    onClickEpItem: (MediaDetailEntity.MediaProgress) -> Unit,
    onClickCrtItem: (MediaDetailEntity.MediaCharacter) -> Unit,
    onClickTagItem: (MediaDetailEntity.MediaTag) -> Unit,
    onClickRelatedItem: (MediaDetailEntity.MediaRelative) -> Unit,
    onClickCollectorItem: (SampleAvatar) -> Unit,
    onClickIndexItem: (SampleAvatar) -> Unit,
    onClickCommentItem: (MediaCommentEntity) -> Unit,
    onClickCommentUser: (MediaCommentEntity) -> Unit,
) : BaseMultiItemAdapter<OverviewAdapter.Item>() {

    init {
        val gridPool = RecyclerView.RecycledViewPool()

        this.addItemType(TYPE_SAVE, OverviewSaveBinder(onClickSave))
            .addItemType(TYPE_EP, OverviewEpBinder(touchedListener, onClickEpItem))
            .addItemType(TYPE_TAG, OverviewTagBinder(onClickTagItem))
            .addItemType(TYPE_SUMMARY, OverviewSummaryBinder(true))
            .addItemType(TYPE_PREVIEW, OverviewPreviewBinder())
            .addItemType(TYPE_DETAIL, OverviewSummaryBinder(false))
            .addItemType(TYPE_RATING, OverviewRatingBinder())
            .addItemType(TYPE_CHARACTER, OverviewCharacterBinder(touchedListener, onClickCrtItem))
            .addItemType(TYPE_RELATIVE, OverviewRelativeBinder(touchedListener, onClickRelatedItem))
            .addItemType(
                TYPE_COLLECTOR,
                OverviewGridBinder(gridPool, touchedListener, onClickCollectorItem)
            )
            .addItemType(
                TYPE_INDEX,
                OverviewGridBinder(gridPool, touchedListener, onClickIndexItem)
            )
            .addItemType(
                TYPE_COMMENT,
                OverviewCommentBinder(touchedListener, onClickCommentItem, onClickCommentUser)
            )
            .onItemViewType(OnItemViewTypeListener { position, list ->
                return@OnItemViewTypeListener list[position].type
            })
    }

    companion object {
        const val TYPE_SAVE = 1
        const val TYPE_EP = 2
        const val TYPE_TAG = 3
        const val TYPE_SUMMARY = 4
        const val TYPE_PREVIEW = 5
        const val TYPE_DETAIL = 6
        const val TYPE_RATING = 7
        const val TYPE_CHARACTER = 8
        const val TYPE_RELATIVE = 9
        const val TYPE_COLLECTOR = 10
        const val TYPE_INDEX = 11
        const val TYPE_COMMENT = 12
    }

    data class Item(
        var entity: Any,
        var type: Int,
        var title: String
    )
}