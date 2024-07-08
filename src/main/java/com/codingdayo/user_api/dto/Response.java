package com.codingdayo.user_api.dto;

import com.codingdayo.user_api.model.Organisation;
import com.codingdayo.user_api.model.User;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Data
@Getter
@Setter
public class Response {


    private int statusCode;
    private String status;
    private String message;
    private Object data;



}
