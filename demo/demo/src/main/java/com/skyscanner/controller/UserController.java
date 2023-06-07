package com.skyscanner.controller;
import com.skyscanner.entity.User;
import com.skyscanner.payload.UserDto;
import com.skyscanner.service.UserService;
import lombok.var;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private UserService userService;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public UserController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping(value = "/register", consumes = {"multipart/form-data"})
    public ResponseEntity<UserDto> saveUser(@RequestParam("firstName") String firstName,
                                            @RequestParam("lastName") String lastName,
                                            @RequestParam("city") String city,
                                            @RequestParam("email") String email,
                                            @RequestParam("mobile") String mobile,
                                            @RequestParam("state") String state,
                                            @RequestParam("country") String country,
                                            @RequestParam("pinCode") String pinCode,
                                            @RequestParam("password") String password,
                                            @RequestParam("file") MultipartFile file) throws IOException {
        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setCity(city);
        user.setEmail(email);
        user.setMobile(mobile);
        user.setState(state);
        user.setCountry(country);
        user.setPinCode(pinCode);
        user.setPassword(passwordEncoder.encode(password));

        if (file != null && !file.isEmpty()) {
            String uniqueFilename = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();

            String rootPath = "D:\\skyscanner\\demo\\demo\\src\\main\\resources\\static\\images";
            File photoFile = new File(rootPath + uniqueFilename);
            file.transferTo(photoFile);
            user.setUserPhoto(uniqueFilename);
        }

        User savedUser = userService.saveUser(user);

        UserDto savedUserDto = new UserDto(savedUser);
        savedUserDto.setPassword(null);
        savedUserDto.setUserPhoto(null);
        var uri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/user/{id}")
                .buildAndExpand(savedUserDto.getId())
                .toUri();
        return ResponseEntity.created(uri).body(savedUserDto);
    }
    //http://localhost:8080/api/users/download/excel
    @GetMapping(value = "/download/excel", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<byte[]> downloadUsersAsExcel(HttpServletRequest request) throws IOException {
        List<User> users = userService.getAllUsers();

        // Create Excel workbook and sheet
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Users");

        // Create header row
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("ID");
        headerRow.createCell(1).setCellValue("First Name");
        headerRow.createCell(2).setCellValue("Last Name");
        headerRow.createCell(3).setCellValue("City");
        headerRow.createCell(4).setCellValue("Email");
        headerRow.createCell(5).setCellValue("Mobile");
        headerRow.createCell(6).setCellValue("State");
        headerRow.createCell(7).setCellValue("Country");
        headerRow.createCell(8).setCellValue("Pin Code");

        // Create data rows
        int rowNum = 1;
        for (User user : users) {
            Row dataRow = sheet.createRow(rowNum++);
            dataRow.createCell(0).setCellValue(user.getId());
            dataRow.createCell(1).setCellValue(user.getFirstName());
            dataRow.createCell(2).setCellValue(user.getLastName());
            dataRow.createCell(3).setCellValue(user.getCity());
            dataRow.createCell(4).setCellValue(user.getEmail());
            dataRow.createCell(5).setCellValue(user.getMobile());
            dataRow.createCell(6).setCellValue(user.getState());
            dataRow.createCell(7).setCellValue(user.getCountry());
            dataRow.createCell(8).setCellValue(user.getPinCode());
        }

        // Auto-size columns
        for (int i = 0; i < 9; i++) {
            sheet.autoSizeColumn(i);
        }

        // Write workbook to ByteArrayOutputStream
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);

        // Set response headers
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=users.xlsx");

        return ResponseEntity
                .ok()
                .headers(headers)
                .body(outputStream.toByteArray());
    }

}
