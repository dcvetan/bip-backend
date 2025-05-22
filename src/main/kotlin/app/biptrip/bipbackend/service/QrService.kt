package app.biptrip.bipbackend.service

import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.client.j2se.MatrixToImageWriter
import com.google.zxing.common.BitMatrix
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import java.io.File
import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.Path
import java.util.*

@Service
class QrService(
        private val webClient: WebClient,

        @Value("\${secrets.api-key.imgbb}")
        private val apiKey: String,
) {

    fun generateQrCode(url: String, width: Int = 300, height: Int = 300): String {
        val bitMatrix: BitMatrix = MultiFormatWriter().encode(
                url,
                BarcodeFormat.QR_CODE,
                width,
                height
        )

        val filePath = "qr_code_${UUID.randomUUID()}.png"

        val path: Path = FileSystems.getDefault().getPath(filePath)
        MatrixToImageWriter.writeToPath(bitMatrix, "PNG", path)

        val imageUrl = uploadImage(File(filePath)).block()

        deleteFileIfExists(filePath)

        return imageUrl!!
    }

    data class ImgBBResponse(
            val data: ImgBBData,
            val success: Boolean,
            val status: Int
    )

    data class ImgBBData(
            val id: String,
            val title: String,
            val url_viewer: String,
            val url: String,
            val display_url: String,
            val width: Int,
            val height: Int,
            val size: Int,
            val time: String,
            val expiration: String,
            val image: ImgBBImage,
            val thumb: ImgBBThumb,
            val delete_url: String
    )

    data class ImgBBImage(
            val filename: String,
            val name: String,
            val mime: String,
            val extension: String,
            val url: String
    )

    data class ImgBBThumb(
            val filename: String,
            val name: String,
            val mime: String,
            val extension: String,
            val url: String
    )

    fun uploadImage(imageFile: File): Mono<String> {
        return try {
            val imageBytes = imageFile.readBytes()
            val base64Image = Base64.getEncoder().encodeToString(imageBytes)

            val formData: MultiValueMap<String, String> = LinkedMultiValueMap()
            formData.add("key", apiKey)
            formData.add("image", base64Image)
            formData.add("name", imageFile.nameWithoutExtension)

            webClient.post()
                .uri("https://api.imgbb.com/1/upload")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData(formData))
                .retrieve()
                .bodyToMono(ImgBBResponse::class.java)
                .map { response ->
                    if (response.success) {
                        response.data.url
                    } else {
                        throw RuntimeException("Upload failed with status: ${response.status}")
                    }
                }
                .doOnError { error ->
                    println("Error uploading image: ${error.message}")
                }
        } catch (e: Exception) {
            Mono.error(RuntimeException("Failed to read image file: ${e.message}"))
        }
    }


    private fun deleteFileIfExists(filePath: String) {
        val path = Path.of(filePath)
        Files.deleteIfExists(path)
    }

}