package ani.saikou.parsers.anime.extractors

import ani.saikou.FileUrl
import ani.saikou.client
import ani.saikou.getSize
import ani.saikou.others.JsUnpacker
import ani.saikou.parsers.anime.Video
import ani.saikou.parsers.anime.VideoContainer
import ani.saikou.parsers.anime.VideoExtractor
import ani.saikou.parsers.anime.VideoServer
import ani.saikou.parsers.anime.VideoType

private val packedRegex = Regex("""eval\(function\(p,a,c,k,e,.*\)\)""")
private fun getPacked(string: String): String? {
    return packedRegex.find(string)?.value
}

private fun getAndUnpack(string: String): String {
    val packedText = getPacked(string)
    return JsUnpacker(packedText).unpack() ?: string
}
class FileMoon(override val server: VideoServer) : VideoExtractor() {
    override suspend fun extract(): VideoContainer {
        val page = client.get(server.embed.url)
        val unpacked = getAndUnpack(page.text)
        val link = Regex("file:\"(.+?)\"").find(unpacked)?.groupValues?.last()

        link?.let {
            return VideoContainer(
                listOf(
                    Video(
                        null,
                        VideoType.M3U8,
                        FileUrl(link, mapOf("Referer" to server.embed.url)),
                        getSize(link)
                    )
                )
            )
        }
        return VideoContainer(listOf())

    }

}