package com.puyixiaowo.fbook.utils.sign;

import com.puyixiaowo.fbook.utils.StringUtils;
import org.apache.commons.codec.digest.DigestUtils;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.io.UnsupportedEncodingException;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.*;

public class SignUtils {

	private static String CHARSET = "UTF-8";
	private static BASE64Encoder encoder = new BASE64Encoder();
	private static BASE64Decoder decoder = new BASE64Decoder();


	public static String sign(String privateKey,
											Map<String, String> params) {

		privateKey = StringUtils.replaceBlank(privateKey);
		String prestr = createLinkString(params);
		String md = DigestUtils.md5Hex(getContentBytes(prestr, CHARSET));
		String mysign = buildSign(md, privateKey);
		return mysign;
	}

	public static String createLinkString(Map<String, String> params) {
		List<String> keys = new ArrayList<>(params.keySet());
		Collections.sort(keys);
		String prestr = "";
		for (int i = 0; i < keys.size(); i++) {
			String key = keys.get(i);
			String value = params.get(key);
			if(value==null || "".equals(value.trim())) {
				continue;
			}
			if(!"sign".equals(key)) {
				prestr = prestr + key + "=" + value + "&";
			}
		}
		return prestr.substring(0, prestr.length()-1);
	}

	private static String buildSign(String content, String privateKey) {
		try {
			PKCS8EncodedKeySpec priPKCS8 = new PKCS8EncodedKeySpec(decoder.decodeBuffer(privateKey));
			KeyFactory keyf = KeyFactory.getInstance("RSA");
			PrivateKey priKey = keyf.generatePrivate(priPKCS8);

			java.security.Signature signature = java.security.Signature.getInstance("SHA1WithRSA");

			signature.initSign(priKey);
			signature.update(content.getBytes(CHARSET));

			byte[] signed = signature.sign();

			return encoder.encode(signed);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private static byte[] getContentBytes(String content, String charset) {
		if (charset == null || "".equals(charset)) {
			return content.getBytes();
		}
		try {
			return content.getBytes(charset);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("MD5签名过程中出现错误,指定的编码集不对,您目前指定的编码集是:" + charset);
		}
	}

	/**
	 *
	 * @param sPara 签名参数
	 * @param sign 签名字符串
	 * @param publicKey 公钥
	 * @param input_charset 字符编码
	 * @return
	 */
	public static boolean verify(Map<String, String> sPara, String sign, String publicKey) {

		publicKey = StringUtils.replaceBlank(publicKey);
		boolean flag = false;
		String prestr = createLinkString(sPara);
		String md = DigestUtils.md5Hex(getContentBytes(prestr, CHARSET));
		flag = verify(md, sign, publicKey);
		return flag;
	}

	private static boolean verify(String content, String sign, String publicKey) {
		try {
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			byte[] encodedKey = decoder.decodeBuffer(publicKey);
			PublicKey pubKey = keyFactory.generatePublic(new X509EncodedKeySpec(encodedKey));

			java.security.Signature signature = java.security.Signature.getInstance("SHA1WithRSA");

			signature.initVerify(pubKey);
			signature.update(content.getBytes(CHARSET));

			boolean bverify = signature.verify(decoder.decodeBuffer(sign));
			return bverify;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}
}
