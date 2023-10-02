package ani.saikou.parsers.anime.extractors

import ani.saikou.asyncMap
import ani.saikou.client
import ani.saikou.getSize
import ani.saikou.parsers.anime.Video
import ani.saikou.parsers.anime.VideoContainer
import ani.saikou.parsers.anime.VideoExtractor
import ani.saikou.parsers.anime.VideoServer
import ani.saikou.parsers.anime.VideoType
import ani.saikou.tryWithSuspend
import kotlinx.serialization.Serializable

class FPlayer(override val server: VideoServer) : VideoExtractor() {

    override suspend fun extract(): VideoContainer {
        val url = server.embed.url
        val apiLink = url.replace("/v/", "/api/source/")
        return  tryWithSuspend {
            val json = client.post(apiLink, referer = url).parsed<Json>()
            if (json.success) {
                VideoContainer(json.data?.asyncMap {
                    Video(
                        it.label.replace("p", "").toIntOrNull(),
                        VideoType.CONTAINER,
                        it.file,
                        getSize(it.file)
                    )
                }?: listOf())
            } else null
        } ?: VideoContainer(listOf())
    }

    @Serializable
    private data class Data(
        val file: String,
        val label: String
    )

    @Serializable
    private data class Json(
        val success: Boolean,
        val data: List<Data>?
    )
}