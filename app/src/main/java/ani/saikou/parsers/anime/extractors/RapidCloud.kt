package ani.saikou.parsers.anime.extractors

import android.util.Base64
import android.net.Uri
import ani.saikou.*
import ani.saikou.parsers.*
import ani.saikou.parsers.anime.Subtitle
import ani.saikou.parsers.anime.Video
import ani.saikou.parsers.anime.VideoContainer
import ani.saikou.parsers.anime.VideoExtractor
import ani.saikou.parsers.anime.VideoServer
import ani.saikou.parsers.anime.VideoType
import kotlinx.serialization.Serializable
import org.json.JSONArray
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.util.*
import java.util.concurrent.*
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

@Suppress("BlockingMethodInNonBlockingContext")
class RapidCloud(override val server: VideoServer) : VideoExtractor() {

    override suspend fun extract(): VideoContainer {
        val videos = mutableListOf<Video>()
        val subtitles = mutableListOf<Subtitle>()
        val decryptKey = decryptKey()

        if (decryptKey.isNotEmpty()) {
            val embedURL = Uri.parse(server.embed.url);
            val id = embedURL.path?.substringAfterLast("/");
            val jsonLink = "https://${embedURL.host}/ajax/embed-6-v2/getSources?id=${id}"
            val response = client.get(jsonLink)

            val sourceObject = if (response.text.contains("encrypted")) {
                val encryptedMap = response.parsedSafe<SourceResponse.Encrypted>()
                var sources = encryptedMap?.sources

                if (sources == null || encryptedMap?.encrypted == false)
                    response.parsedSafe()
                else {
                    var sourcesArray = sources.split("").toMutableList()
                    var key = "";
                    for (index in decryptKey) {
                        for(i in (index[0] + 1) until (index[1] + 1)){
                            key += sourcesArray[i];
                            sourcesArray[i] = "";
                        }
                    }
                    
                    sources = sourcesArray.joinToString("");
                    val decrypted = decryptMapped<List<SourceResponse.Track>>(sources, key)
                    SourceResponse(sources = decrypted, tracks = encryptedMap?.tracks)
                }
            }
            else response.parsedSafe()

            sourceObject?.sources?.forEach {
                videos.add(Video(0, VideoType.M3U8, FileUrl(it.file ?: return@forEach)))
            }
            sourceObject?.sourcesBackup?.forEach {
                videos.add(Video(0, VideoType.M3U8, FileUrl(it.file ?: return@forEach), extraNote = "Backup"))
            }
            sourceObject?.tracks?.forEach {
                if (it.kind == "captions" && it.label != null && it.file != null)
                    subtitles.add(Subtitle(it.label, it.file))
            }
        }

        return VideoContainer(videos, subtitles)
    }

    companion object {
        private suspend fun decryptKey(): Array<Array<Int>> {
            val keyJSON = JSONArray(client.get("https://raw.githubusercontent.com/enimax-anime/key/e0/key.txt").text);
            val key = Array(keyJSON.length()) { i ->
                val innerArray = keyJSON.getJSONArray(i)
                Array(innerArray.length()) { j ->
                    innerArray.getInt(j)
                }
            }
            return key;
        }

        private fun md5(input: ByteArray): ByteArray {
            return MessageDigest.getInstance("MD5").digest(input)
        }

        private fun generateKey(salt: ByteArray, secret: ByteArray): ByteArray {
            var key = md5(secret + salt)
            var currentKey = key
            while (currentKey.size < 48) {
                key = md5(key + secret + salt)
                currentKey += key
            }
            return currentKey
        }

        private fun decryptSourceUrl(decryptionKey: ByteArray, sourceUrl: String): String {
            val cipherData = Base64.decode(sourceUrl, Base64.DEFAULT)
            val encrypted = cipherData.copyOfRange(16, cipherData.size)
            val aesCBC = Cipher.getInstance("AES/CBC/PKCS5Padding")

            Objects.requireNonNull(aesCBC).init(
                Cipher.DECRYPT_MODE, SecretKeySpec(
                    decryptionKey.copyOfRange(0, 32),
                    "AES"
                ),
                IvParameterSpec(decryptionKey.copyOfRange(32, decryptionKey.size))
            )
            val decryptedData = aesCBC!!.doFinal(encrypted)
            return String(decryptedData, StandardCharsets.UTF_8)
        }

        private inline fun <reified T> decryptMapped(input: String, key: String): T? {
            return Mapper.parse(decrypt(input, key))
        }

        private fun decrypt(input: String, key: String): String {
            return decryptSourceUrl(
                generateKey(
                    Base64.decode(input, Base64.DEFAULT).copyOfRange(8, 16),
                    key.toByteArray()
                ), input
            )
        }
    }


    @Serializable
    private data class SourceResponse(
        val encrypted: Boolean? = null,
        val sources: List<Track>? = null,
        val sourcesBackup: List<Track>? = null,
        val tracks: List<Track>? = null
    ) {
        @Serializable
        data class Encrypted (
            val sources: String? = null,
            val sourcesBackup: String? = null,
            val tracks: List<Track>? = null,
            val encrypted: Boolean? = null,
            val server: Long? = null
        )

        @Serializable
        data class Track (
            val file: String? = null,
            val label: String? = null,
            val kind: String? = null,
            val default: Boolean? = null
        )
    }

}