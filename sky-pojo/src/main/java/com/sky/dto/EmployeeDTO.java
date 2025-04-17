package com.sky.dto;

import io.swagger.annotations.Api;
import lombok.Data;

import java.io.Serializable;

@Data
@Api(tags = "员工DTO类")
public class EmployeeDTO implements Serializable {

    private Long id;

    private String username;

    private String name;

    private String phone;

    private String sex;

    private String idNumber;

}
