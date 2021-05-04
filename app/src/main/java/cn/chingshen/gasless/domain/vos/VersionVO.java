package cn.chingshen.gasless.domain.vos;

import lombok.Data;

@Data
public class VersionVO {
    /**
     * version : V1.2.0
     * releaseNote : 1. 更新了UI；
     * 2. 修复了几个bug；
     * 3. 优化了软件的加载速度；
     * download : https://chingshen-public.oss-cn-hangzhou.aliyuncs.com/dou-zhuan/andorid-release/app-release.apk
     */

    private Integer code;
    private String msg;
    private Boolean success;
    private DataBean data;

    @Data
    public static class DataBean {

        /**
         * version : V1.2.0
         * releaseNote : 1. 更新了UI；
         * 2. 修复了几个bug；
         * 3. 优化了软件的加载速度；
         * download : https://chingshen-public.oss-cn-hangzhou.aliyuncs.com/dou-zhuan/andorid-release/app-release.apk
         */
        private String version;
        private String releaseNote;
        private String download;
    }
}
