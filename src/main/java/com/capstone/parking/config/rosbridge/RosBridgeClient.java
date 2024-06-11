package com.capstone.parking.config.rosbridge;

import com.capstone.parking.config.rosbridge.dto.impl.RosPublish;
import com.capstone.parking.config.rosbridge.dto.impl.RosSubscribe;
import lombok.extern.slf4j.Slf4j;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

@Component
@Slf4j
public class RosBridgeClient extends WebSocketClient {

    private final static String SERVER_URI = "ws://172.19.12.107:9090";

    private final CopyOnWriteArrayList<SseEmitter> sseEmitters = new CopyOnWriteArrayList<>();

    /**
     * messageHandlers: 웹소켓을 통해 수신된 메시지를 처리하는 처리기 저장소
     *
     * key: 메시지 토픽 이름 (String)
     * value: 해당 토픽의 메시지를 처리하는 messageHandler (Consumer<String>). 웹소켓에서 받은 메시지를 인자로 얻어 처리하는 역할
     *
     * TODO: Consumer<> 의 정확한 역할과 동작과정에 대해 공부
     */
    private final Map<String, Consumer<String>> messageHandlers = new HashMap<>();

    public RosBridgeClient() throws URISyntaxException {
        super(new URI(SERVER_URI));
    }

    @Override
    public void onOpen(ServerHandshake handshakeData) {
        log.info("[onOpen] Connected to ROS Bridge server");
    }

    /**
     * 구독한 토픽에 대한 메시지를 웹소켓으로부터 받으면 실행
     * 1. 메시지의 토픽을 추출
     * 2. 해당 토픽에 대한 messageHandler 조회
     * 3. 찾은 messageHandler 를 사용해 메시지 처리 (handler.accept(message))
     *
     * TODO: 토픽에 따른 적절한 처리기의 모습을 구상해서 코드로 작성
     * ex) topic: /waypoints -> handler: SseEmitter 를 통해 방출
     */
    @Override
    public void onMessage(String message) {
        // TODO: Parse the message to extract the topic and the data
        // For simplicity, let's assume the message is in the format "topic:data"
        log.info("[onMessage] Received message: {}", message);
        for (SseEmitter emitter : sseEmitters) {
            try {
                //TODO json 형태의 message 변수를 객체로 변환해서 해당 객체의 타입(ResponseType)을 결졍
                //      타입에 따라 적절한 ResponseDto 를 생성해 send

                emitter.send(SseEmitter.event()
                        .name("waypoints")
                        .data(message)
                );
            } catch (IOException e) {
                emitter.completeWithError(e);
            }
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        log.info("[onClose] Connection closed: {}", reason);
    }

    @Override
    public void onError(Exception ex) {
        ex.printStackTrace();
    }

    /**
     * 특정 토픽에 대한 메시지 핸들러 추가
     * 이후 해당 토픽의 메시지가 웹소켓을 통해 도착하면, 추가된 핸들러를 사용하여 메시지 처리
     */
    public void addMessageHandler(String topic, Consumer<String> handler) {
        log.info("[addMessageHandler] Added message handler for topic: {}", topic);
        messageHandlers.put(topic, handler);
    }

    public void publish(String topic, String message) {
        String jsonMessage = RosPublish.of(topic, message).toJson();
        this.send(jsonMessage);
        log.info("[publish] Published message: {} - {}", topic, message);
    }

    //ROS Bridge 서버에 구독 요청을 보냄
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
}
