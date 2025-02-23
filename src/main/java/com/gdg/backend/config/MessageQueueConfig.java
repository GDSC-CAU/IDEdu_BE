package com.gdg.backend.config;

import com.gdg.backend.domain.event.dto.DocumentOperationRequestDto;
import com.gdg.backend.domain.event.dto.DocumentOperationResponseDto;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;


/** 인메모리 메시지 큐 설정 */
@Configuration
public class MessageQueueConfig {

    /**
     * Operation 처리용 메시지 큐 <br>
     * - BlockingQueue 타입이므로 operation이 들어올 때까지 / 큐에 빈 공간이 생길 때까지 wait하는 메소드 제공<br>
     * - 용량은 일단 1000으로 세팅 (꽉 찬 후의 메시지는 공간 생길 때까지 wait) <br>
     * */
    @Bean
    public BlockingQueue<DocumentOperationRequestDto> eventQueue() {
        // TODO Document마다 메시지큐 따로 마련하기 (Map<Long, BlockingQueue> 형식으로)
        // TODO 아니면 아예 Redis나 RabbitMQ 등등 외부 메시지 큐로 옮기기 (옮길 땐 선택이유도 같이 생각해두기!)
        return new LinkedBlockingQueue<>(1000);
    }
}
