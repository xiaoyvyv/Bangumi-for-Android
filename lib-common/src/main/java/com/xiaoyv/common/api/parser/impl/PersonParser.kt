package com.xiaoyv.common.api.parser.impl

import com.xiaoyv.common.api.parser.entity.CharacterEntity
import com.xiaoyv.common.api.parser.entity.MediaDetailEntity
import com.xiaoyv.common.api.parser.entity.PersonEntity
import com.xiaoyv.common.api.parser.fetchStyleBackgroundUrl
import com.xiaoyv.common.api.parser.hrefId
import com.xiaoyv.common.api.parser.optImageUrl
import com.xiaoyv.common.api.parser.parseCount
import com.xiaoyv.common.api.parser.parseHtml
import com.xiaoyv.common.api.parser.parseStar
import com.xiaoyv.common.api.parser.parserTime
import com.xiaoyv.common.api.parser.requireNoError
import com.xiaoyv.common.config.annotation.MediaType
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

/**
 * @author why
 * @since 12/4/23
 */
fun Document.parserPerson(personId: String, isVirtual: Boolean): PersonEntity {
    requireNoError()

    val entity = PersonEntity(id = personId, isVirtual = isVirtual)

    select("#headerSubject").apply {
        entity.nameCn = select("small").text()
        select(".nameSingle a").apply {
            entity.nameNative = text()

            if (entity.nameCn.isBlank()) {
                entity.nameCn = attr("title")
            }
        }

        select(".collect a").apply {
            if (isEmpty()) {
                entity.isCollected = false
            } else {
                attr("href").apply {
                    entity.gh = substringAfterLast("=")
                    entity.isCollected = contains("erase_collect")
                }
            }
        }
    }

    select("#columnCrtA").apply {
        entity.poster = select("img.cover").attr("src").optImageUrl()
        entity.posterLarge = select("a.cover").attr("href").optImageUrl()
        entity.infoHtml = select(".infobox_container li").map { it.html() }
        entity.infos = select(".infobox_container li").map { item ->
            val subInfo = PersonEntity.SubInfo()
            subInfo.title = item.select(".tip").remove().text().trim().removeSuffix(":")
            subInfo.value = item.text()
            subInfo
        }

        entity.recommendIndexes = select("#subjectPanelIndex .groupsLine > li").map { item ->
            val mediaIndex = MediaDetailEntity.MediaIndex()

            item.select(".innerWithAvatar a.avatar").apply {
                mediaIndex.id = hrefId()
                mediaIndex.title = text()
            }
            item.select(".innerWithAvatar small.grey a").apply {
                mediaIndex.userId = hrefId()
                mediaIndex.userName = text()
            }
            mediaIndex.userAvatar = item.select("li > a.avatar > span")
                .attr("style")
                .fetchStyleBackgroundUrl().optImageUrl()
            mediaIndex
        }

        entity.whoCollects = select("#crtPanelCollect .groupsLine > li").map { item ->
            val who = MediaDetailEntity.MediaWho()
            item.select(".innerWithAvatar a.avatar").apply {
                who.id = hrefId()
                who.name = text()
            }
            who.avatar = item.select("li > a.avatar span")
                .attr("style")
                .fetchStyleBackgroundUrl().optImageUrl()

            who.star = item.parseStar()
            who.time = item.select(".innerWithAvatar small").text()
            who
        }
    }

    select("#columnCrtB").apply {
        entity.job = select("h2").firstOrNull()?.text().orEmpty()
        entity.summaryHtml = select(".detail").html().trim()
        entity.summary = entity.summaryHtml.parseHtml()
    }

    select(".browserList").associateBy {
        it.previousElementSibling()?.text().orEmpty()
    }.forEach { (key, value) ->
        when {
            isVirtual -> {
                entity.performers = value.select("ul.browserList > li").orEmpty().map { item ->
                    val character = MediaDetailEntity.MediaCharacter()
                    val media = MediaDetailEntity.MediaRelative()

                    item.select(".innerLeftItem").apply {
                        media.id = select("a.subjectCover").hrefId()
                        media.cover = select("img.cover").attr("src").optImageUrl()
                        media.titleNative = select("h3").text()
                        media.titleCn = select("small.grey").text()
                        media.type = select(".ico_subject_type").toString().let {
                            when {
                                it.contains("subject_type_1") -> MediaType.TYPE_BOOK
                                it.contains("subject_type_2") -> MediaType.TYPE_ANIME
                                it.contains("subject_type_3") -> MediaType.TYPE_MUSIC
                                it.contains("subject_type_4") -> MediaType.TYPE_GAME
                                it.contains("subject_type_6") -> MediaType.TYPE_REAL
                                else -> MediaType.TYPE_UNKNOWN
                            }
                        }
                        character.jobs = select(".badge_job").map { it.text() }
                    }

                    item.select(".innerRightList").apply {
                        character.id = select("a.avatar").hrefId()
                        character.avatar = select("img.avatar").attr("src").optImageUrl()
                        character.characterName = select("h3 a.l").text()
                        character.characterNameCn = select("h3 a.l").text()
                        character.personJob = select("small.grey").text()
                    }
                    PersonEntity.RecentlyPerformer(character.id, character, media)
                }
            }

            key.contains("最近演出角色") -> {
                entity.recentCharacters = value.parserPersonVoices(personId, false)
            }

            key.contains("最近参与") -> {
                entity.recentOpuses = value.select("ul.browserList > li").orEmpty().map { item ->
                    val opus = PersonEntity.RecentlyOpus()
                    item.select(".innerLeftItem").apply {
                        opus.id = select("a.cover").hrefId()
                        opus.cover = select("img.cover").attr("src").optImageUrl()
                        opus.titleNative = select("h3 a.l").text()
                        opus.jobs = select(".badge_job").map { it.text() }
                        opus.mediaType = select(".ico_subject_type").toString().let {
                            when {
                                it.contains("subject_type_1") -> MediaType.TYPE_BOOK
                                it.contains("subject_type_2") -> MediaType.TYPE_ANIME
                                it.contains("subject_type_3") -> MediaType.TYPE_MUSIC
                                it.contains("subject_type_4") -> MediaType.TYPE_GAME
                                it.contains("subject_type_6") -> MediaType.TYPE_REAL
                                else -> MediaType.TYPE_UNKNOWN
                            }
                        }
                    }
                    opus
                }
            }
        }
    }

    entity.recentCooperates = select(".content_inner ul > li").map { item ->
        val cooperate = PersonEntity.RecentCooperate()
        cooperate.id = item.select("a.avatar").hrefId()
        cooperate.avatar = item.select("a.avatar > span").attr("style")
            .fetchStyleBackgroundUrl().optImageUrl()
        cooperate.name = item.select("p.info").text()
        cooperate.times = item.select("small.fade").text().parseCount()
        cooperate
    }
    entity.comments = parserBottomComment()
    return entity
}

