package com.example.todo.constants;

import javax.servlet.http.PushBuilder;

public class Constants {
    public static final String PASSWORD_REGEX = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[\\W,_])[.!-~]{6,}$";
    public static final String NAME_REGEX = "^[a-zA-ZÀÁÂÃÈÉÊÌÍÒÓÔÕÙÚĂĐĨŨƠàáâãèéêìíòóôõùúăđĩũơƯĂẠẢẤẦẨẪẬẮẰẲẴẶẸẺẼỀỀỂẾưăạảấầẩẫậắằẳẵặẹẻẽềềểếỄỆỈỊỌỎỐỒỔỖỘỚỜỞỠỢỤỦỨỪễệỉịọỏốồổỗộớờởỡợụủứừỬỮỰỲỴÝỶỸửữựỳỵỷỹ\\s]*$";
    public static final String EMAIL_REGEX = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
    public static final String INVALID_CLIENT_SECRET = "INVALID_CLIENT_SECRET";
    public static final String INVALID_USER = "INVALID_USER";
    public static final String INVALID_CLIENT_CREDENTIAL = "INVALID_CLIENT_CREDENTIAL";
    public static final String INVALID_REFRESH_TOKEN = "INVALID_REFRESH_TOKEN";
    public static final String REFRESH_TOKEN_EXPIRED = "REFRESH_TOKEN_EXPIRED";
    public static final String MAIL_ALREADY_EXISTS = "MAIL_ALREADY_EXISTS";
    public static final String OBJECT_NOT_FOUND = "OBJECT_NOT_FOUND";
    public static final String LOGIN_VALIDATE = "LOGIN_VALIDATE";
    public static final String LOGIN_TEMPORARILY_LOCKED = "LOGIN_TEMPORARILY_LOCKED";
    public static final String EMAIL_NOT_MATCHED_POLICY = "EMAIL_NOT_MATCHED_POLICY";
    public static final String PASSWORD_NOT_MATCHED_POLICY = "PASSWORD_NOT_MATCHED_POLICY";
    public static final String NAME_NOT_MATCHED_POLICY = "NAME_NOT_MATCHED_POLICY";
    public static final String INVALID_TASK = "INVALID_TASK";
    public static final String USER_NOT_FOUND = "USER_NOT_FOUND";
    public static final String WRONG_PASSWORD = "WRONG_PASSWORD";
    public static final String PASSWORD_HAS_NOT_BEEN_CHANGED = "PASSWORD_HAS_NOT_BEEN_CHANGED";
    public static final Integer DEFAULT_FETCH_COUNT = 5;
    public static final Integer MAX_FETCH_COUNT = 10;
    public static final Integer DEFAULT_OFFSET = 0;
    public static final String NOT_PERMISSION = "NOT_PERMISSION";
    public static final String ALREADY_ASSIGNED = "ALREADY_ASSIGNED";
    public static final String INVALID_ORGANIZATION = "INVALID_ORGANIZATION";
    public static final String USER_NOT_IN_ORGANIZATION = "USER_NOT_IN_ORGANIZATION";
    public static final String START_DATE_MUST_BE_BEFORE_OR_EQUAL_END_DATE = "START_DATE_MUST_BE_BEFORE_OR_EQUAL_END_DATE";
    public static final String START_DATE_MUST_BE_AFTER_OR_EQUAL_CURRENT_DATE = "START_DATE_MUST_BE_BEFORE_OR_EQUAL_CURRENT_DATE";
    public static final String END_DATE_MUST_BE_AFTER_OR_EQUAL_CURRENT_DATE = "END_DATE_MUST_BE_AFTER_OR_EQUAL_CURRENT_DATE";
}
