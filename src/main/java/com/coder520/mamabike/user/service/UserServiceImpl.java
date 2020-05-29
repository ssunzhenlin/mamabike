package com.coder520.mamabike.user.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.coder520.mamabike.cache.CommonCacheUtil;
import com.coder520.mamabike.common.constants.Constants;
import com.coder520.mamabike.common.exception.MaMaBikeException;
import com.coder520.mamabike.common.utils.QiniuFileUploadUtil;
import com.coder520.mamabike.common.utils.RandomNumberCode;
import com.coder520.mamabike.jms.SmsProcessor;
import com.coder520.mamabike.security.AESUtil;
import com.coder520.mamabike.security.Base64Util;
import com.coder520.mamabike.security.MD5Util;
import com.coder520.mamabike.security.RSAUtil;
import com.coder520.mamabike.user.dao.UserMapper;
import com.coder520.mamabike.user.entity.User;
import com.coder520.mamabike.user.entity.UserElement;
import lombok.extern.slf4j.Slf4j;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.jms.Destination;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by JackWangon[www.aiprogram.top] 2017/7/29.
 */
@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private CommonCacheUtil cacheUtil;

    @Autowired
    private SmsProcessor smsProcessor;

    private static final String VERIFYCODE_PREFIX = "verify.code.";
    private static final String SMS_QUEUE = "sms.queue";


    /**
     * @Author JackWang [www.coder520.com]
     * @Date 2017/7/31 23:15
     * @Description 登录业务
     */
    @Override
    @Transactional
    public String login(String data, String key) throws MaMaBikeException {

        String decryptData = null;
        String token = null;
        try {
            //RSA解密AES的key
            byte[] aesKey = RSAUtil.decryptByPrivateKey(Base64Util.decode(key));
            //AES的key解密AES加密数据
            decryptData = AESUtil.decrypt(data, new String(aesKey, "UTF-8"));
            if (decryptData == null) {
                throw new Exception();
            }
            //拿到提交数据 开始验证逻辑
            JSONObject jsonObject = JSON.parseObject(decryptData);
            String mobile = jsonObject.getString("mobile");//电话
            String code = jsonObject.getString("code");//验证码
            String platform = jsonObject.getString("platform");//机器类型
            String channelId = jsonObject.getString("channelId");//推送频道编码 单个设备唯一

            if (StringUtils.isBlank(mobile) || StringUtils.isBlank(code)||StringUtils.isBlank(platform)||StringUtils.isBlank(channelId)) {
                throw new Exception();
            }
            //去redis取验证码比较手机号码和验证码是否匹配 若匹配 说明是本人手机
            String verCode = cacheUtil.getCacheValue(mobile);
            User user = null;
            if (code.equals(verCode)) {
                //检查用户是否存在
                user = userMapper.selectByMobile(mobile);
                if (user == null) {
                    //用户不存在 帮用户注册
                    user = new User();
                    user.setMobile(mobile);
                    user.setNickname(mobile);
                    userMapper.insertSelective(user);
                }
            } else {
                throw new MaMaBikeException("手机号与验证码不匹配");
            }
            //生成token
            try {
                token = this.generateToken(user);
            } catch (Exception e) {
                throw new MaMaBikeException("fail.to.generate.token");
            }

            UserElement ue = new UserElement();
            ue.setMobile(mobile);
            ue.setUserId(user.getId());
            ue.setToken(token);
            ue.setPlatform(platform);
            ue.setPushChannelId(channelId);
            cacheUtil.putTokenWhenLogin(ue);


        } catch (Exception e) {
            log.error("Fail to decrypt data", e);
            throw new MaMaBikeException("数据解析错误");
        }

        return token;
    }

    /**
     * @Author JackWang [www.coder520.com]
     * @Date 2017/8/4 11:49
     * @Description 修改用户昵称
     */

    @Transactional
    @Override
    public void modifyNickName(User user) throws MaMaBikeException {
        userMapper.updateByPrimaryKeySelective(user);
    }

    /**
     *@Author JackWang [www.coder520.com]
     *@Date 2017/8/5 17:22
     *@Description 上传头像
     */
    @Transactional
    @Override
    public String uploadHeadImg(MultipartFile file, long userId) throws MaMaBikeException {

        try {
            //获取user 得到原来的头像地址
            User user = userMapper.selectByPrimaryKey(userId);
            // 调用七牛
            String imgUrlName = QiniuFileUploadUtil.uploadHeadImg(file);
            user.setHeadImg(imgUrlName);
            //更新用户头像URL
            userMapper.updateByPrimaryKeySelective(user);
            return Constants.QINIU_HEAD_IMG_BUCKET_URL+"/"+Constants.QINIU_HEAD_IMG_BUCKET_NAME+"/"+imgUrlName;
        } catch (Exception e) {
            log.error(e.getMessage(),e);
            throw new MaMaBikeException("头像上传失败");
        }
    }

    /**
     *@Author JackWang [www.coder520.com]
     *@Date 2017/8/5 17:27
     *@Description 发送验证码
     */
    @Override
    public void sendVercode(String mobile,String ip) throws MaMaBikeException {
        String verCode = RandomNumberCode.verCode();
        //先存redis  reids缓存检查是否恶意请求 决定是否真的发送验证码
        int result = cacheUtil.cacheForVerificationCode(VERIFYCODE_PREFIX+mobile,verCode,"reg",60,ip);
        if (result == 1) {
            log.info("当前验证码未过期，请稍后重试");
            throw new MaMaBikeException("当前验证码未过期，请稍后重试");
        } else if (result == 2) {
            log.info("超过当日验证码次数上线");
            throw new MaMaBikeException("超过当日验证码次数上限");
        } else if (result == 3) {
            log.info("超过当日验证码次数上限 {}", ip);
            throw new MaMaBikeException(ip + "超过当日验证码次数上限");
        }
        log.info("Sending verify code {} for phone {}", verCode, mobile);
        //验证码推送到队列
        Destination destination = new ActiveMQQueue(SMS_QUEUE);
        Map<String,String> smsParam = new HashMap<>();
        smsParam.put("mobile",mobile);
        smsParam.put("tplId",Constants.MDSMS_VERCODE_TPLID);
        smsParam.put("vercode",verCode);
        String message = JSON.toJSONString(smsParam);
        smsProcessor.sendSmsToQueue(destination,message);
    }


    private String generateToken(User user)
            throws Exception {
        String source = user.getId() + ":" + user.getMobile() + System.currentTimeMillis();
        return MD5Util.getMD5(source);
    }
}
