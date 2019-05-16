//package com.nju.yanjunjie.readinglaterpushingsystem.user;
//
//import android.util.Base64;
//
//import java.io.UnsupportedEncodingException;
//import java.security.InvalidKeyException;
//import java.security.NoSuchAlgorithmException;
//import java.util.Map;
//
//import javax.crypto.BadPaddingException;
//import javax.crypto.Cipher;
//import javax.crypto.IllegalBlockSizeException;
//import javax.crypto.NoSuchPaddingException;
//import javax.crypto.spec.SecretKeySpec;
//
//public class EncryptUtil {
//
//    private static final String ALGORITHM = "AES/ECB/PKCS5Padding";
//
//    // 加密秘钥
//    private static final String AES_KEY = "Super-winner/168";
//
//    private static SecretKeySpec secretKeySpec;
//
//    /**
//     * 前台传输数据解密
//     *
//     * @param rawJson 原始JSON
//     * @return 解密后的Map
//     */
//    //其中的BaseResult也在下面
//    public static <T extends BaseResult> T decrypt(String rawJson, Class<T> tClass) {
//
//        T result=null;
//
//        try {
//            Cipher cipher = Cipher.getInstance(ALGORITHM);
//            cipher.init(Cipher.DECRYPT_MODE, getAesKey());
//            byte[] paramBytes = cipher.doFinal(Base64.decode(rawJson.getBytes("UTF-8"), Base64.NO_WRAP));
//            String paramJson = new String(paramBytes);
//            result = GsonUtil.fromJson(paramJson, tClass);
//        } catch (NoSuchPaddingException e) {
//            e.printStackTrace();
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        } catch (InvalidKeyException e) {
//            e.printStackTrace();
//        } catch (BadPaddingException e) {
//            e.printStackTrace();
//        } catch (IllegalBlockSizeException e) {
//            e.printStackTrace();
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//
//        return result;
//    }
//
//    /**
//     * 数据传输过程中需要加密设置
//     * @param rawMap
//     * @return
//     */
//
//    public static String encrypt(Map<String, String> rawMap) {
//        String result = "";
//
//        try {
//            Cipher cipher = Cipher.getInstance(ALGORITHM);
//            cipher.init(Cipher.ENCRYPT_MODE, getAesKey());
//
//            String rawJson = GsonUtil.toJson(rawMap);
//            byte[] paramBytes = cipher.doFinal(rawJson.getBytes("UTF-8"));
//            result = Base64.encodeToString(paramBytes, Base64.NO_WRAP);
//        } catch (NoSuchPaddingException e) {
//            e.printStackTrace();
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        } catch (InvalidKeyException e) {
//            e.printStackTrace();
//        } catch (BadPaddingException e) {
//            e.printStackTrace();
//        } catch (IllegalBlockSizeException e) {
//            e.printStackTrace();
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//
//        return result;
//    }
//
//    private static SecretKeySpec getAesKey() {
//        if (secretKeySpec != null) {
//            return secretKeySpec;
//        }
//        try {
//            secretKeySpec = new SecretKeySpec(AES_KEY.getBytes("UTF-8"), "AES");
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//
//        return secretKeySpec;
//    }
//}
