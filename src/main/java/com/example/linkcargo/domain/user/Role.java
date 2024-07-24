package com.example.linkcargo.domain.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {
    CONSIGNOR, // 화주
    FORWARDER, // 포워더
    OTHER, // 화주, 포워더 둘다 해당
}
