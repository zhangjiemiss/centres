package org.origin.centres.mapper;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.apache.commons.lang3.StringEscapeUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

/**
 * @author zhangjie
 * @version 2020-06-29
 * @apiNote 简单封装Jackson，实现JSON String<->Java Object的Mapper.
 * 封装不同的输出风格, 使用不同的builder函数创建实例.
 */
@SuppressWarnings("unused")
public class JsonMapper extends ObjectMapper {

    private static JsonMapper mapper;

    private JsonMapper() {
        this(Include.NON_EMPTY);
    }

    private JsonMapper(Include include) {
        // 设置输出时包含属性的风格
        if (include != null) {
            this.setSerializationInclusion(include);
        }
        // 允许单引号、允许不带引号的字段名称
        this.enableSimple();
        // 设置输入时忽略在JSON字符串中存在但Java对象实际没有的属性
        this.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        // 空值处理为空串
        this.getSerializerProvider().setNullValueSerializer(new JsonSerializer<Object>() {
            @Override
            public void serialize(Object value, JsonGenerator jg, SerializerProvider provider) throws IOException {
                jg.writeString("");
            }
        });
        // 进行HTML解码。
        this.registerModule(new SimpleModule().addSerializer(String.class, new JsonSerializer<String>() {
            @Override
            public void serialize(String value, JsonGenerator jg, SerializerProvider provider) throws IOException {
                jg.writeString(StringEscapeUtils.unescapeHtml4(value));
            }
        }));
        // 设置时区 getTimeZone("GMT+8:00")
        this.setTimeZone(TimeZone.getDefault());
    }

    /**
     * 创建只输出非Null且非Empty(如List.isEmpty)的属性到Json字符串的Mapper,建议在外部接口中使用.
     */
    private static JsonMapper getInstance() {
        if (mapper == null) {
            mapper = new JsonMapper().enableSimple();
        }
        return mapper;
    }

    /**
     * 允许单引号、允许不带引号的字段名称
     */
    private JsonMapper enableSimple() {
        this.configure(Feature.ALLOW_SINGLE_QUOTES, true);
        this.configure(Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        return this;
    }

    /**
     * 创建只输出初始值被改变的属性到Json字符串的Mapper, 最节约的存储方式，建议在内部接口中使用。
     */
    public static JsonMapper nonDefaultMapper() {
        if (mapper == null) {
            mapper = new JsonMapper(Include.NON_DEFAULT);
        }
        return mapper;
    }

    /**
     * Object转化为json
     */
    private String toJson(Object object) {
        try {
            return super.writeValueAsString(object);
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * 为JSON字符串转换对象
     */
    public <T> T readValue(String content, Class<T> valueType) {
        try {
            return super.readValue(content, valueType);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 对象转换为JSON字符串
     */
    public static String toJsonString(Object object) {
        return JsonMapper.getInstance().toJson(object);
    }

    /**
     * 为JSON字符串转换对象
     */
    public static <T> T parseObject(String json, Class<T> clazz) {
        return JsonMapper.getInstance().readValue(json, clazz);
    }

    /**
     * 为JSON字符串转换对象集合
     */
    public static <T> List<T> parseArray(String json, Class<T> clazz) {
        JsonMapper jsonMapper = JsonMapper.getInstance();
        JavaType javaType = jsonMapper.getTypeFactory().constructParametricType(ArrayList.class, clazz);
        try {
            return jsonMapper.readValue(json, javaType);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }
}
