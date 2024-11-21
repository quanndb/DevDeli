package com.example.identityService.mapper;

import com.example.identityService.DTO.response.CloudResponseDTO;
import org.mapstruct.Mapper;


import java.util.Map;

@Mapper(componentModel = "spring")
public interface CloudImageMapper {
    default CloudResponseDTO toCloudResponse(Map<?, ?> map) {
        CloudResponseDTO fileMetadata = new CloudResponseDTO();
        if (map.containsKey("url")) {
            fileMetadata.setUrl(String.valueOf(map.get("url")));
        }
        if (map.containsKey("public_id")) {
            fileMetadata.setPublicId(String.valueOf(map.get("public_id")));
        }
        if (map.containsKey("original_filename")) {
            fileMetadata.setOriginalFilename(
                    String.valueOf(map.get("original_filename")));
        }
        if(map.containsKey("format")){
            fileMetadata.setOriginalFilename(
                    fileMetadata.getOriginalFilename()+"."+ map.get("format")
            );
        }
        return fileMetadata;
    }
}