/**
 * 解析收藏者者数据
 */
fun Element.parserPersonCollector(): List<MediaDetailEntity.MediaWho> {
    requireNoError()

    return select("#memberUserList > li.user").map { item ->
        val who = MediaDetailEntity.MediaWho()
        item.select(".userImage img").apply {
            who.avatar = attr("src").optImageUrl()
            who.name = attr("alt")
        }
        who.id = item.select(".userContainer a").hrefId()
        who.time = item.select(".info").text()
        who
    }
}

/**
 * 解析合作者数据
 */
fun Element.parserPersonCooperate(): List<PersonEntity.RecentCooperate> {
    requireNoError()

    return select(".browserCrtList > div").map { item ->
        val cooperate = PersonEntity.RecentCooperate()

        item.select("a.avatar").apply {
            cooperate.avatar = select("img").attr("src").optImageUrl()
            cooperate.id = hrefId()
        }

        item.select("h3").apply {
            cooperate.name = select("a").text()
            cooperate.times = select("small").text().parseCount()
        }

        item.select(".prsn_info").apply {
            cooperate.jobs = select(".badge_job").map { it.text() }
            cooperate.infos = select(".tip").text()
                .split("/")
                .map {
                    it.trim().split("\\s".toRegex()).let { list ->
                        list.getOrNull(0).orEmpty().trim() to list.getOrNull(1).orEmpty().trim()
                    }
                }
            cooperate.opus = select(".subject_tag_section > a").map { subItem ->
                val relative = MediaDetailEntity.MediaRelative()
                relative.id = subItem.hrefId()
                relative.titleCn = subItem.attr("title")
                relative.titleNative = subItem.text()
                relative
            }
        }
        cooperate
    }
}

/**
 * 解析作品条目数据
 */
fun Element.parserPersonOpus(): List<PersonEntity.RecentlyOpus> {
    requireNoError()

    return select("#browserItemList > li").map { item ->
        val opus = PersonEntity.RecentlyOpus()
        opus.id = item.select("a.subjectCover").hrefId()
        opus.cover = item.select("img.cover").attr("src").optImageUrl()
        opus.titleCn = item.select("h3 a").text()
        opus.titleNative = item.select("small.grey").text()
        opus.mediaType = item.select(".ico_subject_type").toString().let {
            when {
                it.contains("subject_type_1") -> MediaType.TYPE_BOOK
                it.contains("subject_type_2") -> MediaType.TYPE_ANIME
                it.contains("subject_type_3") -> MediaType.TYPE_MUSIC
                it.contains("subject_type_4") -> MediaType.TYPE_GAME
                it.contains("subject_type_6") -> MediaType.TYPE_REAL
                else -> MediaType.TYPE_UNKNOWN
            }
        }
        opus.jobs = item.select(".badge_job").map { it.text() }
        opus.rateInfo = item.select(".rateInfo").let { subItem ->
            val rateInfo = PersonEntity.RateInfo()
            rateInfo.rate = subItem.select(".fade").text().toFloatOrNull() ?: 0f
            rateInfo.count = subItem.select(".tip_j").text().parseCount()
            rateInfo
        }
        opus.desc = item.select("p.info").text().substringAfter("/").trim()
        opus.time = item.select("p.info").text().parserTime()
        opus
    }
}

/**
 * 解析角色的条目数据
 */
fun Element.parserPersonVoices(personId: String, isVirtual: Boolean): List<CharacterEntity> {
    requireNoError()

    return select(".browserList > li").map { item ->
        val entity = CharacterEntity()

        item.select(".innerLeftItem").apply {
            entity.id = select("a.avatar").hrefId()
            entity.avatar = select("img.avatar").attr("src").optImageUrl()
            entity.nameNative = select("h3 a.l").text()
            entity.nameCn = select("h3 .tip").text()
        }

        entity.from = item.select(".innerRightList > li").map { subItem ->
            val relative = MediaDetailEntity.MediaRelative()
            relative.id = subItem.select("a.subjectCover").hrefId()
            relative.cover = subItem.select("img.cover").attr("src").optImageUrl()
            relative.titleNative = subItem.select("h3").text()
            relative.titleCn = subItem.select("small.grey").text()
            relative.characterJobs = subItem.select(".badge_job").map { it.text() }
            relative
        }
        entity.comments = item.parserBottomComment()
        entity
    }
}















