package com.vesystem.version.module.mapper;

import com.vesystem.version.module.dto.UserDto;
import com.vesystem.version.module.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author hcy
 * @since 2020-08-24
 */
@Repository
public interface UserMapper extends BaseMapper<User> {

    @Select(" SELECT * FROM `user` WHERE username = #{username} ")
    UserDto selectUserDetilsByUsername(String username);

}
