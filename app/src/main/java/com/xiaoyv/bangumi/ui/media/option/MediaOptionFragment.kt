package com.xiaoyv.bangumi.ui.media.option

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.lifecycle.LifecycleOwner
import com.xiaoyv.bangumi.R
import com.xiaoyv.bangumi.databinding.FragmentMediaOptionBinding
import com.xiaoyv.bangumi.ui.media.MediaFragment
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModelFragment
import com.xiaoyv.blueprint.constant.NavKey
import com.xiaoyv.common.config.annotation.BrowserSortType
import com.xiaoyv.common.config.annotation.MediaType
import com.xiaoyv.common.config.bean.MediaOptionConfig
import com.xiaoyv.common.kts.setOnDebouncedChildClickListener

/**
 * Class: [MediaOptionFragment]
 *
 * @author why
 * @since 11/26/23
 */
class MediaOptionFragment :
    BaseViewModelFragment<FragmentMediaOptionBinding, MediaOptionViewModel>() {

    private val optionAdapter by lazy {
        MediaOptionAdapter()
    }

    private val mediaFragment
        get() = parentFragment as? MediaFragment

    override fun initArgumentsData(arguments: Bundle) {
        viewModel.currentMediaType = arguments.getString(NavKey.KEY_STRING).orEmpty()
    }

    override fun initView() {
        binding.rvOptions.adapter = optionAdapter
    }

    override fun initData() {

    }

    override fun LifecycleOwner.initViewObserver() {
        viewModel.onOptionsItemLiveData.observe(this) {
            optionAdapter.submitList(it)

            // 默认值为排名对应 [MediaPageViewModel.mediaType]
            it.find { any -> any is MediaOptionConfig.Config.Option.Item && any.value == BrowserSortType.TYPE_RANK }
                .let { any -> optionAdapter.selectItem(any as MediaOptionConfig.Config.Option.Item) }
        }
    }

    override fun initListener() {
        optionAdapter.setOnDebouncedChildClickListener(R.id.tv_option_item) {
            if (it is MediaOptionConfig.Config.Option.Item) {
                // 增加年份
                if (it.value == MediaOptionConfig.YEAR_UP) {
                    viewModel.yearUp()
                    return@setOnDebouncedChildClickListener
                }
                // 降低年份
                if (it.value == MediaOptionConfig.YEAR_DOWN) {
                    viewModel.yearDown()
                    return@setOnDebouncedChildClickListener
                }

                if (optionAdapter.isSelected(it)) {
                    optionAdapter.unselectItem(it)
                } else {
                    optionAdapter.selectItem(it)
                }
                mediaFragment?.onOptionSelected(viewModel.currentMediaType to optionAdapter.selected)
            }
        }
    }

    companion object {
        fun newInstance(@MediaType mediaType: String): MediaOptionFragment {
            return MediaOptionFragment().apply {
                arguments = bundleOf(NavKey.KEY_STRING to mediaType)
            }
        }
    }
}