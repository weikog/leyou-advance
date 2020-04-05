package com.leyou.common.auth.utils;

import org.junit.Test;

import java.security.PrivateKey;
import java.security.PublicKey;

import static org.junit.Assert.*;

public class RsaUtilsTest {

    private String pubPath = "D:\\ly_guangzhou126\\software\\rsa_key\\jwt_key.pub";

    private String priPath = "D:\\ly_guangzhou126\\software\\rsa_key\\jwt_key";

    @Test
    public void generateKey() throws Exception {
        //参数一：给公钥指定路径和名称，参数二：给私钥指定路径和名称，参数三：盐，参数四：密钥大小
        RsaUtils.generateKey(pubPath, priPath, "heima", 2048);
    }

    @Test
    public void getKey() throws Exception {
        PublicKey publicKey = RsaUtils.getPublicKey(pubPath);
        System.out.println(publicKey);
        PrivateKey privateKey = RsaUtils.getPrivateKey(priPath);
        System.out.println(privateKey);
    }
}