package org.origin.centres.security.remote.handle;

import org.origin.centres.security.remote.exception.InvalidException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Map;

/**
 * @author zhangjie
 * @version 2020-09-27
 * @apiNote 自定义 Remote RestTemplate 响应异常处理
 */
public class WebResponseErrorHandler extends DefaultResponseErrorHandler {

    private RestTemplate restTemplate;

    public WebResponseErrorHandler(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        if (response.getStatusCode() != HttpStatus.BAD_REQUEST) {
            // 获取 message 信息
            ResponseExtractor<ResponseEntity<Map>> responseExtractor = restTemplate.responseEntityExtractor(Map.class);
            ResponseEntity<Map> entity = responseExtractor.extractData(response);
            if (entity != null && entity.getBody() != null) {
                Map body = entity.getBody();
                Object Message = body.get("message");
                String message = Message.toString();
                if (response.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                    throw new InvalidTokenException(message);
                }
                throw new InvalidException(message);
            }
        }
    }

}
