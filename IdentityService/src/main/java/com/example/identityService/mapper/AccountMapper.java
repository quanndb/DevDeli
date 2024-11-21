package com.example.identityService.mapper;

import com.example.identityService.DTO.request.RegisterRequestDTO;
import com.example.identityService.DTO.request.UpdateProfileRequestDTO;
import com.example.identityService.entity.Account;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface AccountMapper {

    Account toAccount(RegisterRequestDTO request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateAccount(@MappingTarget Account response, UpdateProfileRequestDTO request);
}
