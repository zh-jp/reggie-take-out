package com.reggie.utils;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;

import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.profile.DefaultProfile;
import lombok.extern.slf4j.Slf4j;

/**
 * 短信发送工具类
 */
@Slf4j
public class SMSUtils {

    /**
     * 发送短信
     *
     * @param signName     签名
     * @param templateCode 模板
     * @param phoneNumbers 手机号
     * @param param        参数
     */

    public static void sendMessage(String accessKeyId, String accessKeySecret, String signName,
                                   String templateCode, String phoneNumbers, String param) {

        DefaultProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId,
                accessKeySecret);
        IAcsClient client = new DefaultAcsClient(profile);

        SendSmsRequest request = new SendSmsRequest();
        request.setPhoneNumbers(phoneNumbers);
        request.setSignName(signName);
        request.setTemplateCode(templateCode);
        request.setTemplateParam("{\"code\":\"" + param + "\"}");
        request.setSysRegionId("cn-hangzhou");
        try {
            SendSmsResponse response = client.getAcsResponse(request);
            log.info("短信发送成功");
        } catch (ServerException e) {
            e.printStackTrace();
        } catch (ClientException e) {
            log.info("ErrCode:" + e.getErrCode());
            log.info("ErrMsg:" + e.getErrMsg());
            log.info("RequestId:" + e.getRequestId());
        }
    }

}
