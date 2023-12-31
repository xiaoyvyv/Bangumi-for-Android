package com.xiaoyv.bangumi.ui.discover.mono.list

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import androidx.core.view.updatePadding
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.layoutmanager.QuickGridLayoutManager
import com.xiaoyv.bangumi.R
import com.xiaoyv.bangumi.base.BaseListActivity
import com.xiaoyv.bangumi.helper.RouteHelper
import com.xiaoyv.blueprint.constant.NavKey
import com.xiaoyv.common.config.annotation.BgmPathType
import com.xiaoyv.common.config.annotation.MonoOrderByType
import com.xiaoyv.common.config.bean.SampleImageEntity
import com.xiaoyv.common.kts.setOnDebouncedChildClickListener
import com.xiaoyv.common.widget.dialog.filter.FilterHeaderAdapter
import com.xiaoyv.widget.binder.BaseQuickDiffBindingAdapter
import com.xiaoyv.widget.kts.dpi

/**
 * Class: [MonoListActivity]
 *
 * @author why
 * @since 12/21/23
 */
class MonoListActivity : BaseListActivity<SampleImageEntity, MonoListViewModel>() {
    override val isOnlyOnePage: Boolean
        get() = false

    override fun initIntentData(intent: Intent, bundle: Bundle, isNewIntent: Boolean) {
        viewModel.orderByType.value = bundle.getString(NavKey.KEY_STRING).orEmpty()
        viewModel.isCharacter = bundle.getBoolean(NavKey.KEY_BOOLEAN, false)
        viewModel.userId = bundle.getString(NavKey.KEY_STRING_SECOND).orEmpty()
    }

    override fun onCreateLayoutManager(): LinearLayoutManager {
        return QuickGridLayoutManager(requireActivity, 3, GridLayoutManager.VERTICAL, false)
    }

    override fun initView() {
        super.initView()

        binding.rvContent.updatePadding(left = 8.dpi, right = 8.dpi)
    }

    override fun initData() {
        super.initData()

        // 非指定用户收藏才显示筛选
        if (viewModel.userId.isBlank()) {
            adapterHelper.addBeforeAdapter(FilterHeaderAdapter(viewModel.filterMenu).apply {
                onSelectedChangeListener = {
                    viewModel.selectedOptions = it
                    viewModel.refresh()
                }
            })
        }
    }

    override fun initListener() {
        super.initListener()

        contentAdapter.setOnDebouncedChildClickListener(R.id.item_mono) {
            RouteHelper.jumpPerson(it.id, it.type == BgmPathType.TYPE_CHARACTER)
        }
    }

    override fun LifecycleOwner.initViewObserverExt() {
        viewModel.orderByType.observe(this) {
            val monoTypeName = if (viewModel.isCharacter) "角色列表" else "人物列表"

            if (viewModel.userId.isBlank()) {
                binding.toolbar.title =
                    String.format("%s/%s排序", monoTypeName, MonoOrderByType.string(it))
            } else {
                binding.toolbar.title = buildString {
                    append("收藏的")
                    append(monoTypeName)
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        if (viewModel.userId.isBlank()) viewModel.toolbarMenus.forEach { type ->
            menu.add(String.format("按%s排序", MonoOrderByType.string(type)))
                .setOnMenuItemClickListener {
                    viewModel.orderByType.value = type
                    viewModel.refresh()
                    true
                }
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onCreateContentAdapter(): BaseQuickDiffBindingAdapter<SampleImageEntity, *> {
        return MonoListAdapter()
    }
}