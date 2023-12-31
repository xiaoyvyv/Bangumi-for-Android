package com.xiaoyv.bangumi.ui.feature.summary

import androidx.lifecycle.MutableLiveData
import com.blankj.utilcode.util.EncryptUtils
import com.kunminx.architecture.ui.callback.UnPeekLiveData
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModel
import com.xiaoyv.blueprint.kts.launchUI
import com.xiaoyv.common.api.BgmApiManager
import com.xiaoyv.common.api.parser.parseHtml
import com.xiaoyv.common.api.response.BaiduTranslateEntity
import com.xiaoyv.common.helper.CacheHelper
import com.xiaoyv.common.helper.ConfigHelper
import com.xiaoyv.common.kts.randId
import com.xiaoyv.widget.kts.errorMsg
import com.xiaoyv.widget.kts.showToastCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Class: [SummaryViewModel]
 *
 * @author why
 * @since 12/10/23
 */
class SummaryViewModel : BaseViewModel() {
    internal var summary: Array<out String> = emptyArray()
    internal var summaryOriginal = MutableLiveData<List<CharSequence>?>()
    internal var summaryTranslate = MutableLiveData<String>()
    internal var isShowOriginal = true

    internal val onNeedConfig = UnPeekLiveData<Unit>()

    /**
     * 待翻译的文本
     */
    private val needTranslateText: String
        get() = summary.joinToString("\n") {
            it.parseHtml().toString().trim()
        }

    private val cacheKey: String
        get() = EncryptUtils.encryptMD5ToString(needTranslateText)

    /**
     * 直接刷新
     */
    fun showOriginal() {
        launchUI(stateView = loadingViewState) {
            isShowOriginal = true
            summaryOriginal.value = withContext(Dispatchers.IO) {
                summary.map { it.parseHtml(true) }
            }
        }
    }

    fun shoTranslate() {
        launchUI(
            stateView = loadingViewState,
            error = {
                it.printStackTrace()

                showToastCompat(it.errorMsg)
            },
            block = {
                isShowOriginal = false
                val translate = CacheHelper.readTranslate(cacheKey)
                if (translate.isNotBlank()) {
                    summaryTranslate.value = translate
                    return@launchUI
                }

                val (appId, secret) = ConfigHelper.readBaiduTranslateConfig()
                val salt = randId()
                val sign = generateSign(needTranslateText, appId, secret, salt)

                if (appId.isBlank() || secret.isBlank()) {
                    onNeedConfig.value = Unit
                    return@launchUI
                }

                val result: BaiduTranslateEntity = withContext(Dispatchers.IO) {
                    BgmApiManager.bgmJsonApi.postBaiduTranslate(
                        q = needTranslateText,
                        appId = appId,
                        secret = secret,
                        salt = salt,
                        sign = sign
                    )
                }

                if (!result.errorMsg.isNullOrBlank()) {
                    summaryTranslate.value = result.errorMsg.orEmpty()
                    return@launchUI
                }

                val translateResult = result.transResult.orEmpty()
                    .joinToString("\n") { it.dst.orEmpty() }

                // 缓存结果
                CacheHelper.saveTranslate(cacheKey, translateResult)

                summaryTranslate.value = translateResult
            }
        )
    }

    private fun generateSign(text: String, appId: String, secret: String, salt: String): String {
        return EncryptUtils.encryptMD5ToString("$appId$text$salt$secret").lowercase()
    }
}