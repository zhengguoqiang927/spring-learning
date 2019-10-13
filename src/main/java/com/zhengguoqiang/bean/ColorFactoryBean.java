package com.zhengguoqiang.bean;

import org.springframework.beans.factory.FactoryBean;

/**
 * @author zhengguoqiang
 */
public class ColorFactoryBean implements FactoryBean<Color> {
    @Override
    public Color getObject() throws Exception {
        return new Color();
    }

    @Override
    public Class<?> getObjectType() {
        return Color.class;
    }
}
