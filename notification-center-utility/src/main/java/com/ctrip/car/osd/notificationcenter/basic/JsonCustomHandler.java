package com.ctrip.car.osd.notificationcenter.basic;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.base.ParserBase;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.deser.BeanDeserializerBase;
import com.fasterxml.jackson.databind.deser.DeserializationProblemHandler;
import com.fasterxml.jackson.databind.deser.SettableBeanProperty;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiayx on 2023/6/16.
 * 同步: com.ctrip.car.osd.framework.common.utils.json.CustomDeserializationProblemHandler
 */
public class JsonCustomHandler extends DeserializationProblemHandler {

    private static final String UNDERLINE = "_";
    private static final String EMPTY = "";
    private static final List<String> BOOLEAN_VALUE = new ArrayList<>(4);

    static {
        BOOLEAN_VALUE.add("1");
        BOOLEAN_VALUE.add("0");
        BOOLEAN_VALUE.add("true");
        BOOLEAN_VALUE.add("false");
    }

    public JsonCustomHandler() {
        super();
    }

    @Override
    public boolean handleUnknownProperty(DeserializationContext ctxt, JsonParser p, JsonDeserializer<?> deserializer, Object beanOrClass, String propertyName) throws IOException {
        if (deserializer instanceof BeanDeserializerBase) {
            BeanDeserializerBase beanDeserializerBase = (BeanDeserializerBase) deserializer;
            // 参考BeanDeserializer.vanillaDeserialize()
            SettableBeanProperty prop = beanDeserializerBase.findProperty(compatPropertyName(propertyName));
            if (prop != null) {
                prop.deserializeAndSet(p, ctxt, beanOrClass);
                return true;
            }
        }
        return super.handleUnknownProperty(ctxt, p, deserializer, beanOrClass, propertyName);
    }

    private String compatPropertyName(String propertyName) {
        return propertyName.replaceAll(UNDERLINE, EMPTY);
    }

    @Override
    public Object handleWeirdStringValue(DeserializationContext ctxt, Class<?> targetType, String valueToConvert, String failureMsg) throws IOException {
        if (targetType.getName().equals("java.lang.Boolean") && BOOLEAN_VALUE.contains(valueToConvert)) {
            return "1".equals(valueToConvert) || "true".equals(valueToConvert);
        }
        return super.handleWeirdStringValue(ctxt, targetType, valueToConvert, failureMsg);
    }

    @Override
    public Object handleUnexpectedToken(DeserializationContext ctxt, JavaType targetType, JsonToken t, JsonParser p, String failureMsg) throws IOException {
        if (targetType.getRawClass().getName().equals("java.lang.String")) {
            if (p instanceof ParserBase) {
                ParserBase parserBase = (ParserBase) p;
                String result = p.getCurrentValue() == null ? EMPTY : p.getCurrentValue().toString();
                parserBase.skipChildren();
                return result;
            }
        }
        return super.handleUnexpectedToken(ctxt, targetType, t, p, failureMsg);
    }

}