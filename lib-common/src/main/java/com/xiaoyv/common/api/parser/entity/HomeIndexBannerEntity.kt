package com.xiaoyv.common.api.parser.entity

import android.os.Parcelable
import androidx.annotation.Keep
import com.xiaoyv.common.config.bean.HomeIndexFeature
import com.xiaoyv.common.config.bean.SampleImageEntity
import kotlinx.parcelize.Parcelize

/**
 * Class: [HomeIndexBannerEntity]
 *
 * @author why
 * @since 11/25/23
 */
@Keep
@Parcelize
data class HomeIndexBannerEntity(
    var banners: List<SampleImageEntity> = emptyList(),
    var features: List<HomeIndexFeature> = emptyList()
) : Parcelable