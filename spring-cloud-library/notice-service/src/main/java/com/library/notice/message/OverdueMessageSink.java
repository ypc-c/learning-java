package com.library.notice.message;

import com.library.common.dto.OverdueMessage;
import com.library.notice.service.NoticeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@EnableBinding(Sink.class)
public class OverdueMessageSink {

    @Autowired
    private NoticeService noticeService;

    /**
     * Listen to overdue messages from RabbitMQ via Spring Cloud Stream
     */
    @StreamListener(Sink.INPUT)
    public void handleOverdueMessage(@Payload OverdueMessage message) {
        log.info("Received overdue message from Stream: userId={}, bookId={}, bookTitle={}",
                message.getUserId(), message.getBookId(), message.getBookTitle());

        try {
            noticeService.handleOverdueMessage(message);
        } catch (Exception e) {
            log.error("Failed to process overdue message for user: {}, book: {}",
                    message.getUserId(), message.getBookId(), e);
        }
    }
}
