package com.polaris.lesscode.form.bo;

import lombok.Data;

import javax.websocket.Session;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

/**
 * @author: Liu.B.J
 * @date: 2020/11/27 19:46
 * @description:
 */
@Data
public class MemoSocketObject {

    private Session session;

    private String token;

    private List<Timer> timerList = new ArrayList<>();

    public MemoSocketObject(Session session, String token){
        this.session = session;
        this.token = token;
    }

}
