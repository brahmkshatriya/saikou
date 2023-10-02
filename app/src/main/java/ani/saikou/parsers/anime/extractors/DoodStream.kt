package ani.saikou.parsers.anime.extractors

import android.net.Uri
import ani.saikou.FileUrl
import ani.saikou.client
import ani.saikou.findBetween
import ani.saikou.getSize
import ani.saikou.parsers.anime.Video
import ani.saikou.parsers.anime.VideoContainer
import ani.saikou.parsers.anime.VideoExtractor
import ani.saikou.parsers.anime.VideoServer
import ani.saikou.parsers.anime.VideoType
import java.util.*

class DoodStream(override val server: VideoServer) : VideoExtractor() {
    override suspend fun extract(): VideoContainer {
        val domain = "https://" + Uri.parse(server.embed.url).host!!
        val res = client.get(server.embed.url).text
        val hash = res.findBetween("/pass_md5/", "'")!!
        val token = res.findBetween("token=", "&")!!
        val url = client.get("$domain/pass_md5/$hash", referer = domain).text
        val link = FileUrl("$url?token=$token&expiry=${Date().time}}", mapOf("referer" to domain))
        return VideoContainer(
            listOf(Video(null, VideoType.CONTAINER, link, getSize(link)))
        )
    }
}