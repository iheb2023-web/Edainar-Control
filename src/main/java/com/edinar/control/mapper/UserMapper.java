package com.edinar.control.mapper;

import com.edinar.control.domain.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Optional;
import java.util.UUID;

@Mapper
public interface UserMapper {

    int insert(User user);

    Optional<User> findById(@Param("id") UUID id);

    Optional<User> findFirst();

    Optional<User> findByEmail(@Param("email") String email);

    int updatePassword(
        @Param("id") UUID id,
        @Param("passwordHash") String passwordHash,
        @Param("updatedAt") java.time.OffsetDateTime updatedAt
    );

    int updateEmail(
        @Param("id") UUID id,
        @Param("email") String email,
        @Param("updatedAt") java.time.OffsetDateTime updatedAt
    );
}
