package com.mot.mot.service;

import com.mot.mot.model.dto.UploadImageResponse;
import com.mot.mot.service.abstractInterface.IImageUpload;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;


@Service
public class ImageService implements IImageUpload {
    //https://api.imgur.com/3/image
    private final String CLIENT_ID = "72dea9288b9c184";

    public UploadImageResponse upload(String fileName, byte[] file){
        String imageUrl = null;
        try(CloseableHttpClient httpClient = HttpClients.createDefault()){

            HttpPost request = new HttpPost("https://api.imgur.com/3/image");
            request.setHeader("Authorization", "Client-ID " + CLIENT_ID);

            ContentBody byteArrayContent = new ByteArrayBody(file, fileName);
            HttpEntity entity = MultipartEntityBuilder.create()
                    .addPart("image", byteArrayContent)
                    .build();

            request.setEntity(entity);

            try (CloseableHttpResponse response = httpClient.execute(request)) {
                // Xử lý phản hồi
                String responseString = EntityUtils.toString(response.getEntity());
                JSONObject jsonResponse = JSONValue.parse(responseString, JSONObject.class);
                System.out.println("Response: " + jsonResponse);
                // Kiểm tra nếu upload thành công
                if (jsonResponse.get("success").equals(true)) {
                    // Trả về URL của ảnh vừa tải lên
                    JSONObject data = (JSONObject) jsonResponse.get("data");
                    String link = data.getAsString("link");
                    String deleteHash = data.getAsString("deletehash");
                    String storageWeb = "imgur";
                    String type = data.getAsString("type");
                    System.out.println("Link image: " + link);
                    return UploadImageResponse.builder()
                            .name(data.getAsString("name"))
                            .url(link)
                            .storageWeb(storageWeb)
                            .deleteHash(deleteHash)
                            .type(type)
                            .size(data.getAsNumber("size").longValue())
                            .build();
                } else {
                    throw new Exception("Error uploading image: " + jsonResponse.getAsString("data"));
                }
            }


        }catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException("Error uploading image");
        }
    }

    public void deleteImage(String deleteHash){
        try(CloseableHttpClient httpClient = HttpClients.createDefault()){

            HttpDelete request = new HttpDelete("https://api.imgur.com/3/image/" + deleteHash);
            request.setHeader("Authorization", "Client-ID " + CLIENT_ID);

            try (CloseableHttpResponse response = httpClient.execute(request)) {
                // Xử lý phản hồi
                String responseString = EntityUtils.toString(response.getEntity());
                JSONObject jsonResponse = JSONValue.parse(responseString, JSONObject.class);
                System.out.println("Response: " + jsonResponse);
                // Kiểm tra nếu upload thành công
                if (jsonResponse.get("success").equals(true)) {
                    System.out.println("Delete image success");
                } else {
                    throw new Exception("Error deleting image: " + jsonResponse.getAsString("data"));
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }}
    }
