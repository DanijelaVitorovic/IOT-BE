package com.dex.coreserver.exceptions;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErrorObject {

    Integer statusCode;
    String message;
    String requestId;
}
