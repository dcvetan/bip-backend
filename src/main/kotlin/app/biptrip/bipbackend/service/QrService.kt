package app.biptrip.bipbackend.service

import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.client.j2se.MatrixToImageWriter
import com.google.zxing.common.BitMatrix
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import java.io.File
import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.Path
import java.util.*

@Service
class QrService(
        private val webClient: WebClient,

        @Value("\${secrets.api-key.imgur}")
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

        val imageUrl = uploadToImgur(filePath)

        deleteFileIfExists(filePath)

        return imageUrl!!
    }

    private fun uploadToImgur(filePath: String): String? {
        val file = File(filePath)
        val imageBytes = file.readBytes()
        val encodedImage = Base64.getEncoder().encodeToString(imageBytes)

        val response = webClient.post()
            .uri("https://api.imgur.com/3/image")
            .header("Authorization", "Client-ID $apiKey")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .body(
                    BodyInserters.fromFormData("image", encodedImage)
            )
            .retrieve()
            .bodyToMono(ImgurResponse::class.java)
            .block()

        return response!!.data.link
    }

    private fun deleteFileIfExists(filePath: String) {
        val path = Path.of(filePath)
        Files.deleteIfExists(path)
    }

    data class ImgurResponse(
        val data: ImgurData
    )

    data class ImgurData(
        val link: String
    )

}