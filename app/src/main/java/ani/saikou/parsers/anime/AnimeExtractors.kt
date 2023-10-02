package ani.saikou.parsers.anime

import android.net.Uri
import ani.saikou.parsers.anime.extractors.*

object AnimeExtractors {

    fun getExtractor(name: String, server: VideoServer): VideoExtractor? {
        var domain = Uri.parse(server.embed.url).host ?: return null
        if (domain.startsWith("www.")) {domain = domain.substring(4)}

        val extractor: VideoExtractor? = when (domain) {
            "filemoon.to", "filemoon.sx"  -> FileMoon(server)
            "rapid-cloud.co"              -> RapidCloud(server)
            "streamtape.com"              -> StreamTape(server)
            "vidstream.pro"               -> VidStreaming(server)
            "mp4upload.com"               -> Mp4Upload(server)
            "playtaku.net","goone.pro"    -> GogoCDN(server)
            "alions.pro"                  -> ALions(server)
            "awish.pro"                   -> AWish(server)
            "dood.wf"                     -> DoodStream(server)
            "ok.ru"                       -> OkRu(server)
            else                          -> {
                println("$name : No extractor found for: $domain | ${server.embed.url}")
                null
            }
        }
        return extractor
    }
}