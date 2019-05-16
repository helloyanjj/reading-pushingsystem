//package com.nju.yanjunjie.readinglaterpushingsystem.user;
//
//public class APIUtil {
//
//    public interface CallBack<T extends BaseResult> {
//        void handleResult(T result);
//    }
//
//    /**
//     * 保存用户打开APP的时候的token信息
//     * @param token 用户
//     */
//    public static void saveToken(String token) {
//        RuntimeConfig.token = token;
//        String absolutePath = WinnerApplication.getContext().getCacheDir().getAbsolutePath();
//        AndroidFileUtil.writeStringToFile("true", absolutePath, DataConfig.PUSH_FILE_NAME);
//        AndroidFileUtil.writeStringToFile(token, absolutePath, DataConfig.TOKEN_FILE_NAME);
//        RuntimeConfig.FIRST_STARTUP = true;
//    }
//
//}
