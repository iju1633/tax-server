package com.tax.o3server.constant;

import java.util.HashMap;
import java.util.Map;

public class RegisterConst {

    public static final Map<String, String> ALLOWED_USERS = new HashMap<>();

    static {
        ALLOWED_USERS.put("홍길동", "860824-1655068");
        ALLOWED_USERS.put("김둘리", "921108-1582816");
        ALLOWED_USERS.put("마징가", "880601-2455116");
        ALLOWED_USERS.put("베지터", "910411-1656116");
        ALLOWED_USERS.put("손오공", "820326-2715702");
    }
}
