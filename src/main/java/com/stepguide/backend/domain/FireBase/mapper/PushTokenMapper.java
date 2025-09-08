package com.stepguide.backend.domain.FireBase.mapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface PushTokenMapper {
    int upsert(@Param("userId") Long userId,
               @Param("token") String token,
               @Param("ua") String userAgent);

    int unsubscribe(@Param("userId") Long userId,
                    @Param("token") String token);

    String findLatestActiveToken(@Param("userId") Long userId);
}