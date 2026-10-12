package com.anilynime.app

import com.google.gson.annotations.SerializedName

data class AnimeItem(
    @SerializedName("title") val title: String,
    @SerializedName("slug") val slug: String,
    @SerializedName("thumbnail") val thumbnail: String?,
    @SerializedName("type") val type: String?,
    @SerializedName("year") val year: String?,
    @SerializedName("episode") val episode: String?,
    @SerializedName("latestTime") val latestTime: String?
)

data class AnimeListResponse(
    @SerializedName("page") val page: Int,
    @SerializedName("totalItems") val totalItems: Int,
    @SerializedName("items") val items: List<AnimeItem>?
)

data class EmbedItem(
    @SerializedName("server") val server: String,
    @SerializedName("hostType") val hostType: String?,
    @SerializedName("iframe") val iframe: String,
    @SerializedName("streamUrl") val streamUrl: String?
)

data class EpisodeNav(
    @SerializedName("prev") val prev: String?,
    @SerializedName("next") val next: String?
)

data class EpisodeDetailResponse(
    @SerializedName("title") val title: String,
    @SerializedName("animeSlug") val animeSlug: String?,
    @SerializedName("navigation") val navigation: EpisodeNav?,
    @SerializedName("embeds") val embeds: List<EmbedItem>?
)

data class EpisodeInfo(
    @SerializedName("title") val title: String,
    @SerializedName("episode") val episode: String?,
    @SerializedName("slug") val slug: String
)

data class AnimeDetailResponse(
    @SerializedName("title") val title: String,
    @SerializedName("thumbnail") val thumbnail: String?,
    @SerializedName("synopsis") val synopsis: String?,
    @SerializedName("metadata") val metadata: Map<String, String>?,
    @SerializedName("episodes") val episodes: List<EpisodeInfo>?
)
