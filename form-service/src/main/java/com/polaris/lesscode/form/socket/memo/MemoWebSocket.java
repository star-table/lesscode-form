package com.polaris.lesscode.form.socket.memo;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.polaris.lesscode.consts.CommonConsts;
import com.polaris.lesscode.dc.internal.dsl.*;
import com.polaris.lesscode.dc.internal.feign.DataCenterProvider;
import com.polaris.lesscode.dc.internal.req.ValueFilterReq;
import com.polaris.lesscode.form.bo.MemoSocketObject;
import com.polaris.lesscode.form.config.MyEndpointConfigure;
import com.polaris.lesscode.form.entity.AppForm;
import com.polaris.lesscode.form.mapper.AppFormMapper;
import com.polaris.lesscode.form.socket.resp.AppMemoSocketResp;
import com.polaris.lesscode.form.util.DslUtil;
import com.polaris.lesscode.form.vo.EncoderAppMemoResp;
import com.polaris.lesscode.uc.internal.feign.UserCenterProvider;
import com.polaris.lesscode.uc.internal.resp.UserAuthResp;
import com.polaris.lesscode.util.DataSourceUtil;
import com.polaris.lesscode.util.DateTimeFormatterUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Timer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author: Liu.B.J
 * @data: 2020/10/28 13:57
 * @description:
 */
@Slf4j
@Component
@ServerEndpoint(value = "/form/websocket/memo/{appId}/{token}", encoders = {EncoderAppMemoResp.class}, configurator = MyEndpointConfigure.class)
public class MemoWebSocket {

    @Autowired
    private AppFormMapper appFormMapper;

    @Autowired
    private UserCenterProvider userCenterProvider;

    @Autowired
    private DataCenterProvider dataCenterProvider;

    private static final int size = 100;

    private static AtomicInteger onlineCount = new AtomicInteger();

    private static ConcurrentHashMap<String, MemoSocketObject> webSocketMap = new ConcurrentHashMap<>();

    private static ConcurrentHashMap<String,String> webSocketSessionMap = new ConcurrentHashMap<>();

    //private static CopyOnWriteArraySet<MemoWebSocket> webSocketSet = new CopyOnWriteArraySet<>();

    @OnOpen
    public void OnOpen(Session session, @PathParam(value = "appId") Long appId, @PathParam(value = "token") String token){
        UserAuthResp userAuthResp = userCenterProvider.authCheckStatus(token).getData();
        MemoSocketObject memoSocket = new MemoSocketObject(session, token);
        if(webSocketMap.containsKey(token)){
            String oldSessionId = webSocketMap.get(token).getSession().getId();
            // close要用
            webSocketSessionMap.remove(oldSessionId);
            webSocketSessionMap.put(session.getId(), token);

            webSocketMap.remove(token);
        }else{
            webSocketSessionMap.put(session.getId(), token);
            onlineCount.addAndGet(1);
        }
        log.info("用户连接:"+token+",当前在线人数为:" + onlineCount);
        if(appId != null){
            AppForm appForm = appFormMapper.selectOne(new LambdaQueryWrapper<AppForm>().eq(AppForm :: getAppId, appId).eq(AppForm :: getDelFlag, CommonConsts.NO_DELETE).last(" limit 1"));
            if(appForm != null){
                send(appForm, Optional.ofNullable(userAuthResp.getUserId()).orElse(0L), session, token, memoSocket);
            }
        }
    }

    private void send(AppForm appForm, Long userId, Session session, String token, MemoSocketObject memoSocket){
        int page = 1;
        int offset = 0;

        Condition condition = new Condition();
        condition.setType(Conditions.AND);
        Condition[] conds = new Condition[1];
        Condition subCondition = new Condition();
        subCondition.setType(Conditions.EQUAL);
        subCondition.setColumn("creator");
        subCondition.setValue(String.valueOf(userId));
        conds[0] = subCondition;
        condition.setConds(conds);

        List<Order> orders = new ArrayList<>();
        Order order = new Order("id", false);
        orders.add(order);


        boolean flag = true;
        while(flag){
            offset = (page - 1) * size;

            Query query = Query.select()
                    .from(new Table(SqlUtil.wrapperTableName(appForm.getOrgId(), appForm.getId())))
                    .where(DslUtil.getWrapperCondition(condition))
                    .limit(offset, size)
                    .orders(DslUtil.convertOrders(orders));
            log.info(">>>>>>>>>>>>>>>MemoWebSocket>>>>>>>>>>>>>>>>>>>>>>>>>>query="+ JSON.toJSONString(query));

            List<Map<String, Object>> data = dataCenterProvider.query(DataSourceUtil.getDsId(),
                    DataSourceUtil.getDbId(), query).getData();
            if(! CollectionUtils.isEmpty(data)){
                List<AppMemoSocketResp> memos = new ArrayList<>();
                Date now = new Date();
                for (Map<String, Object> memoMap : data) {
                    AppMemoSocketResp appMemoSocketResp = new AppMemoSocketResp();
                    appMemoSocketResp.setId(String.valueOf(memoMap.get("id")));
                    appMemoSocketResp.setTitle(Optional.ofNullable((String) memoMap.get("title")).orElse(""));
                    if(! StringUtils.isBlank((String) memoMap.get("alarmTime")) && DateTimeFormatterUtils.parseDateTime((String) memoMap.get("alarmTime")).isAfter(now.toInstant())){
                        appMemoSocketResp.setAlarmTime((String) memoMap.get("alarmTime"));
                        memos.add(appMemoSocketResp);
                    }
                }
                if(! CollectionUtils.isEmpty(memos)){
                    for (AppMemoSocketResp memo : memos) {
                        Timer timer = new Timer();
                        timer.schedule(new RemindTask(timer, memo, session), Date.from(DateTimeFormatterUtils.parseDateTime(memo.getAlarmTime())));
                        memoSocket.getTimerList().add(timer);
                    }
                }
                if(data.size() < size){
                    flag = false;
                }
            }else{
                flag = false;
            }
            page++;
        }
        webSocketMap.put(token, memoSocket);

    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void OnClose(Session session){
        String token = webSocketSessionMap.get(session.getId());
        if(! StringUtils.isBlank(token)){
            MemoSocketObject mso = webSocketMap.get(token);
            if(mso != null){
                for (Timer timer : mso.getTimerList()) {
                    timer.cancel();
                }
                webSocketMap.remove(token);
                webSocketSessionMap.remove(session.getId());
                onlineCount.getAndDecrement();
                log.info("[MemoWebSocket]用户退出:"+token+",当前在线人数为:" + onlineCount);
            }
        }
    }

    /**
     * 收到客户端消息后调用的方法
     * * @param message     客户端发送过来的消息
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        //log.info("[MemoWebSocket] 收到客户端消息 message={}，session={}", message, session);
    }

    /**
     * 发生错误时调用
     */
    @OnError
    public void onError(Session session, Throwable error) {
        log.error("[MemoWebSocket]出现错误");
        error.printStackTrace();
    }

}
