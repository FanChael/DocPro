package com.skl.basemodule.common_class;

/**
*@Author: hl
*@Date: created at 2019/10/8 10:52
*@Description: Eventbus公共数据事件类
*/
public class MessageEvent {
    private String key;
    private Object content;

    public MessageEvent(String key, Object content) {
        this.key = key;
        this.content = content;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Object getContent() {
        return content;
    }

    public void setContent(Object content) {
        this.content = content;
    }
}
