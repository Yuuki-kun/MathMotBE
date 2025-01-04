package com.mot.mot.service;

import com.mot.mot.model.dto.UploadImageResponse;

public interface IImageUpload {
    UploadImageResponse upload(String fileName, byte[] file);

    //hash or id
    void deleteImage(String deleteHash);

}
