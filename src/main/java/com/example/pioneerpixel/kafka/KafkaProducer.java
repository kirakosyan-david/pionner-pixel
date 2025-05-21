package com.example.pioneerpixel.kafka;

import com.example.pioneerpixel.dto.TransferMoneyRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaProducer {

  private final KafkaTemplate<String, TransferMoneyRequestDto> kafkaTemplate;

  public void send(String topic, String key, TransferMoneyRequestDto dto) {
    kafkaTemplate.send(topic, key, dto);
    System.out.println("Message sent to Kafka (JSON): " + dto);
  }
}
