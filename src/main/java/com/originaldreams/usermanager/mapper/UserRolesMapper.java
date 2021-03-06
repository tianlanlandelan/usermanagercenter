package com.originaldreams.usermanager.mapper;

import com.originaldreams.usermanagercenter.entity.UserRoles;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * @author yangkaile
 * @date 2018-08-19 19:43:35
 */
@Mapper
public interface UserRolesMapper {
    String TABLE_NAME = "user_roles";

     @Select("SELECT userId, roleId, createTime FROM " + TABLE_NAME + " WHERE userId = #{userId}")
     UserRoles getByUserId(Integer userId);

     @Select("SELECT userId, roleId, createTime FROM " + TABLE_NAME + " WHERE roleId = #{roleId}")
     UserRoles getByRoleId(Integer roleId);

     @Select("SELECT userId, roleId, createTime FROM " + TABLE_NAME)
     List<UserRoles> getAll();

     @Insert("INSERT INTO " + TABLE_NAME + "(userId, roleId, createTime) VALUES (#{userId}, #{roleId}, #{createTime})")
     Integer insert(UserRoles userRoles);

     @Delete("DELETE FROM " + TABLE_NAME + " WHERE roleId = #{roleId}")
     Integer deleteByRoleId(Integer roleId);

     @Update("UPDATE " + TABLE_NAME + " SET roleId = #{roleId} , createTime = #{createTime} WHERE userId = #{userId}")
     Integer update(UserRoles userRoles);

}
