package com.skyscanner.payload;
import com.skyscanner.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.io.IOException;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto implements Serializable {
    private Long id;
    private String firstName;
    private String lastName;
    private String city;
    private String email;
    private String mobile;
    private String state;
    private String country;
    private String pinCode;
    private String password;
    private String userPhoto;



    public UserDto(User user) throws IOException {
        this.id = user.getId();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.city = user.getCity();
        this.email = user.getEmail();
        this.mobile = user.getMobile();
        this.state = user.getState();
        this.country = user.getCountry();
        this.pinCode = user.getPinCode();
        this.password = user.getPassword();
        this.userPhoto = user.getUserPhoto();
    }
}
