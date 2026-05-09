package adapter.sse.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.sse.Sse;
import jakarta.ws.rs.sse.SseBroadcaster;
import jakarta.ws.rs.sse.SseEventSink;

import adapter.output.StatusResponse;
import shared.mapper.JsonMapper;

@ApplicationScoped
public class SSEEventService {
    
    private final Map<String, SseBroadcaster> broadcasters = new ConcurrentHashMap<>();
    
    @Inject
    Sse sse;
    
    @Inject
    JsonMapper json;
    
    public void addClient(String processId, SseEventSink sink) {
        SseBroadcaster broadcaster = broadcasters.computeIfAbsent(processId, id -> sse.newBroadcaster());
        broadcaster.register(sink);
    }
    
    public void sendEvent(String processId, StatusResponse status) {
        SseBroadcaster broadcaster = broadcasters.get(processId);
        if (broadcaster != null) {
            broadcaster.broadcast(sse.newEventBuilder()
                .name("status-update")
                .data(json.toJson(status))
                .build());
        }
    }
    
    public void removeClient(String processId, SseEventSink sink) {
        SseBroadcaster broadcaster = broadcasters.get(processId);
        if (broadcaster != null) {
            broadcaster.close();
            broadcasters.remove(processId);
        }
    }
}