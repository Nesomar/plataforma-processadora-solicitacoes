package com.portalcliente.backend.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient
import software.amazon.awssdk.services.s3.S3AsyncClient
import software.amazon.awssdk.services.sqs.SqsAsyncClient
import java.net.URI

/**
 * Endpoint configurável: vazio aponta pros serviços reais da AWS; setado (ex: http://localhost:4566,
 * porta única do ministack) aponta todos os clientes pro emulador local. Nenhum outro código muda
 * entre os dois ambientes.
 */
@Configuration
class AwsClientConfig(
    @Value("\${aws.region}") private val region: String,
    @Value("\${aws.endpoint-override:}") private val endpointOverride: String,
) {

    @Bean
    fun dynamoDbAsyncClient(): DynamoDbAsyncClient {
        val builder = DynamoDbAsyncClient.builder().region(Region.of(region))
        if (endpointOverride.isNotBlank()) builder.endpointOverride(URI.create(endpointOverride))
        return builder.build()
    }

    @Bean
    fun dynamoDbEnhancedAsyncClient(dynamoDbAsyncClient: DynamoDbAsyncClient): DynamoDbEnhancedAsyncClient =
        DynamoDbEnhancedAsyncClient.builder().dynamoDbClient(dynamoDbAsyncClient).build()

    @Bean
    fun s3AsyncClient(): S3AsyncClient {
        val builder = S3AsyncClient.builder().region(Region.of(region))
        // Path-style (bucket como parte do path, não subdomínio) — obrigatório contra o
        // ministack, que não resolve `<bucket>.<host>` como virtual-hosted style faria.
        if (endpointOverride.isNotBlank()) builder.endpointOverride(URI.create(endpointOverride)).forcePathStyle(true)
        return builder.build()
    }

    @Bean
    fun sqsAsyncClient(): SqsAsyncClient {
        val builder = SqsAsyncClient.builder().region(Region.of(region))
        if (endpointOverride.isNotBlank()) builder.endpointOverride(URI.create(endpointOverride))
        return builder.build()
    }
}
