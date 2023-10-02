package ani.saikou.parsers.manga

import ani.saikou.Lazier
import ani.saikou.lazyList
import ani.saikou.parsers.BaseParser
import ani.saikou.parsers.MangaReadSources
import ani.saikou.parsers.manga.sources.MangaDummy

object MangaSources : MangaReadSources() {
    override val list: List<Lazier<BaseParser>> = lazyList(
        "Dummy" to ::MangaDummy,
    )
}

object HMangaSources : MangaReadSources() {
    private val aList: List<Lazier<BaseParser>> = lazyList(
//        Here comes hentai manga parsers
    )
    override val list = listOf(aList, MangaSources.list).flatten()
}
