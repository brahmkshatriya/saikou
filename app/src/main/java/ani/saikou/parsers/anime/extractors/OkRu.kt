package ani.saikou.parsers.anime.extractors

import ani.saikou.client
import ani.saikou.parsers.anime.Video
import ani.saikou.parsers.anime.VideoContainer
import ani.saikou.parsers.anime.VideoExtractor
import ani.saikou.parsers.anime.VideoServer
import ani.saikou.parsers.anime.VideoType

class OkRu(override val server: VideoServer) : VideoExtractor() {
    override suspend fun extract(): VideoContainer {
        val player = client.get(server.embed.url).document.html()
        val mediaUrl = Regex("https://vd\\d+\\.mycdn\\.me/e[^\\\\]+").find(player)

        return VideoContainer(
            listOf(
                Video(null, VideoType.M3U8, mediaUrl!!.value),
                Video(null, VideoType.DASH, mediaUrl.next()!!.value)
            )
        )
    }
}