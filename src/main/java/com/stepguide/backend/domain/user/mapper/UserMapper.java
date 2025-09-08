package com.stepguide.backend.domain.user.mapper;

import com.stepguide.backend.domain.user.dto.UserDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {

    Long findIdByProviderAndProviderId(@Param("provider") String provider,
                                       @Param("providerId") String providerId);

    void insertUser(UserDTO user); // useGeneratedKeys는 XML에서 설정
    String findUsernameById(@Param("userId") Long userId);
    UserDTO findById(@Param("userId") Long userId);
}