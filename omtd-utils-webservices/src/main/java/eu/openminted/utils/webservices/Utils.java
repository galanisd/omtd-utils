package eu.openminted.utils.webservices;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.AbstractResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;


/**
 * @author galanisd
 *
 */
public class Utils {

	private static final Logger log = LoggerFactory.getLogger(Utils.class);
	
	/**
	 * Download.
	 * @param fileInputStream
	 * @param fname
	 * @return
	 */
	public static ResponseEntity<Resource> download(InputStream fileInputStream, String fname){

        Resource resource  = null;
        String cont_len = "0";
        try {
            cont_len = Integer.toString(fileInputStream.available());
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            resource = new InputStreamResource(fileInputStream);
            if(!resource.exists() || !resource.isReadable()) {
            	throw new DownloadException("Could not read file: " + fname);
            }
            
        } catch (DownloadException e) {
            throw new DownloadException("Could not read file: " + fname, e);
        }

		return ResponseEntity
                .ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fname + "\"")
				.header(HttpHeaders.CONTENT_LENGTH, cont_len)
                .body(resource);
	}	
	
	/**
	 * Downloads from server.
	 * @param parameters
	 * @param service
	 * @param fileName
	 * @param destination
	 * @return
	 */
	public static boolean downloadFromServer(MultiValueMap<String, Object> parameters, String endpoint, String service, String fileName, String destination, RestTemplate restTemplate){
		try{			
			// Callback
			RequestCallback requestCallback = new DataRequestCallback(parameters, restTemplate);			
			// Streams the response.
			ResponseExtractor<Void> responseExtractor = response -> {
			    // Write the response to a file.
			    Path path = Paths.get(destination);
			    Files.copy(response.getBody(), path, StandardCopyOption.REPLACE_EXISTING);
			    return null;
			};			
			restTemplate.execute(destination(endpoint, service), HttpMethod.POST, requestCallback, responseExtractor);	
		}catch(Exception e){
			log.info("ERROR", e);
			return false;
		}
		
		return true;
	}
		
	public static File upload(MultipartFile multipartFile){
		
		File file = null;
		
	    try{
	    	log.info("uploadedFile");
	    	// Create temp file.
	    	File tmp = Files.createTempFile("ELRC", "").toFile();
			String destination = tmp  + multipartFile.getOriginalFilename();
		    
	    	file = new File(destination);
		    multipartFile.transferTo(file);
	    		    	
		    
	    }catch(Exception e){
	    	e.printStackTrace();
	    }
		
		return file;	
	}
	
	public static String destination(String endpoint, String service){
		return endpoint + service;
	}
	
}
