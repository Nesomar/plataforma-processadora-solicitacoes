package com.portalcliente.backend.adapter.output.sqs

import com.portalcliente.backend.domain.Anexo
import com.portalcliente.backend.port.output.EventoAnexoPublisher
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import software.amazon.awssdk.services.sqs.SqsClient
import software.amazon.awssdk.services.sqs.model.SendMessageRequest
import tools.jackson.databind.ObjectMapper

private data class AnexoEvento(val anexoId: String, val clienteId: String, val s3Key: String)

@Component
class SqsEventoAnexoPublisher(
    private val sqsClient: SqsClient,
    private val objectMapper: ObjectMapper,
    @Value("\${aws.sqs.attachments-queue-url}") private val queueUrl: String,
) : EventoAnexoPublisher {

    override fun publicar(anexo: Anexo) {
        val body = objectMapper.writeValueAsString(AnexoEvento(anexo.id, anexo.clienteId, anexo.s3Key))
        sqsClient.sendMessage(SendMessageRequest.builder().queueUrl(queueUrl).messageBody(body).build())
    }
}
