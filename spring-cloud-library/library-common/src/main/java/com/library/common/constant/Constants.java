package com.library.common.constant;

public class Constants {

    public static final String TOKEN_HEADER = "Authorization";
    public static final String TOKEN_PREFIX = "Bearer ";

    public static final String ROLE_STUDENT = "STUDENT";
    public static final String ROLE_ADMIN = "ADMIN";

    public static final String HEADER_USER_ID = "X-User-Id";
    public static final String HEADER_USER_NAME = "X-User-Name";
    public static final String HEADER_USER_ROLE = "X-User-Role";

    public static final String BORROW_STATUS_BORROWED = "BORROWED";
    public static final String BORROW_STATUS_RETURNED = "RETURNED";
    public static final String BORROW_STATUS_OVERDUE = "OVERDUE";

    public static final String NOTICE_TYPE_ANNOUNCEMENT = "ANNOUNCEMENT";
    public static final String NOTICE_TYPE_OVERDUE = "OVERDUE_REMINDER";
    public static final String NOTICE_TYPE_SYSTEM = "SYSTEM";
}
