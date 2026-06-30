package org.openownership.form_builder.service

import io.minio.GetObjectArgs
import io.minio.MinioClient
import io.minio.PutObjectArgs
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.InputStream
import java.util.UUID

@Service
class FileServiceImpl(
    private val minioClient: MinioClient,
    @Value("\${minio.bucket}") private val bucket: String,
) : FileService {
    override fun upload(file: MultipartFile): String {
        val sanitized =
            file.originalFilename
                ?.replace(Regex("[^a-zA-Z0-9._-]"), "_")
                ?.ifEmpty { "file" }
                ?: "file"
        val key = "submissions/${UUID.randomUUID()}/$sanitized"
        minioClient.putObject(
            PutObjectArgs
                .builder()
                .bucket(bucket)
                .`object`(key)
                .stream(file.inputStream, file.size, -1)
                .contentType(file.contentType ?: "application/octet-stream")
                .build(),
        )
        return key
    }

    override fun download(key: String): InputStream =
        minioClient.getObject(
            GetObjectArgs
                .builder()
                .bucket(bucket)
                .`object`(key)
                .build(),
        )
}
