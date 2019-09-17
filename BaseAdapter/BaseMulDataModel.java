package com.xxxx.app.base.adapter;

import com.xxxx.app.anotation.NotProguard;

/**
 * Created by hl on 2018/3/14.
 */
@NotProguard
public abstract class BaseMulDataModel {
    private int drawType = 0;

    public int getDrawType() {
        return drawType;
    }

    public void setDrawType(int drawType) {
        this.drawType = drawType;
    }
}
