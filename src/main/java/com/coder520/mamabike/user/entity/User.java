package com.coder520.mamabike.user.entity;

import lombok.Data;
import sun.misc.BASE64Encoder;

import java.io.*;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

@Data
public class User {

    private Long id;

    private String nickname;

    private String mobile;

    private String headImg;

    private Byte verifyFlag;

    private Byte enableFlag;

}