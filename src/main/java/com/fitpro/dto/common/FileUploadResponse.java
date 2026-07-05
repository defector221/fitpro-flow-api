package com.fitpro.dto.common;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FileUploadResponse {
    private String url;
    private String objectKey;
    private String fileName;
    private String contentType;
    private long size;
}
