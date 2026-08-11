package com.portalcliente.backend.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.sqs.SqsClient
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
    fun dynamoDbClient(): DynamoDbClient {
        val builder = DynamoDbClient.builder().region(Region.of(region))
        if (endpointOverride.isNotBlank()) builder.endpointOverride(URI.create(endpointOverride))
        return builder.build()
    }

    @Bean
    fun dynamoDbEnhancedClient(dynamoDbClient: DynamoDbClient): DynamoDbEnhancedClient =
        DynamoDbEnhancedClient.builder().dynamoDbClient(dynamoDbClient).build()

    @Bean
    fun s3Client(): S3Client {
        val builder = S3Client.builder().region(Region.of(region))
        if (endpointOverride.isNotBlank()) builder.endpointOverride(URI.create(endpointOverride))
        return builder.build()
    }

    @Bean
    fun sqsClient(): SqsClient {
        val builder = SqsClient.builder().region(Region.of(region))
        if (endpointOverride.isNotBlank()) builder.endpointOverride(URI.create(endpointOverride))
        return builder.build()
    }
}
