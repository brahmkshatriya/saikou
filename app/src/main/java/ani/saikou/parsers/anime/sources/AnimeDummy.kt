package ani.saikou.parsers.anime.sources

import ani.saikou.parsers.ShowResponse
import ani.saikou.parsers.anime.AnimeParser
import ani.saikou.parsers.anime.Episode
import ani.saikou.parsers.anime.VideoServer

class AnimeDummy : AnimeParser() {

    override val name = "Dummy"
    override val saveName = "anime_dummy"
    override val hostUrl = "https://example.com"
    override val isDubAvailableSeparately = false

    override suspend fun loadEpisodes(
        animeLink: String,
        extra: Map<String, String>?
    ): List<Episode> {
        TODO("Not yet implemented")
    }

    override suspend fun loadVideoServers(
        episodeLink: String,
        extra: Map<String, String>?
    ): List<VideoServer> {
        TODO("Not yet implemented")
    }

    override suspend fun search(query: String): List<ShowResponse> {
        TODO("Not yet implemented")
    }
}