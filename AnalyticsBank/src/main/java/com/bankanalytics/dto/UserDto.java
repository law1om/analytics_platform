package com.bankanalytics.dto;

import com.bankanalytics.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long id;
    private String name;
    private String email;
    private User.UserRole role;
    private Long divisionId;
    private String divisionName;
    private String block;
}
