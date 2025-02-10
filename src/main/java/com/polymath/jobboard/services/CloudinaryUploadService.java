package com.polymath.jobboard.services;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.polymath.jobboard.exceptions.CustomBadRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
public class CloudinaryUploadService {
    @Autowired
    private Cloudinary cloudinary;

    public String uploadFile(MultipartFile file, String folderName) {
        try {
            String timestamp = String.valueOf(System.currentTimeMillis());
            String fileName = file.getOriginalFilename();
            String uniqueFileName = folderName+"/"+timestamp +"_"+ fileName;
            String contentType = file.getContentType();
            if(!isValidType(contentType)){
                throw new CustomBadRequest("This file format is not supported");
            }
            System.out.println(contentType);
            Map uploadParams;
            if(contentType.startsWith("image/")){
                uploadParams=ObjectUtils.asMap(
                        "resource_type","image",
                        "public_id",uniqueFileName,
                        "folder",folderName,
                        "allowed_formats",Arrays.asList("jpg","jpeg","png","gif")
                );
            }else {
                uploadParams=ObjectUtils.asMap(
                        "resource_type","auto",
                        "public_id",uniqueFileName,
                        "folder",folderName,
                        "allowed_formats","pdf"
                );
            }
            Map uploadResult=cloudinary.uploader().upload(
                    file.getBytes(),
                   uploadParams
            );
            return uploadResult.get("secure_url").toString();
        }catch (IOException e){
            throw new RuntimeException("File upload failed: "+e.getMessage(),e);
        }
    }

    public void deleteFile(String fileUrl) {
        if(StringUtils.hasText(fileUrl)){
            try {
                String publicId = extractFromPublicIdUrl(fileUrl);
                cloudinary.uploader().destroy(publicId,ObjectUtils.emptyMap());
            }catch (Exception e){
                throw new RuntimeException("File delete failed: "+e.getMessage(),e);
            }
        }
    }

    private String extractFromPublicIdUrl(String fileUrl) {
        String[] fileUrlPart = fileUrl.split("/");
        String fileName = fileUrlPart[fileUrlPart.length-1];
        return fileUrl.contains("/")?fileUrl.substring(fileUrl.lastIndexOf("/")+1).split("\\.")[0]:fileName;
    }
    private boolean isValidType(String contentType) {
        List<String> allowedTypes = Arrays.asList("image/jpeg","image/jpg","image/png","image/gif","application/pdf");
        return contentType!=null && allowedTypes.contains(contentType);
    }
}
