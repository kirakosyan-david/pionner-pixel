package com.example.pioneerpixel.mapper;

import com.example.pioneerpixel.dto.UserPhoneDtoResponse;
import com.example.pioneerpixel.entity.PhoneData;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PhoneDataMapper {

  @Mapping(
      target = "name",
      expression = "java(phoneData.getUser() != null ? phoneData.getUser().getName() : null)")
  @Mapping(source = "phoneData.phone", target = "phone")
  UserPhoneDtoResponse mapToUserPhoneDtoResponse(PhoneData phoneData);
}
