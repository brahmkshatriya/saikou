package ani.saikou.parsers.anime.extractors

import ani.saikou.FileUrl
import ani.saikou.client
import ani.saikou.getSize
import ani.saikou.parsers.anime.Video
import ani.saikou.parsers.anime.VideoContainer
import ani.saikou.parsers.anime.VideoExtractor
import ani.saikou.parsers.anime.VideoServer
import ani.saikou.parsers.anime.VideoType

class Mp4Upload(override val server: VideoServer) : VideoExtractor() {
    override suspend fun extract(): VideoContainer {
        val link = client.get(server.embed.url).document
            .select("script").html()
            .substringAfter("src: \"").substringBefore("\"")
        val host = link.substringAfter("https://").substringBefore("/")
        val file = FileUrl(link, mapOf("host" to host))
        return VideoContainer(
            listOf(Video(null, VideoType.CONTAINER, file, getSize(file)))
        )
    }
}