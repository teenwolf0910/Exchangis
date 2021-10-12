package com.webank.wedatasphere.exchangis.ctyun.sso;

import com.webank.wedatasphere.exchangis.auth.AuthController;
import com.webank.wedatasphere.exchangis.common.auth.AuthConfiguration;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Date;

public class CasValidate {
    private static Logger LOG = LoggerFactory.getLogger(CasValidate.class);

    // http://ai.ctyun.cn:8088/api/rest_j/v1/application/ssologin

    public static JSONObject validate(String ticket, AuthConfiguration conf) throws IOException, JSONException {

        String appId = conf.getAppId();
        String appSecret = conf.getAppSecret();
        String service = conf.getctyunCasLoginUrl();

        HmacSHA256 coder = new HmacSHA256();
        String now = new Date().getTime()+"";
        String toSign = appId + "@" +
                service + "@" +
                ticket + "@" +
                now;
        String signed = coder.encode(toSign,appSecret);

        StringBuffer url = new StringBuffer();
        url.append(conf.getCtyunCasUrl()+service+"&ticket="+ticket).append("&")
                .append("appId=").append(appId).append("&")
                .append("timestamp=").append(now).append("&")
                .append("signature=").append(signed);

        LOG.info("url:"+url);
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpget = new HttpGet(url.toString());
        CloseableHttpResponse response = null;
        JSONObject jsonObj = null;
        try {
            response = httpclient.execute(httpget);
            if (response.getStatusLine().getStatusCode() == 200) {
                String content = EntityUtils.toString(response.getEntity(), "UTF-8");
                jsonObj = XML.toJSONObject(content);
                if(jsonObj.getJSONObject("cas:serviceResponse").has("cas:authenticationSuccess")){
                  JSONObject userInfo = jsonObj.getJSONObject("cas:serviceResponse").getJSONObject("cas:authenticationSuccess").getJSONObject("cas:attributes");
                    LOG.info("验证成功：userInfo:"+userInfo);
                  if(userInfo != null){
                      return userInfo;
                  }
                }else {
                    LOG.error("验证失败："+ jsonObj.getJSONObject("cas:serviceResponse"));
                }
            }
        }
        finally {
            if (response != null) {
                response.close();
            }
            httpclient.close();
        }

        return jsonObj;
    }
}
