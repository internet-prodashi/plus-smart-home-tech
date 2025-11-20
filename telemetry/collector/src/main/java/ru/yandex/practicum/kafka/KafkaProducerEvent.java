package ru.yandex.practicum.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.exception.KafkaSendException;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaProducerEvent implements DisposableBean {
    private final KafkaTemplate<String, SpecificRecordBase> kafkaTemplate;
    private final Producer<String, SpecificRecordBase> producer;

    public void sendRecord(KavkaProducerParam kavkaProducerParam) {
        if (!kavkaProducerParam.isValid())
            throw new IllegalArgumentException("Unacceptable Param: " + kavkaProducerParam);

        try {
            kafkaTemplate.send(new ProducerRecord<>(
                            kavkaProducerParam.getTopic(),
                            kavkaProducerParam.getPartition(),
                            kavkaProducerParam.getTimestamp(),
                            kavkaProducerParam.getKey(),
                            kavkaProducerParam.getValue()
                    )
            ).get();
        } catch (Exception exp) {
            log.error("Error when sending a message for Param={}", kavkaProducerParam, exp);
            throw new KafkaSendException("Error " + exp + " when sending a message: " + kavkaProducerParam);
        }
    }

    @Override
    public void destroy() {
        try {
            kafkaTemplate.flush();
            producer.flush();
            producer.close();
            log.info("KafkaEventProducer stopped");
        } catch (Exception e) {
            log.error("Error when stopping KafkaEventProducer", e);
            throw new RuntimeException(e);
        }
    }
}