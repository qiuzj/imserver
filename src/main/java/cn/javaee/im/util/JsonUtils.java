package cn.javaee.im.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class JsonUtils {

    private static final ObjectMapper mapper = new ObjectMapper();

    public static <T> T parseObject(String value, Class<T> clazz) {
        try {
            return mapper.readValue(value, clazz);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <T> T parseObject(String value, TypeReference<T> valueTypeRef) {
        try {
            return mapper.readValue(value, valueTypeRef);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String toJSONString(Object value) {
        try {
            return mapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

}
