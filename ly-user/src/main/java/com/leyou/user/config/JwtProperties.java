package com.leyou.user.config;

import com.leyou.common.auth.utils.RsaUtils;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.annotation.PostConstruct;
import java.security.PublicKey;

@Data
@ConfigurationProperties(prefix = "ly.jwt")
public class JwtProperties {

    private String pubKeyPath;

    private PublicKey publicKey;


    private AppTokenPojo app = new AppTokenPojo();

    @Data
    public class AppTokenPojo{
        private Long id;
        private String serviceName;
    }

    /**
     * 指定初始化方法
     * @throws Exception
     */
    @PostConstruct
    public void initMethod() throws Exception {
        this.publicKey = RsaUtils.getPublicKey(pubKeyPath);
    }

}