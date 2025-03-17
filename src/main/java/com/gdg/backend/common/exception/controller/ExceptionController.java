package com.gdg.backend.common.exception.controller;


import com.gdg.backend.common.response.ApiResponse;
import com.gdg.backend.common.response.status.ErrorCode;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;

import java.util.Map;

@Controller
@RequiredArgsConstructor
@Hidden // Swagger에 명시 X
public class ExceptionController implements ErrorController {
    private final ErrorAttributes errorAttributes;

    @RequestMapping("/error")
    @ResponseBody
    public ApiResponse<Object> handlerError(WebRequest request) {

        Map<String, Object> errorAttributes = this.errorAttributes.getErrorAttributes(request, ErrorAttributeOptions.defaults());

        System.out.println(errorAttributes);

        int status = (int) errorAttributes.getOrDefault("status", 500);
        String message = (String) errorAttributes.getOrDefault("error", "Unexpected error");

        System.out.println("CustomErrorController: received status : " + status);
        System.out.println("CustomErrorController: error message - " + message);

        return ApiResponse.onFailure(String.valueOf(status), message, "ERROR");
    }
}