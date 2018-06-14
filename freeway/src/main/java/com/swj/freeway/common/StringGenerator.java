package com.swj.freeway.common;

import com.swj.basic.helper.StringHelper;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

/**
 * @author 余焕【yuh@3vjia.com】
 * @since 2018/5/28 16:53
 **/
@Slf4j
public class StringGenerator {

    public static final String SOURCES = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890";
    private static      Random random  = new Random();

    public static String generateString(int length) {
        char[] text = new char[length];
        for (int i = 0; i < length; i++) {
            text[i] = SOURCES.charAt(random.nextInt(SOURCES.length()));
        }
        return new String(text);
    }

    public static String getReqId() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        LocalDateTime now = LocalDateTime.now();
        return now.format(formatter) + generateString(18);
    }

    public static String getReqSeq(String reqSeq) {
        if (StringHelper.isNullOrWhiteSpace(reqSeq)) return "1";

        try {
            Integer seq = Integer.parseInt(reqSeq);
            return String.valueOf(seq + 1);
        } catch (Exception e) {
            log.info("parse request seq error {}", reqSeq);
        }
        return reqSeq;
    }

}
