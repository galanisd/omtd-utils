package eu.openminted.utils.webservices;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.RestTemplate;



public class DataRequestCallback<T> implements RequestCallback {
	 
	private RestTemplate restTemplate;
	
	private static final Logger log = LoggerFactory.getLogger(DataRequestCallback.class);
	
    private List<MediaType> mediaTypes = Arrays.asList(MediaType.APPLICATION_OCTET_STREAM, MediaType.ALL);
	//private List<MediaType> mediaTypes = Arrays.asList(MediaType.APPLICATION_OCTET_STREAM, MediaType.APPLICATION_JSON, MediaType.ALL);
	
    private HttpEntity<T> requestEntity;

    public DataRequestCallback(T entity, RestTemplate restTemplate) {
        requestEntity = new HttpEntity<>(entity);
        this.restTemplate = restTemplate;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void doWithRequest(ClientHttpRequest httpRequest) throws IOException {
    	log.debug("doWithRequest");
        httpRequest.getHeaders().setAccept(mediaTypes);
        T requestBody = requestEntity.getBody();
        Class<?> requestType = requestBody.getClass();
        HttpHeaders requestHeaders = requestEntity.getHeaders();
        MediaType requestContentType = requestHeaders.getContentType();

        for (HttpMessageConverter<?> messageConverter : restTemplate.getMessageConverters()) {
            if (messageConverter.canWrite(requestType, requestContentType)) {
                if (!requestHeaders.isEmpty()) {
                    httpRequest.getHeaders().putAll(requestHeaders);
                }
                ((HttpMessageConverter<Object>) messageConverter).write(requestBody, requestContentType, httpRequest);
                return;
            }
        }                     
    }
}