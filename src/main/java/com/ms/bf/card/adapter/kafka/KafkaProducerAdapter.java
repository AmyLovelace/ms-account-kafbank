package com.ms.bf.card.adapter.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ms.bf.card.adapter.controller.model.card.AccountRest;
import com.ms.bf.card.adapter.rest.card.model.card.AccountModel;
import com.ms.bf.card.config.property.KafkaProperty;
import com.ms.bf.card.domain.Account;
import com.ms.bf.card.config.exception.GenericException;
import com.ms.bf.card.config.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class KafkaProducerAdapter implements KafkaProducerPort {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final KafkaProperty kafkaProperty;
    private final ObjectMapper objectMapper;

    public KafkaProducerAdapter(KafkaTemplate<String, String> kafkaTemplate, KafkaProperty kafkaProperty, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.kafkaProperty = kafkaProperty;
        this.objectMapper = objectMapper;
    }

    @Override
    public Integer sendMessage(Account card) throws GenericException {
        try {
            log.info("Message sent -> {}", card.toString());


            String json = objectMapper.writeValueAsString(card);


            Message<String> message = MessageBuilder.withPayload(json)
                    .setHeader(KafkaHeaders.TOPIC, kafkaProperty.getTopicName())
                    .build();

            kafkaTemplate.send(message);
            log.info("Sent message value: {}", card);
            return 0;
        } catch (MessagingException e) {
            log.error("Error al generar el mensaje: ", e);
            throw new GenericException(ErrorCode.CARD_INVALID_REQUEST, "Error al generar el mensaje");
        } catch (JsonProcessingException e) {
            log.error("Error al serializar a JSON: ", e);
            throw new GenericException(ErrorCode.CARD_INVALID_REQUEST, "Error al serializar a JSON");
        }
    }
}