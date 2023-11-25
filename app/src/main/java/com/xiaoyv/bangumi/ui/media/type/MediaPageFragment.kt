package com.xiaoyv.bangumi.ui.media.type

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.GridLayoutManager
import com.chad.library.adapter.base.QuickAdapterHelper
import com.chad.library.adapter.base.loadState.LoadState
import com.chad.library.adapter.base.loadState.trailing.TrailingLoadStateAdapter
import com.xiaoyv.bangumi.databinding.FragmentMediaPageBinding
import com.xiaoyv.bangumi.ui.media.MediaFragment
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModelFragment
import com.xiaoyv.blueprint.constant.NavKey
import com.xiaoyv.blueprint.kts.toJson
import com.xiaoyv.common.config.bean.MediaTab
import com.xiaoyv.common.kts.GoogleAttr
import com.xiaoyv.common.kts.debugLog
import com.xiaoyv.widget.kts.getAttrColor
import com.xiaoyv.widget.kts.getParcelObj

/**
 * Class: [MediaPageFragment]
 *
 * @author why
 * @since 11/24/23
 */
class MediaPageFragment : BaseViewModelFragment<FragmentMediaPageBinding, MediaPageViewModel>() {

    private val contentAdapter by lazy { MediaPageAdapter() }

    private val adapterHelper by lazy {
        QuickAdapterHelper.Builder(contentAdapter)
            .setTrailingLoadStateAdapter(object : TrailingLoadStateAdapter.OnTrailingListener {
                override fun isAllowLoading(): Boolean {
                    return binding.srlRefresh.isRefreshing.not()
                }

                override fun onFailRetry() {
                    viewModel.loadMore()
                }

                override fun onLoad() {
                    viewModel.loadMore()
                }
            })
            .build()
    }

    private val mediaFragment
        get() = parentFragment as? MediaFragment

    private val layoutManager: GridLayoutManager?
        get() = binding.rvContent.layoutManager as? GridLayoutManager

    override fun initArgumentsData(arguments: Bundle) {
        viewModel.mediaTable = arguments.getParcelObj(NavKey.KEY_PARCELABLE)
    }

    override fun initView() {
        binding.srlRefresh.initRefresh()
        binding.srlRefresh.setColorSchemeColors(hostActivity.getAttrColor(GoogleAttr.colorPrimary))
    }

    override fun initData() {
        binding.rvContent.adapter = adapterHelper.adapter
        binding.srlRefresh.isRefreshing = true

        viewModel.refresh()
    }

    override fun initListener() {
        binding.srlRefresh.setOnRefreshListener {
            adapterHelper.trailingLoadState = LoadState.None
            viewModel.refresh()
        }
    }

    override fun LifecycleOwner.initViewObserver() {
        viewModel.onBrowserRankLiveData.observe(this) {
            debugLog { it.toJson(true) }

            contentAdapter.submitList(it.orEmpty()) {
                if (viewModel.isRefresh) {
                    layoutManager?.scrollToPositionWithOffset(0, 0)
                }

                adapterHelper.trailingLoadState = viewModel.loadingMoreState
            }
        }

        viewModel.onBrowserOptionLiveData.observe(this) {
            mediaFragment?.refreshOptions(it.orEmpty())
        }
    }

    override fun onResumeExceptFirst() {
        mediaFragment?.refreshOptions(viewModel.onBrowserOptionLiveData.value)
    }

    companion object {
        fun newInstance(mediaTab: MediaTab): MediaPageFragment {
            return MediaPageFragment().apply {
                arguments = bundleOf(NavKey.KEY_PARCELABLE to mediaTab)
            }
        }
    }
}