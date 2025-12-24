package msa;

import com.corundumstudio.socketio.SocketIOServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SocketIOSender {
    @Autowired
    private SocketIOServer server;

    public void sendAlertToAll(AlertDistribution alertDistribution) {
       server.getBroadcastOperations().sendEvent("alert", alertDistribution);
    }

    public void sendCancellationToAll(int incidentId) {
        server.getBroadcastOperations().sendEvent("cancel", incidentId);
    }
}
