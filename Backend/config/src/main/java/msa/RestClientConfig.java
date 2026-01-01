package msa;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {
    @Value("${dist.host}")
    private String baseUri;
    @Value("${dist.timeout}")
    private int timeout;

    @Bean
    public RestClient zofarim(RestClient.Builder builder) {
        SimpleClientHttpRequestFactory factory =
                new SimpleClientHttpRequestFactory();
        factory.setReadTimeout(this.timeout);

        return builder
                .baseUrl(baseUri)
                .requestFactory(factory)
                .build();
    }
}
