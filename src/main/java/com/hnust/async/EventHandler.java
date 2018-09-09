package com.hnust.async;

import java.util.List;

/**
 *处理事件的接口
 */
public interface EventHandler {
    void doHandle(EventModel model);        //处理事件
    List<EventType> getSupportEventTypes();     //EventHandler 要关注的事件
}
