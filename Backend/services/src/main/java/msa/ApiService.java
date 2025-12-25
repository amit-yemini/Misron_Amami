package msa;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class ApiService {
    @Autowired
    private RestClient restClient;

    @Value("${dist.uri}")
    private String uri;

    public void sendRequest(AlertDistribution alertDistribution) {
        restClient.post()
                .uri(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .body(alertDistribution)
                .retrieve()
                .toBodilessEntity();
    }
}
