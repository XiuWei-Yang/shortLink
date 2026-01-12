package com.oBrway.shortLink.core.Base62Test;

import com.oBrway.shortLink.core.Base62.Base62Encoder;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Base62Test {
    Base62Encoder base62Encoder;

    @Test
    public void testEncode() throws Exception {
        BigInteger value = new BigInteger("123456789");
        String encoded = Base62Encoder.encode(value);
        assertEquals("8M0kX", encoded);
    }

    @Test
    public void testEncodeZero() throws Exception {
        BigInteger value = BigInteger.ZERO;
        String encoded = Base62Encoder.encode(value);
        assertEquals("0", encoded);
    }

    @Test
    public void testEncodeLargeNumber() throws Exception {
        BigInteger value = new BigInteger("9876543210123456789");
        String encoded = Base62Encoder.encode(value);
        assertEquals("BlafhneO193", encoded);
    }

    @Test
    public void testEncodeNegativeNumber() {
        BigInteger value = new BigInteger("-12345");
        try {
            Base62Encoder.encode(value);
        } catch (Exception e) {
            assertEquals("Base62_ENCODE_ERROR", e.getMessage());
        }
    }

    @Test
    public void testDecode() throws Exception {
        String encoded = "8M0kX";
        BigInteger decoded = Base62Encoder.decode(encoded);
        assertEquals(new BigInteger("123456789"), decoded);
    }

    @Test
    public void testDecodeZero() throws Exception {
        String encoded = "0";
        BigInteger decoded = Base62Encoder.decode(encoded);
        assertEquals(BigInteger.ZERO, decoded);
    }

    @Test
    public void testDecodeLargeString() throws Exception {
        String encoded = "BlafhneO193";
        BigInteger decoded = Base62Encoder.decode(encoded);
        assertEquals(new BigInteger("9876543210123456789"), decoded);
    }
}