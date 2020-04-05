package com.leyou.upload.config;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author 黑马程序员
 */
@Configuration
public class OSSConfig {

    /**
     * @Bean 注解表示把当前方法的返回值对象放入到IOC容器中
     * 如果当前方法有参数，spring会在IOC容器中寻找同类型的对象给其传参
     * 如果找到了多个，可以通过@Qualifier("name")注解按照名称找对象给其传参
     * @param prop
     * @return
     */
    @Bean
    public OSS client(OSSProperties prop){
        return new OSSClientBuilder()
                .build(prop.getEndpoint(), prop.getAccessKeyId(), prop.getAccessKeySecret());
    }
}