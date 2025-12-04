package com.bankanalytics.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DivisionDto {
    private Long id;
    private String name;
    private List<String> blocks;
}
