package com.oBrway.shortLink.core.service.Base62;

import com.oBrway.shortLink.common.enums.ResponseCode;
import com.oBrway.shortLink.common.exception.Base62Exception;

import java.math.BigInteger;

public class Base62Encoder {
    private static final String BASE62_CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final int BASE = 62;

    public static String encode(BigInteger value) throws Exception{
        /**
         * 不存在线程安全问题，且需要高性能，选用StringBuilder
         */
        StringBuilder encoded = new StringBuilder();
        BigInteger base = BigInteger.valueOf(BASE);

        try{
            if (value.compareTo(BigInteger.ZERO) == 0) {
                return "0";
            }
            while (value.compareTo(BigInteger.ZERO) > 0) {
                BigInteger[] divRem = value.divideAndRemainder(base);
                value = divRem[0];
                int remainder = divRem[1].intValue();
                encoded.insert(0, BASE62_CHARS.charAt(remainder));
            }
        } catch (Exception e){
            throw new Base62Exception(ResponseCode.Base62_ENCODE_ERROR);
        }

        return encoded.toString();
    }

    public static String encode(long value) throws Exception{
        return encode(BigInteger.valueOf(value));
    }

    public static BigInteger decode(String encoded) throws Exception{
        BigInteger value = BigInteger.ZERO;
        BigInteger base = BigInteger.valueOf(BASE);

        try{
            for (int i = 0; i < encoded.length(); i++) {
                char c = encoded.charAt(i);
                int index = BASE62_CHARS.indexOf(c);
                if (index == -1) {
                    throw new IllegalArgumentException("Invalid character in Base62 string: " + c);
                }
                value = value.multiply(base).add(BigInteger.valueOf(index));
            }
        } catch (Exception e){
            throw new Base62Exception(ResponseCode.Base62_DECODE_ERROR);
        }

        return value;
    }

    public static long decodeToLong(String encoded) throws Exception{
        BigInteger bigIntegerValue = decode(encoded);
        return bigIntegerValue.longValue();
    }
}
