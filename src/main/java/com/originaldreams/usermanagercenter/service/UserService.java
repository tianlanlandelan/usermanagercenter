package com.originaldreams.usermanagercenter.service;

import com.originaldreams.common.encryption.MyMD5Utils;
import com.originaldreams.common.response.MyServiceResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.originaldreams.usermanagercenter.entity.User;
import com.originaldreams.usermanagercenter.mapper.UserMapper;

import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;

    private Logger logger = LoggerFactory.getLogger(UserService.class);

    /**
     * 判断是否使用密码登录
     *  如果使用密码登录，看是用户名密码、手机号密码、邮箱密码的哪种组合
     *      如果密码校验无误，返回用户ID
     *      如果密码校验失败或不支持指定的密码登录组合，则返回相应的错误信息
     *  如果不是密码登录，看是否是微信登录
     * @param user
     * @return
     */
    public MyServiceResponse logon(User user)  {
        User checker = null;
        boolean checkPassword = false;
        MyServiceResponse responseObject = new MyServiceResponse();
        if(user.getPassword() != null){   //使用密码登录
            if(user.getUserName() != null){   //用户名密码组合
                checker = userMapper.getByUserName(user);
                //判断是否允许用户使用用户名登录
                if(checker != null && checker.isPermitUserNameLogon()){
                    checkPassword = true;
                }else{
                    responseObject.setSuccess(MyServiceResponse.success_code_failed);
                    responseObject.setMessage("不支持用户名登录");
                }
            }else if(user.getPhone() != null){    //手机号密码组合
                checker = userMapper.getByPhone(user);
                //判断是否允许用户使用手机号登录
                if(checker != null && checker.isPermitPhoneLogon()){
                    checkPassword = true;

                }else{
                    responseObject.setSuccess(MyServiceResponse.success_code_failed);
                    responseObject.setMessage("不支持手机号登录");
                }
            }else if(user.getEmail() != null){    //邮箱密码组合
                checker = userMapper.getByEmail(user);
                //判断是否允许用户使用邮箱登录
                if(checker != null && checker.isPermitEmailLogon()){
                    checkPassword = true;
                }else{
                    responseObject.setSuccess(MyServiceResponse.success_code_failed);
                    responseObject.setMessage("不支持邮箱登录");
                }
            }
            try{
                //校验密码
                if(checkPassword && MyMD5Utils.checkqual(user.getPassword(),checker.getPassword())){
                    responseObject.setData(checker.getId());
                }else {
                    responseObject.setSuccess(MyServiceResponse.success_code_failed);
                    responseObject.setMessage("用户名密码错误");
                }
            }catch (Exception e){
                logger.error("校验密码异常 ",e);
                responseObject.setSuccess(MyServiceResponse.success_code_failed);
                responseObject.setMessage("用户名密码错误");
            }


        }else{
            //TODO 微信登录
        }
        return responseObject;
    }

    /**
     *  注册时，只允许手机号或者邮箱注册
     *  检查用户名、手机号、邮箱是否已被注册
     *  保证用户名、手机号、邮箱的唯一
     * @param user
     * @return
     */
    public MyServiceResponse register(User user){
        User checker;
        MyServiceResponse responseObject = new MyServiceResponse();
        responseObject.setSuccess(MyServiceResponse.success_code_failed);
        if(user.getPhone() == null && user.getEmail() == null){
            responseObject.setMessage("参数异常");
            return responseObject;
        }else if(user.getPassword() == null){
            responseObject.setMessage("密码为空");
            return responseObject;
        }
        if(user.getUserName() != null){
            checker = userMapper.getByUserName(user);
            //检查用户名是否已存在
            if(checker != null ){
                responseObject.setMessage("用户名已注册");
                return responseObject;
            }

        }else if(user.getPhone() != null){    //手机号密码组合
            checker = userMapper.getByPhone(user);
            //检查手机号是否已存在
            if(checker != null){
                responseObject.setMessage("手机号已注册");
                return responseObject;
            }
        }else if(user.getEmail() != null){    //邮箱密码组合
            checker = userMapper.getByEmail(user);
            //检查邮箱是否已存在
            if(checker != null){
                responseObject.setMessage("邮箱已注册");
                return responseObject;
            }
        }
        try {
            user.setPassword(MyMD5Utils.EncoderByMd5(user.getPassword()));
            userMapper.insert(user);
            responseObject.setSuccess(MyServiceResponse.success_code_success);
            responseObject.setData(user.getId());
            return responseObject;
        }catch (Exception e){
            logger.error("注册失败 ", e);
            responseObject.setMessage("注册失败");
            return responseObject;
        }
    }

    /**
     * 根据角色查找用户
     * @param roleId
     * @return
     */
    public MyServiceResponse getUsersByRoleId(int roleId){
        return new MyServiceResponse(userMapper.getUsersByRoleId(roleId));
    }

    public User getById(Integer id){

        return userMapper.getById(id);
    }

    public List<User> getAll(){
        return userMapper.getAll();
    }

    public Integer insert(User user){
        return userMapper.insert(user);
    }

    public Integer deleteById(Integer id){
        return userMapper.deleteById(id);
    }

    public Integer update(User user){
        return userMapper.update(user);
    }


}
