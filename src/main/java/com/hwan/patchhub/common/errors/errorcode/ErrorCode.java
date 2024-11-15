package com.hwan.patchhub.common.errors.errorcode;

import org.springframework.http.HttpStatus;

public interface ErrorCode {
    String getName();
    HttpStatus getStatus();
    String getDefaultMessage();
}