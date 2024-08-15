package com.example.linkcargo.domain.user.dto.response;


import com.example.linkcargo.global.s3.dto.FileDTO;
import java.util.List;

public record FilesResponse (
    List<FileDTO> files
){
}
