package ani.saikou.parsers.anime.extractors

import ani.saikou.client
import ani.saikou.others.JsUnpacker
import ani.saikou.parsers.anime.Video
import ani.saikou.parsers.anime.VideoContainer
import ani.saikou.parsers.anime.VideoExtractor
import ani.saikou.parsers.anime.VideoServer
import ani.saikou.parsers.anime.VideoType

class ALions(override val server: VideoServer) : VideoExtractor() {
    override suspend fun extract(): VideoContainer {
        val player = client.get(server.embed.url).document.html()
        val script = Regex("<script type=\"text/javascript\">(eval.+)\n</script>").find(player)!!.groups[1]!!.value // I could do this with nice-http html parser, but that's too much effort and regex only needs 3ms

        val url = Regex("file:\"([^\"]+)\"\\}").find(JsUnpacker(script).unpack()!!)!!.groups[1]!!.value
        return VideoContainer(listOf(Video(null, VideoType.M3U8, url)))
    }
}