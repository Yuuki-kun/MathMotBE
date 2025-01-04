package com.mot.mot.service;

import com.mot.mot.model.dto.UploadImageResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;

@Service
public class LocalStoreImageService implements IImageUpload{
    @Override
    public UploadImageResponse upload(String fileName, byte[] file) {
        //create a new file name
        fileName = "image_"+System.currentTimeMillis() + fileName.substring(fileName.lastIndexOf("."));
        if(file != null){
            String filePath = "src/main/resources/static/images/" + fileName;
            // save file to local
            // return file data
            File isExist = new File(filePath);
            if(isExist.exists()){
                //image.png -> image1.png
                int lastDot = filePath.lastIndexOf(".");
                filePath = filePath.substring(0, lastDot) + "1" + filePath.substring(lastDot);
                fileName = fileName.substring(0, lastDot) + "1" + fileName.substring(lastDot);
            }


            try(FileOutputStream fos = new FileOutputStream(filePath)){
                fos.write(file);
                return UploadImageResponse.builder()
                        .name(fileName)
                        .url("http://localhost:8080/mathmot-api/images/" + fileName)
                        .storageWeb("local")
                        .deleteHash(fileName)
                        .type("image/"+fileName.substring(fileName.lastIndexOf(".")+1))
                        .build();

            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    @Override
    public void deleteImage(String deleteHash) {

    }

    public byte[] downloadImageFromFileSystem(String fileName) throws IOException {
        String filePath = "src/main/resources/static/images/" + fileName;
        byte[] images = Files.readAllBytes(new File(filePath).toPath());
        return images;
    }

}
