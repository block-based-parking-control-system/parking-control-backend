package com.capstone.parking.config.rosbridge;

import com.capstone.parking.config.rosbridge.dto.impl.RosPublish;
import com.capstone.parking.config.rosbridge.dto.impl.RosSubscribe;
import com.capstone.parking.config.rosbridge.topics.ROutTopic;
import com.capstone.parking.controller.response.dto.Completion;
import com.capstone.parking.controller.response.dto.Location;
import com.capstone.parking.controller.response.dto.ResponseDto;
import com.capstone.parking.controller.response.dto.ResponseType;
import lombok.extern.slf4j.Slf4j;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.concurrent.CopyOnWriteArrayList;

import static com.capstone.parking.controller.response.dto.ResponseType.CURRENT_LOCATION;

@Component
@Slf4j
public class RosBridgeClient extends WebSocketClient {

    private final static String SERVER_URI = "ws://172.19.5.197:9090";
    private final static long INACTIVITY_TIMEOUT_MS = 1000; // 1초 비활성화 타임아웃

    private final CopyOnWriteArrayList<SseEmitter> sseEmitters = new CopyOnWriteArrayList<>();

    private static int counter = 0;

    // 마지막 메시지 수신 시간을 추적하기 위한 필드
    private Instant lastMessageTime = Instant.now();

    public RosBridgeClient() throws URISyntaxException {
        super(new URI(SERVER_URI));
    }

    @Override
    public void onOpen(ServerHandshake handshakeData) {
        log.info("[onOpen] Connected to ROS Bridge server");
    }

    @Override
    public void onMessage(String message) {
        // 마지막 메시지 수신 시간 업데이트
        lastMessageTime = Instant.now();

        if ((++counter) % 7 != 0) {
            return;
        }

        log.info("[onMessage] Received message: {}", message);

        ROutTopic rOutTopic = ROutTopic.parse(message);
        if (rOutTopic != null) {
            Location location = rOutTopic.toLocation();
            log.info("[onMessage] location: {}", location);
            for (SseEmitter emitter : sseEmitters) {
                try {
                    emitter.send(
                            SseEmitter.event()
                                    .data(ResponseDto.of(CURRENT_LOCATION, location))
                    );

                } catch (IOException e) {
                    log.error("[onMessage] Error occurred while sending message", e);
                    emitter.completeWithError(e);
                }
            }
        }

    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        log.info("[onClose] Connection closed: {}", reason);
    }

    @Override
    public void onError(Exception ex) {
        log.info("[onError] error occurred", ex);
    }

    public void publish(String topic, String message) {
        String jsonMessage = RosPublish.of(topic, message).toJson();
        this.send(jsonMessage);
        log.info("[publish] Published message: {} - {}", topic, message);
    }

    public void subscribe(SseEmitter emitter, String... topics) {
        addSseEmitter(emitter);

        for (String topic : topics) {
            String jsonMessage = RosSubscribe.of(topic).toJson();
            this.send(jsonMessage); //구독 요청. 이후 ROS Bridge 서버가 주기적으로 해당 토픽에 관한 메시지를 웹소켓을 통해 보내면 받을 수 있음
            log.info("[subscribe] Subscribe topic: {}", topic);
        }
    }

    private void addSseEmitter(SseEmitter emitter) {
        this.sseEmitters.add(emitter);
        emitter.onCompletion(() -> sseEmitters.remove(emitter));
        emitter.onTimeout(() -> sseEmitters.remove(emitter));
        emitter.onError((e) -> sseEmitters.remove(emitter));
    }

    // 비활성 상태를 확인하는 예약 작업
    @Scheduled(fixedRate = 500) // 500밀리초마다 확인
    private void checkInactivity() {
        Instant now = Instant.now();
        if (now.minusMillis(INACTIVITY_TIMEOUT_MS).isAfter(lastMessageTime)) {
            log.info("[checkInactivity] 1초 이상 메시지가 수신되지 않았습니다. SSE emitters를 닫습니다.");

            // 타임아웃된 모든 emitters를 닫습니다.
            for (SseEmitter emitter : sseEmitters) {
                try {
                    emitter.send(
                            SseEmitter.event()
                                    .data(ResponseDto.of(ResponseType.COMPLETION, Completion.success()))
                    );
                    emitter.complete();
                } catch (IOException e) {
                    log.error("[checkInactivity] emitter를 닫는 중 오류 발생", e);
                    emitter.completeWithError(e);
                }
            }
            sseEmitters.clear(); // 닫은 후 모든 emitters를 삭제
        }
    }
}
