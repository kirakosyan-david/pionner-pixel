package com.example.pioneerpixel.mapper;

import com.example.pioneerpixel.dto.UserEmailDtoResponse;
import com.example.pioneerpixel.entity.EmailData;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EmailDataMapper {

    @Mapping(target = "name", expression = "java(emailData.getUser() != null ? emailData.getUser().getName() : null)")
    @Mapping(source = "emailData.email", target = "email")
    UserEmailDtoResponse mapToUserPhoneDtoResponse(EmailData emailData);
}
