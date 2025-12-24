package msa;

import com.corundumstudio.socketio.SocketIOServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SocketIOSender {
    @Autowired
    private SocketIOServer server;

    public void sendToAll(AlertDistribution alertDistribution) {
       server.getBroadcastOperations().sendEvent("alert", alertDistribution);
    }
}
