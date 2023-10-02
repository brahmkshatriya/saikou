package ani.saikou.parsers.novel

import ani.saikou.Lazier
import ani.saikou.lazyList
import ani.saikou.parsers.BaseParser
import ani.saikou.parsers.NovelReadSources
import ani.saikou.parsers.novel.sources.NovelDummy

object NovelSources : NovelReadSources() {
    override val list: List<Lazier<BaseParser>> = lazyList(
        "Dummy" to ::NovelDummy,
    )
}