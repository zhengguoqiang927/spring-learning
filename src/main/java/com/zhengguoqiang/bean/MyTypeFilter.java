package com.zhengguoqiang.bean;

import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.TypeFilter;

import java.io.IOException;

/**
 * 自定义过滤规则
 *
 * @author zhengguoqiang
 */
public class MyTypeFilter implements TypeFilter {

    /**
     *
     * @param metadataReader 当前目标类的类元数据信息Reader类
     * @param metadataReaderFactory 获取指定类的元数据信息Reader类
     * @return
     * @throws IOException
     */
    @Override
    public boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory) throws IOException {
        //获取当前目标类的注解信息
        AnnotationMetadata annotationMetadata = metadataReader.getAnnotationMetadata();
        //获取当前目标类的类信息
        ClassMetadata classMetadata = metadataReader.getClassMetadata();
        String className = classMetadata.getClassName();
        System.out.println("----> " + className);
        if (className.contains("er")) return true;
        return false;
    }
}
