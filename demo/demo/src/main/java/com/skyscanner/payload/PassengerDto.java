package com.skyscanner.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PassengerDto {
    private String name;
    private int age;
    private String email;
    private String mobile;
}
