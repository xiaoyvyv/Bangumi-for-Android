package com.xiaoyv.common.api.parser.entity

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

/**
 * Class: [MediaReviewBlogEntity]
 *
 * @author why
 * @since 11/29/23
 */
@Parcelize
@Keep
data class MediaReviewBlogEntity(
    var id: String = "",
    var title: String = "",
    var avatar: String = "",
    var userName: String = "",
    var userId: String = "",
    var time: String = "",
    var comment: String = "",
    var commentCount: Int = 0
) : Parcelable
