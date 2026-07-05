package com.library.borrow.message;

import com.library.common.dto.OverdueMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@EnableBinding(Source.class)
public class OverdueMessageSource {

    @Autowired
    private Source source;

    public void send(OverdueMessage message) {
        boolean sent = source.output().send(
                MessageBuilder.withPayload(message)
                        .setHeader("type", "overdue")
                        .build()
        );

        if (sent) {
            log.info("Overdue message sent to RabbitMQ: userId={}, bookId={}",
                    message.getUserId(), message.getBookId());
        } else {
            log.warn("Failed to send overdue message: userId={}, bookId={}",
                    message.getUserId(), message.getBookId());
        }
    }
}
