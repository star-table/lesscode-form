package com.polaris.lesscode.form.socket.memo;

import com.polaris.lesscode.form.socket.resp.AppMemoSocketResp;
import lombok.extern.slf4j.Slf4j;

import javax.websocket.EncodeException;
import javax.websocket.Session;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author: Liu.B.J
 * @date: 2020/11/29 10:44
 * @description:
 */
@Slf4j
public class RemindTask extends TimerTask {

    private Timer timer;

    private AppMemoSocketResp msg;

    private Session session;

    public RemindTask(Timer timer, AppMemoSocketResp msg, Session session){
        this.timer = timer;
        this.msg = msg;
        this.session = session;
    }

    public void run() {
        log.info("{}->闹钟时间到", msg);
        sendMessage(msg, session);
        this.timer.cancel();
    }

    private void sendMessage(AppMemoSocketResp msg, Session session) {
        try {
            session.getBasicRemote().sendObject(msg);
            log.info("闹钟推送成功，消息为->{}", msg);
        } catch (IOException | EncodeException e) {
            e.printStackTrace();
        }
    }

}
