package com.portalcliente.backend.adapter.output.dynamodb

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import java.net.URI

/**
 * Endpoint configurável: vazio aponta pro DynamoDB real da AWS; setado (ex: http://localhost:4566)
 * aponta pro ministack local. Nenhum outro código muda entre os dois ambientes.
 */
@Configuration
class DynamoDbConfig(
    @Value("\${aws.region}") private val region: String,
    @Value("\${aws.dynamodb.endpoint:}") private val endpointOverride: String,
) {

    @Bean
    fun dynamoDbClient(): DynamoDbClient {
        val builder = DynamoDbClient.builder().region(Region.of(region))
        if (endpointOverride.isNotBlank()) {
            builder.endpointOverride(URI.create(endpointOverride))
        }
        return builder.build()
    }

    @Bean
    fun dynamoDbEnhancedClient(dynamoDbClient: DynamoDbClient): DynamoDbEnhancedClient =
        DynamoDbEnhancedClient.builder().dynamoDbClient(dynamoDbClient).build()
}
