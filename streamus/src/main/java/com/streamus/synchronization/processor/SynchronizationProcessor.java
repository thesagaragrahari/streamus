package com.streamus.synchronization.processor;

import com.streamus.synchronization.model.SyncEvent;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class SynchronizationProcessor {

    public Mono<SyncEvent> synchronize(String traceId, String sessionId, String audioPayload) {
        return Mono.fromSupplier(() -> {
            SyncEvent baseEvent = new SyncEvent(traceId, sessionId, audioPayload);
            String syncedPayload = "synced_" + audioPayload
                    + "_timestamp_marker_" + baseEvent.timestamp().toEpochMilli();
            return new SyncEvent(traceId, sessionId, baseEvent.timestamp(), syncedPayload);
        });
    }
}
