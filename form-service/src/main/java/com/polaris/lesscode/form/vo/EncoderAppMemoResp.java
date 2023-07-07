package com.polaris.lesscode.form.vo;

import com.alibaba.fastjson.JSON;
import com.polaris.lesscode.form.socket.resp.AppMemoSocketResp;

import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

/**
 * @author: Liu.B.J
 * @data: 2020/10/28 19:29
 * @description:
 */
public class EncoderAppMemoResp implements Encoder.Text<AppMemoSocketResp>{

    @Override
    public String encode(AppMemoSocketResp resp) {
        String json= JSON.toJSONString(resp);
        return  json;
    }

    @Override
    public void init(EndpointConfig endpointConfig) {

    }

    @Override
    public void destroy() {

    }
}
