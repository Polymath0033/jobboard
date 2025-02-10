package com.polymath.jobboard.services;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
            Map uploadResult=cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            "public_id",uniqueFileName,
                            "folder",folderName,
                            "resource_type","auto",
                            "allowed_formats","pdf"

                    )
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
}
