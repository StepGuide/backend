package com.stepguide.backend.domain.user.mapper;

import com.stepguide.backend.domain.user.dto.RefreshTokenDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;

@Mapper
public interface RefreshTokenMapper {
    void insert(RefreshTokenDTO token);
    RefreshTokenDTO findByTokenHash(@Param("tokenHash") byte[] tokenHash);
    RefreshTokenDTO findByTokenHashForUpdate(@Param("tokenHash") byte[] tokenHash);
    int revokeById(@Param("id") Long id);
    int revokeIfNotRevoked(@Param("id") Long id);
}