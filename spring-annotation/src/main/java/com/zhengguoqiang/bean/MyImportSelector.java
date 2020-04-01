package com.zhengguoqiang.bean;

import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

/**
 * @author zhengguoqiang
 */
public class MyImportSelector implements ImportSelector {
    @Override
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
        //不能返回null，否则将会NPE
        return new String[]{"com.zhengguoqiang.bean.Red"};
    }
}
