package com.originaldreams.usermanager.mapper;

import com.originaldreams.usermanagercenter.entity.User;
import com.originaldreams.usermanager.view.UserView;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserMapper {
    String TABLE_NAME = "user";
    String USER_ROLES = "user_roles";

     @Select("SELECT id, userName, phone, wxId, email, password, createTime, mask,isDelete FROM " + TABLE_NAME
             + " WHERE id = #{id} AND isDelete = 0")
     User getById(Integer Id);

     @Select("SELECT id, userName, phone, wxId, email, password, createTime, mask,isDelete FROM " + TABLE_NAME
             + " WHERE isDelete = 0 ")
     List<User> getAll();

     @Insert("INSERT INTO " + TABLE_NAME + "(userName, phone, wxId, email, password, createTime, mask,isDelete) "
             + " VALUES (#{userName}, #{phone}, #{wxId}, #{email}, #{password}, #{createTime}, #{mask},#{isDelete})")
     @Options(useGeneratedKeys = true)
     Integer insert(User user);

     @Delete("UPDATE " + TABLE_NAME + " SET isDelete = 1 , WHERE id = #{id}")
     Integer deleteById(Integer id);
     @Update("UPDATE " + TABLE_NAME
             + " SET userName=#{userName}, phone=#{phone}, wxId=#{wxId}, email=#{email}, password=#{password}, createTime=#{createTime}, mask=#{mask} "
             + "WHERE id = #{id}")
     Integer update(User user);

     @Select("SELECT id, userName, phone, wxId, email, password, createTime, mask,isDelete FROM " + TABLE_NAME
             + " WHERE userName = #{userName} AND isDelete = #{isDelete}")
     User getByUserName(User user);

     @Select("SELECT id, userName, phone, wxId, email, password, createTime, mask,isDelete FROM " + TABLE_NAME
             + " WHERE phone = #{phone} AND isDelete = #{isDelete}")
     User getByPhone(User user);

     @Select("SELECT id, userName, phone, wxId, email, password, createTime, mask,isDelete FROM " + TABLE_NAME
             + " WHERE wxId = #{wxId} AND isDelete = #{isDelete}")
     User getByWXId(User user);

     @Select("SELECT id, userName, phone, wxId, email, password, createTime, mask,isDelete FROM " + TABLE_NAME
             + " WHERE email = #{email} AND isDelete = #{isDelete}")
     User getByEmail(User user);

     @Select("SELECT b.id, b.userName, b.phone, b.createTime, b.email, b.createTime, b.mask, b.isDelete "
             + " FROM " + USER_ROLES + " a, "+ TABLE_NAME + " b "
             + " WHERE a.userId =b.id  AND a.roleId = #{roleId}" )
     List<User> getUsersByRoleId(Integer roleId);

     @Select("select u.id as userId,u.userName as userName,r.name as roleName,u.createTime as createTime from user u,user_roles ur,role r where u.id = ur.userId and r.id = ur.roleId")
     List<UserView> getAllUserNameAndRoleName();
}
