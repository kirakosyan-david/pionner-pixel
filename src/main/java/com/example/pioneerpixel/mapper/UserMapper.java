package com.example.pioneerpixel.mapper;

import com.example.pioneerpixel.dto.SearchUserDtoResponse;
import com.example.pioneerpixel.dto.UserContactDtoResponse;
import com.example.pioneerpixel.dto.UserDtoResponse;
import com.example.pioneerpixel.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mappings({
            @Mapping(source = "user.name", target = "name"),
            @Mapping(source = "user.dateOfBirth", target = "dateOfBirth"),
            @Mapping(source = "user.account.balance", target = "balance"),
            @Mapping(target = "email", expression = "java(user.getEmails().isEmpty() ? null : user.getEmails().get(0).getEmail())"),
            @Mapping(target = "phone", expression = "java(user.getPhones().isEmpty() ? null : user.getPhones().get(0).getPhone())")
    })
    UserDtoResponse mapToUserDtoResponse(User user);


    @Mappings({
            @Mapping(source = "user.name", target = "name"),
            @Mapping(target = "email", expression = "java(user.getEmails().isEmpty() ? null : user.getEmails().get(0).getEmail())"),
            @Mapping(target = "phone", expression = "java(user.getPhones().isEmpty() ? null : user.getPhones().get(0).getPhone())")
    })
    UserContactDtoResponse mapToUserContactDtoResponse(User user);

    @Mapping(source = "user.name", target = "name")
    @Mapping(source = "user.dateOfBirth", target = "dateOfBirth", qualifiedByName = "formatDate")
    @Mapping(target = "email", expression = "java(user.getEmails().isEmpty() ? null : user.getEmails().iterator().next().getEmail())")
    @Mapping(target = "phone", expression = "java(user.getPhones().isEmpty() ? null : user.getPhones().iterator().next().getPhone())")
    @Mapping(source = "page", target = "page")
    @Mapping(source = "size", target = "size")
    SearchUserDtoResponse mapToSearchUserDtoResponse(User user, int page, int size);

    @Named("formatDate")
    default String formatDate(LocalDate dateOfBirth) {
        return dateOfBirth != null ? DateTimeFormatter.ISO_LOCAL_DATE.format(dateOfBirth) : null;
    }
}
