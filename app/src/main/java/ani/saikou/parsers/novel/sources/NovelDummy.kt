package ani.saikou.parsers.novel.sources

import ani.saikou.parsers.ShowResponse
import ani.saikou.parsers.novel.Book
import ani.saikou.parsers.novel.NovelParser

class NovelDummy : NovelParser(){

    override val name = "Dummy"
    override val saveName = "novel_dummy"
    override val hostUrl = "https://example.com"
    override val volumeRegex: Regex = Regex("")

    override suspend fun loadBook(link: String, extra: Map<String, String>?): Book {
        TODO("Not yet implemented")
    }

    override suspend fun search(query: String): List<ShowResponse> {
        TODO("Not yet implemented")
    }

}