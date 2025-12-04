package com.bankanalytics.dto;

import com.bankanalytics.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserDto {
    private String name;
    private String email;
    private String password;
    private User.UserRole role;
    private Long divisionId;
    private String block;
}
