package ani.saikou.parsers.anime.extractors

import ani.saikou.client
import ani.saikou.parsers.anime.Video
import ani.saikou.parsers.anime.VideoContainer
import ani.saikou.parsers.anime.VideoExtractor
import ani.saikou.parsers.anime.VideoServer
import ani.saikou.parsers.anime.VideoType

class AWish(override val server: VideoServer) : VideoExtractor() {
    override suspend fun extract(): VideoContainer {
        val player = client.get(server.embed.url).document.html()

        val url = Regex("file:\"([^\"]+)\"\\}").find(player)!!.groups[1]!!.value
        return VideoContainer(listOf(Video(null, VideoType.M3U8, url)))
    }
}