package ani.saikou.parsers.manga.sources

import ani.saikou.parsers.ShowResponse
import ani.saikou.parsers.manga.MangaChapter
import ani.saikou.parsers.manga.MangaImage
import ani.saikou.parsers.manga.MangaParser

class MangaDummy : MangaParser() {

    override val name = "Dummy"
    override val saveName = "manga_dummy"
    override val hostUrl = "https://example.com"

    override suspend fun loadChapters(
        mangaLink: String,
        extra: Map<String, String>?
    ): List<MangaChapter> {
        TODO("Not yet implemented")
    }

    override suspend fun loadImages(chapterLink: String): List<MangaImage> {
        TODO("Not yet implemented")
    }

    override suspend fun search(query: String): List<ShowResponse> {
        TODO("Not yet implemented")
    }
}