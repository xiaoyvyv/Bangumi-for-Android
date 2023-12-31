package com.xiaoyv.bangumi.ui.feature.person.overview

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import com.xiaoyv.bangumi.databinding.FragmentPersonOverviewBinding
import com.xiaoyv.bangumi.helper.RouteHelper
import com.xiaoyv.bangumi.ui.feature.person.PersonViewModel
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModelFragment
import com.xiaoyv.blueprint.constant.NavKey
import com.xiaoyv.blueprint.kts.launchUI
import com.xiaoyv.common.api.parser.entity.PersonEntity
import com.xiaoyv.common.config.annotation.BgmPathType
import com.xiaoyv.common.helper.callback.RecyclerItemTouchedListener
import com.xiaoyv.common.kts.CommonId
import com.xiaoyv.common.kts.forceCast
import com.xiaoyv.common.kts.setOnDebouncedChildClickListener
import com.xiaoyv.common.widget.scroll.AnimeLinearLayoutManager

/**
 * Class: [PersonOverviewFragment]
 *
 * @author why
 * @since 12/4/23
 */
class PersonOverviewFragment :
    BaseViewModelFragment<FragmentPersonOverviewBinding, PersonOverviewViewModel>() {

    private val personViewModel: PersonViewModel by activityViewModels()

    private val itemAdapter by lazy {
        PersonOverviewAdapter(
            touchedListener = RecyclerItemTouchedListener {
                personViewModel.vpEnableLiveData.value = it
            },
            clickSubItem = { type, id ->
                when (type) {
                    BgmPathType.TYPE_USER -> RouteHelper.jumpUserDetail(id)
                    BgmPathType.TYPE_PERSON -> RouteHelper.jumpPerson(id, false)
                    BgmPathType.TYPE_CHARACTER -> RouteHelper.jumpPerson(id, true)
                    BgmPathType.TYPE_INDEX -> RouteHelper.jumpIndexDetail(id)
                    BgmPathType.TYPE_SUBJECT -> RouteHelper.jumpMediaDetail(id)
                }
            }
        )
    }

    override fun initArgumentsData(arguments: Bundle) {
        viewModel.personId = arguments.getString(NavKey.KEY_STRING).orEmpty()
        viewModel.isVirtual = arguments.getBoolean(NavKey.KEY_BOOLEAN)
    }

    override fun initView() {

    }

    override fun initData() {
        binding.rvContent.layoutManager =
            AnimeLinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                .apply {
                    extraLayoutSpaceScale = 2f
                }

        binding.rvContent.adapter = itemAdapter
    }

    override fun initListener() {
        // 点击查看更多
        itemAdapter.setOnDebouncedChildClickListener(CommonId.tv_more) {
            when (it.type) {
                PersonOverviewAdapter.TYPE_SUMMARY -> {
                    RouteHelper.jumpSummaryDetail(it.entity.forceCast<PersonEntity>().summaryHtml)
                }

                PersonOverviewAdapter.TYPE_INFOS -> {
                    RouteHelper.jumpSummaryDetail(*it.entity.forceCast<PersonEntity>().infoHtml.toTypedArray())
                }
            }
        }

        // 详情信息点击查看更多
        itemAdapter.setOnDebouncedChildClickListener(CommonId.tv_summary_content) {
            when (it.type) {
                PersonOverviewAdapter.TYPE_SUMMARY -> {
                    RouteHelper.jumpSummaryDetail(it.entity.forceCast<PersonEntity>().summaryHtml)
                }

                PersonOverviewAdapter.TYPE_INFOS -> {
                    RouteHelper.jumpSummaryDetail(*it.entity.forceCast<PersonEntity>().infoHtml.toTypedArray())
                }
            }
        }
    }

    override fun LifecycleOwner.initViewObserver() {
        binding.stateView.initObserver(
            lifecycleOwner = this,
            loadingViewState = personViewModel.loadingViewState,
            loadingBias = 0.2f
        )

        personViewModel.onPersonLiveData.observe(this) {
            val entity = it ?: return@observe

            launchUI {
                itemAdapter.submitList(viewModel.buildBinderList(entity))
            }
        }
    }

    companion object {
        fun newInstance(personId: String, isVirtual: Boolean): PersonOverviewFragment {
            return PersonOverviewFragment().apply {
                arguments = bundleOf(
                    NavKey.KEY_STRING to personId,
                    NavKey.KEY_BOOLEAN to isVirtual
                )
            }
        }
    }
}