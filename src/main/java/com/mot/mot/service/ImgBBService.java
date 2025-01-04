package com.mot.mot.service;
import org.springframework.http.*;

import com.mot.mot.model.dto.UploadImageResponse;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;
import java.util.Map;

@Service
public class ImgBBService implements IImageUpload{
    private static final String API_URL = "https://api.imgbb.com/1/upload";
    private final String API_KEY = "6198f12d5918fb7d737c43278805be8f";
    @Override
    public UploadImageResponse upload(String fileName, byte[] file) {
        RestTemplate restTemplate = new RestTemplate();
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("image", Base64.getEncoder().encodeToString(file)); // Chuyển byte[] sang Base64
//        body.add("expiration", "600");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<Object> response = restTemplate.exchange(
                API_URL + "?key=" + API_KEY,
                HttpMethod.POST,
                requestEntity,
                Object.class
        );

        System.out.println("Response: " + response.getBody());
        Object responseBody = response.getBody();
        if (responseBody instanceof Map) {
            Map<String, Object> responseMap = (Map<String, Object>) responseBody;

            // Truy cập trường "data"
            Map<String, Object> data = (Map<String, Object>) responseMap.get("data");

            if (data != null) {
                String id = (String) data.get("id");
                String url = (String) data.get("url");
                String name = (String) data.get("filename");
                String deleteHash = (String) data.get("delete_url");
                Number sizeNumber = (Number) data.get("size");
                long size = sizeNumber.longValue(); // Chuyển đổi sang Long

                System.out.println("ID: " + id);
                System.out.println("URL: " + url);
                System.out.println("Name: " + name);
                System.out.println("Delete hash: " + deleteHash);
                System.out.println("Size: " + size);
            }
        }

        return null;
    }

    //also as delete url
    @Override
    public void deleteImage(String deleteUrl) {
        RestTemplate restTemplate = new RestTemplate();
        try{
            ResponseEntity<String> response = restTemplate.exchange(deleteUrl, HttpMethod.POST, null, String.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                System.out.println("Xóa ảnh thành công!");
            } else {
                System.out.println("Xóa ảnh thất bại! Mã trạng thái: " + response.getStatusCode());
            }
        }catch (Exception e) {
            System.out.println("Có lỗi xảy ra khi xóa ảnh: " + e.getMessage());
        }

    }
}
