package com.portalcliente.backend.adapter.output.s3

import com.portalcliente.backend.port.output.ArquivoStorage
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.PutObjectRequest

@Component
class S3ArquivoStorage(
    private val s3Client: S3Client,
    @Value("\${aws.s3.attachments-bucket}") private val bucket: String,
) : ArquivoStorage {

    override fun gravar(key: String, bytes: ByteArray, contentType: String) {
        val request = PutObjectRequest.builder()
            .bucket(bucket)
            .key(key)
            .contentType(contentType)
            .build()
        s3Client.putObject(request, RequestBody.fromBytes(bytes))
    }
}
