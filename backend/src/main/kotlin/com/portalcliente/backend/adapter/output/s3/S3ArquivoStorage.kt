package com.portalcliente.backend.adapter.output.s3

import com.portalcliente.backend.port.output.ArquivoStorage
import kotlinx.coroutines.future.await
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import software.amazon.awssdk.core.async.AsyncRequestBody
import software.amazon.awssdk.core.async.AsyncResponseTransformer
import software.amazon.awssdk.services.s3.S3AsyncClient
import software.amazon.awssdk.services.s3.model.GetObjectRequest
import software.amazon.awssdk.services.s3.model.PutObjectRequest

@Component
class S3ArquivoStorage(
    private val s3Client: S3AsyncClient,
    @Value("\${aws.s3.attachments-bucket}") private val bucket: String,
) : ArquivoStorage {

    override suspend fun gravar(key: String, bytes: ByteArray, contentType: String) {
        val request = PutObjectRequest.builder()
            .bucket(bucket)
            .key(key)
            .contentType(contentType)
            .build()
        s3Client.putObject(request, AsyncRequestBody.fromBytes(bytes)).await()
    }

    override suspend fun ler(key: String): ByteArray {
        val request = GetObjectRequest.builder().bucket(bucket).key(key).build()
        return s3Client.getObject(request, AsyncResponseTransformer.toBytes()).await().asByteArray()
    }
}
