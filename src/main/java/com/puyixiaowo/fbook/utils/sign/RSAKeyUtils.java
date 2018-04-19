package com.puyixiaowo.fbook.utils.sign;

import sun.misc.BASE64Encoder;

import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.compile;

/**
 * RSA密钥生成工具
 * 因本工具类使用随机数，在tomcat下使用时需加上启动参数：
 * -Djava.security.egd=file:/dev/./urandom
 */
public class RSAKeyUtils {
  
    /** 指定加密算法为RSA */  
    private static final String ALGORITHM = "RSA";  
    /** 密钥长度，用来初始化 */  
    private static final int KEYSIZE = 1024;

    private static BASE64Encoder encoder = new BASE64Encoder();

    public static class RSAKey {
        private String privateKey;
        private String publicKey;

        public RSAKey(String privateKey, String publicKey) {
            this.privateKey = privateKey;
            this.publicKey = publicKey;
        }

        public String getPrivateKey() {
            return privateKey;
        }

        public void setPrivateKey(String privateKey) {
            this.privateKey = privateKey;
        }

        public String getPublicKey() {
            return publicKey;
        }

        public void setPublicKey(String publicKey) {
            this.publicKey = publicKey;
        }
    }

    private static String replaceBlank(String str) {
        String dest = "";
        if (str!=null) {
            Pattern p = compile("\\s*|\t|\r|\n");
            Matcher m = p.matcher(str);
            dest = m.replaceAll("");
        }
        return dest;
    }

    public static RSAKey generateRSAKey() throws NoSuchAlgorithmException {
          
        /** RSA算法要求有一个可信任的随机数源 */  
        SecureRandom secureRandom = new SecureRandom();  
          
        /** 为RSA算法创建一个KeyPairGenerator对象 */  
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(ALGORITHM);  
  
        /** 利用上面的随机数据源初始化这个KeyPairGenerator对象 */  
        keyPairGenerator.initialize(KEYSIZE, secureRandom);  
        //keyPairGenerator.initialize(KEYSIZE);  
  
        /** 生成密匙对 */  
        KeyPair keyPair = keyPairGenerator.generateKeyPair();  
  
        /** 得到公钥 */  
        Key publicKey = keyPair.getPublic();  
  
        /** 得到私钥 */  
        Key privateKey = keyPair.getPrivate();  
  
        byte[] publicKeyBytes = publicKey.getEncoded();  
        byte[] privateKeyBytes = privateKey.getEncoded();  

        String publicKeyBase64 = encoder.encode(publicKeyBytes);
        String privateKeyBase64 = encoder.encode(privateKeyBytes);

        //java语言需要pkcs8
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
        try {
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
            PrivateKey privateKey2 = keyFactory.generatePrivate(keySpec);
            privateKeyBase64 = encoder.encode(privateKey2.getEncoded());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new RSAKey(privateKeyBase64, publicKeyBase64);
    }

    /**
     * 测试用
     * @param privateKey
     * @param publicKey
     * @return
     */
    private static boolean testSign(String privateKey, String publicKey) {

        Map<String, String> sPara = new HashMap<>();
        sPara.put("name", "你猜啊");
        sPara.put("type", "workbook");
        String sign = SignUtils.sign(privateKey, sPara);

        return SignUtils.verify(sPara, sign, publicKey);
    }

    public static void main(String[] args) throws Exception {
        RSAKey rsaKey = generateRSAKey();

        System.out.println(testSign(rsaKey.getPrivateKey(), rsaKey.getPublicKey()));
    }
}  