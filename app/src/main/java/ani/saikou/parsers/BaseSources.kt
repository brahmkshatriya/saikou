package ani.saikou.parsers

import ani.saikou.Lazier
import ani.saikou.media.anime.Episode
import ani.saikou.media.manga.MangaChapter
import ani.saikou.media.Media
import ani.saikou.parsers.anime.AnimeParser
import ani.saikou.parsers.manga.MangaParser
import ani.saikou.parsers.novel.NovelParser
import ani.saikou.tryWithSuspend

abstract class WatchSources : BaseSources() {

    override operator fun get(i: Int): AnimeParser {
        return (list.getOrNull(i)?:list[0]).get.value as AnimeParser
    }

    suspend fun loadEpisodesFromMedia(i: Int, media: Media): MutableMap<String, Episode> {
        return tryWithSuspend(true) {
            val res = get(i).autoSearch(media) ?: return@tryWithSuspend mutableMapOf()
            loadEpisodes(i, res.link, res.extra)
        } ?: mutableMapOf()
    }

    suspend fun loadEpisodes(i: Int, showLink: String, extra: Map<String, String>?): MutableMap<String, Episode> {
        val map = mutableMapOf<String, Episode>()
        val parser = get(i)
        tryWithSuspend(true) {
            parser.loadEpisodes(showLink,extra).forEach {
                map[it.number] = Episode(it.number, it.link, it.title, it.description, it.thumbnail, it.isFiller, extra = it.extra)
            }
        }
        return map
    }

}

abstract class MangaReadSources : BaseSources() {

    override operator fun get(i: Int): MangaParser {
        return (list.getOrNull(i)?:list[0]).get.value as MangaParser
    }

    suspend fun loadChaptersFromMedia(i: Int, media: Media): MutableMap<String, MangaChapter> {
        return tryWithSuspend(true) {
            val res = get(i).autoSearch(media) ?: return@tryWithSuspend mutableMapOf()
            loadChapters(i, res)
        } ?: mutableMapOf()
    }

    suspend fun loadChapters(i: Int, show: ShowResponse): MutableMap<String, MangaChapter> {
        val map = mutableMapOf<String, MangaChapter>()
        val parser = get(i)
        tryWithSuspend(true) {
            parser.loadChapters(show.link, show.extra).forEach {
                map[it.number] = MangaChapter(it)
            }
        }
        return map
    }
}

abstract class NovelReadSources : BaseSources(){
        override operator fun get(i: Int): NovelParser {
            return (list.getOrNull(i)?:list[0]).get.value as NovelParser
        }
}

abstract class BaseSources {
    abstract val list: List<Lazier<BaseParser>>

    val names: List<String> get() = list.map { it.name }

    fun flushText() {
        list.forEach {
            if (it.get.isInitialized())
                it.get.value.showUserText = ""
        }
    }

    open operator fun get(i: Int): BaseParser {
        return list[i].get.value
    }

    fun saveResponse(i: Int, mediaId: Int, response: ShowResponse) {
        get(i).saveShowResponse(mediaId, response, true)
    }
}



