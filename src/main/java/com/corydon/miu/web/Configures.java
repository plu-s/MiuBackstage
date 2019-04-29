package com.corydon.miu.web;

import org.apache.log4j.Logger;

public class Configures {
    public static final String HOST="http://120.79.50.55:8080";
    //public static final String HOST="http://127.0.0.1:8080";
    public static final String PROJECT_NAME="MiuBackstage";
    public static final String MODULE_USER="/user";
    public static final String MODULE_DISCUSS="/discuss";
    public static Logger logger=Logger.getLogger(Configures.class);

    /**
     * @see #RESULT_ACTIVE_USER_FAILED 表示用户激活的token错误或者已经激活
     */
    public static final int RESULT_OK=200;
    public static final int RESULT_USER_UN_ACTIVE=300;
    public static final int RESULT_USER_NON_EXISTENT=301;
    public static final int RESULT_USER_EXISTENT=302;
    public static final int RESULT_ACCESS_DENIED=303;
    public static final int RESULT_ACTIVE_USER_FAILED=304;
    public static final int RESULT_PASSWORDS_ERROR=305;
    public static final int RESULT_REPEAT_DO_LIKE=306;
    public static final int RESULT_DISCUSS_NON_EXISTENT=307;
    public static final int RESULT_SYSTEM_ERROR=400;
}
