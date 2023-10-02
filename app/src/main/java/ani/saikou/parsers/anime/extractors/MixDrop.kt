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

//https://github.com/recloudstream/cloudstream/blob/master/app/src/main/java/com/lagradost/cloudstream3/extractors/MixDrop.kt

private val packedRegex = Regex("""eval\(function\(p,a,c,k,e,.*\)\)""")
private fun getPacked(string: String): String? {
    return packedRegex.find(string)?.value
}

private fun getAndUnpack(string: String): String {
    val packedText = getPacked(string)
    return JsUnpacker(packedText).unpack() ?: string
}

private fun httpsify(url: String?): String? {
    return if (url?.startsWith("//") == true) "https:$url" else url
}

class MixDrop(override val server: VideoServer) : VideoExtractor() {
    override suspend fun extract(): VideoContainer {
        val page = client.get(server.embed.url)
        val unpacked = getAndUnpack(page.text)
        val link = httpsify(Regex("""wurl.*?=.*?"(.*?)";""").find(unpacked)?.groupValues?.last())

        link?.let {
            return VideoContainer(
                listOf(
                    Video(
                        null,
                        VideoType.CONTAINER,
                        FileUrl(link, mapOf("Referer" to server.embed.url)),
                        getSize(link)
                    )
                )
            )
        }
        return VideoContainer(listOf())

    }

}