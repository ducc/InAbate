package red.cory.coriddos.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.SocketAddress;
import java.util.HashMap;
import java.util.Map;

public class ReflectionUtils {

    private static final Field FIELD_MODIFIERS;

    private static Map<Class<?>, Map<String, Field>> cachedFields = new HashMap<>();

    public static void setFinalField(Class objectClass, Object object, String fieldName, Object value) throws Exception {
        Field field = getDeclaredField(objectClass, fieldName);
        field.setAccessible(true);

        if (Modifier.isFinal(field.getModifiers())) {
            FIELD_MODIFIERS.setInt(field, field.getModifiers() & 0xFFFFFFEF);
        }

        field.set(object, value);
    }

    public static Object getPrivateField(Class objectClass, Object object, String fieldName) throws Exception {
        Field field = getDeclaredField(objectClass, fieldName);
        field.setAccessible(true);
        return field.get(object);
    }

    public static <T> T getPrivateField(Class<?> objectClass, Object object, Class<T> fieldClass, String fieldName) throws Exception {
        Field field = getDeclaredField(objectClass, fieldName);
        field.setAccessible(true);
        return (T) field.get(object);
    }

    public static String getProperField(Class objectClass) {
        for (Field f : objectClass.getFields()) {
            if (f.getType() == SocketAddress.class) {
                return f.getName();
            }
        }

        return "N/A";
    }

    private static Field getDeclaredField(Class<?> clazz, String fieldName) {
        if (!cachedFields.containsKey(clazz)) {
            cachedFields.put(clazz, new HashMap<String, Field>());
        }

        Map<String, Field> clazzCache = cachedFields.get(clazz);

        if (clazzCache.containsKey(fieldName)) {
            return clazzCache.get(fieldName);
        }

        try {
            Field field = clazz.getDeclaredField(fieldName);
            clazzCache.put(fieldName, field);
            return field;
        } catch (NoSuchFieldException e) {
            clazzCache.put(fieldName, null);
            throw new RuntimeException(e);
        }
    }

    static {
        Field field = null;

        try {
            field = getDeclaredField(Field.class, "modifiers");
            field.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        FIELD_MODIFIERS = field;
    }
}
