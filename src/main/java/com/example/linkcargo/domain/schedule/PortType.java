package com.example.linkcargo.domain.schedule;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PortType {
    EXPORT, // 수출항
    IMPORT // 수입항
}
