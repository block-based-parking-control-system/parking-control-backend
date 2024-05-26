package com.capstone.parking.config.websocket;

import lombok.extern.slf4j.Slf4j;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;

@Component
@Slf4j
public class RosBridgeClient extends WebSocketClient {

    private final static String SERVER_URI = "ws://172.19.12.107:9090";
    private final Set<String> topics = new HashSet<>();

    public RosBridgeClient() throws URISyntaxException {
        super(new URI(SERVER_URI));
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        log.info("Connected to ROS Bridge server");
    }

    @Override
    public void onMessage(String message) {
        /*
        TODO: Parse the message to extract the topic and the data
              For simplicity, let's assume the message is in the format "topic:data"
         */

        // Call the handler for the topic
        topics.add(message);
        log.info("Received message: {}", message);
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        log.info("Connection closed: {}", reason);
    }

    @Override
    public void onError(Exception ex) {
        ex.printStackTrace();
    }

    public void publish(String topic, String message) {
        String jsonMessage = String.format("{\"op\":\"publish\",\"topic\":\"%s\",\"msg\":{\"data\":\"%s\"}}", topic, message);
        this.send(jsonMessage);
        log.info("Published message: {} - {}", topic, message);
    }

    //ROS Bridge 서버에 구독 요청을 보냄
    public String subscribe(String topic) {
        String jsonMessage = String.format("{\"op\":\"subscribe\",\"topic\":\"%s\"}", topic);
        this.send(jsonMessage); //구독 요청. 이후 ROS Bridge 서버가 주기적으로 해당 토픽에 관한 메시지를 웹소켓을 통해 보내면 받을 수 있음

        String result = topics.stream().filter(topic::equals)
                .findFirst()
                .orElse(null);
        log.info("Subscribe topic: {}", result);

        return result;
    }
}
