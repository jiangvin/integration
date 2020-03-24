package service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import model.CustomException;
import model.CustomExceptionType;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * @author 蒋文龙(Vin)
 * @description
 * @date 2020/3/23
 */
@Slf4j
public class ConnectService {
    private static final String KEY_OF_PARAM = "?";

    private RestTemplate restTemplate = new RestTemplate();
    private ObjectMapper objectMapper = new ObjectMapper();
    private HttpHeaders headers;

    public ConnectService() {
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
    }

    <T> T getRequest(String url, Class<T> type) {
        return getRequest(url, type, null);
    }

    <T> T getRequest(String url, Class<T> type, Map<String, String> params) {
        try {
            url = generateUrlWithParams(url, params);
            log.info("Send get request to url:{}", url);
            ResponseEntity<String> responseStr = restTemplate.getForEntity(url, String.class);
            log.info("Receive response:{}, try to convert to {}", responseStr.getBody(), type.getName());
            if (type == String.class) {
                return (T) responseStr.getBody();
            } else {
                return objectMapper.readValue(responseStr.getBody(), type);
            }
        } catch (HttpClientErrorException e) {
            throw new CustomException(CustomExceptionType.HTTP, e.getStatusCode().toString());
        } catch (Exception e) {
            log.error("Catch http error:", e);
            throw new CustomException(CustomExceptionType.HTTP, e.getMessage());
        }
    }

    <T> T postRequest(String url, Class<T> type, Map<String, String> params) {
        try {
            HttpEntity<MultiValueMap<String, String>> request = generateRequest(params);
            log.info("Send post request:{} to url:{}", request.toString(), url);
            ResponseEntity<String> responseStr = restTemplate.postForEntity(url, request , String.class);
            log.info("Receive response:{}, try to convert to {}", responseStr.getBody(), type.getName());
            return objectMapper.readValue(responseStr.getBody(), type);
        } catch (HttpClientErrorException e) {
            throw new CustomException(CustomExceptionType.HTTP, e.getStatusCode().toString());
        } catch (Exception e) {
            log.error("Catch http error:", e);
            throw new CustomException(CustomExceptionType.HTTP, e.getMessage());
        }
    }

    private String generateUrlWithParams(String url, Map<String, String> params) {
        if (params == null || params.isEmpty()) {
            return url;
        }

        for (Map.Entry<String, String> param : params.entrySet()) {
            url += String.format("%s%s=%s", getUrlMatching(url), param.getKey(), param.getValue());
        }

        return url;
    }

    private HttpEntity<MultiValueMap<String, String>> generateRequest(Map<String, String> params) {
        MultiValueMap<String, String> pushMap = new LinkedMultiValueMap<>(16);
        params.forEach(pushMap::add);
        return new HttpEntity<>(pushMap, headers);
    }

    private String getUrlMatching(String url) {
        if (url.contains(KEY_OF_PARAM)) {
            return "&";
        }

        return KEY_OF_PARAM;
    }
}
