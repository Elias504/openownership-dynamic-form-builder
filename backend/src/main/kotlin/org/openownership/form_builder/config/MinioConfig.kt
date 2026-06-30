package org.openownership.form_builder.config

import io.minio.BucketExistsArgs
import io.minio.MakeBucketArgs
import io.minio.MinioClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.DependsOn

@Configuration
class MinioConfig(
    @Value("\${minio.endpoint}") private val endpoint: String,
    @Value("\${minio.access-key}") private val accessKey: String,
    @Value("\${minio.secret-key}") private val secretKey: String,
    @Value("\${minio.bucket}") private val bucket: String,
) {
    @Bean
    fun minioClient(): MinioClient =
        MinioClient
            .builder()
            .endpoint(endpoint)
            .credentials(accessKey, secretKey)
            .build()

    @Bean
    @DependsOn("minioClient")
    fun minioSetup(minioClient: MinioClient): Boolean {
        try {
            if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucket).build())) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucket).build())
            }
        } catch (_: Exception) {
            // MinIO not reachable at startup; bucket will be required on first use
        }
        return true
    }
}
