package com.inabate.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.SocketAddress;

public class ReflectionUtils {

    private static final Field FIELD_MODIFIERS;

    public static void setFinalField(Class objectClass, Object object, String fieldName, Object value) throws Exception {
        Field field = objectClass.getDeclaredField(fieldName);
        field.setAccessible(true);

        if (Modifier.isFinal(field.getModifiers())) {
            FIELD_MODIFIERS.setInt(field, field.getModifiers() & 0xFFFFFFEF);
        }

        field.set(object, value);
    }

    public static Object getPrivateField(Class objectClass, Object object, String fieldName) throws Exception {
        Field field = objectClass.getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(object);
    }

    public static <T> T getPrivateField(Class<?> objectClass, Object object, Class<T> fieldClass, String fieldName) throws Exception {
        Field field = objectClass.getDeclaredField(fieldName);
        field.setAccessible(true);
        return (T)field.get(object);
    }

    public static String getProperField(Class objectClass) {
        for (Field f : objectClass.getFields()) {
            if (f.getType() == SocketAddress.class) {
                return f.getName();
            }
        }

        return "N/A";
    }

    static {
        Field field = null;

        try {
            field = Field.class.getDeclaredField("modifiers");
            field.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        FIELD_MODIFIERS = field;
    }
}
