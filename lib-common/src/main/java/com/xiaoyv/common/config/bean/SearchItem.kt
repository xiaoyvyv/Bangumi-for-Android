package com.xiaoyv.common.config.bean

import android.os.Parcelable
import androidx.annotation.Keep
import com.xiaoyv.common.config.annotation.BgmPathType
import com.xiaoyv.common.helper.callback.IdEntity
import kotlinx.parcelize.Parcelize

/**
 * Class: [SearchItem]
 */
@Keep
@Parcelize
data class SearchItem(
    var label: String,
    override var id: String,
    @BgmPathType
    var pathType: String,
    var keyword: String = "",
    var timestamp: Long = System.currentTimeMillis(),
    var forSelectedMedia: Boolean = false,
) : IdEntity, Parcelable