package com.example.pioneerpixel.kafka;

import com.example.pioneerpixel.dto.TransferMoneyRequestDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class KafkaConsumer {

  @KafkaListener(
      topics = "my-topic",
      groupId = "my-group",
      containerFactory = "transferListenerContainerFactory")
  public void listen(TransferMoneyRequestDto transfer) {
    System.out.println("Received message from Kafka (object): " + transfer);
  }
}
