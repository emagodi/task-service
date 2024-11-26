package za.co.datacentrix.taskservice.utils;

import java.lang.reflect.Field;

public class ObjectUtils {

    public static void copyNonNullProperties(Object source, Object target) {
        if (source == null || target == null) {
            throw new IllegalArgumentException("Source and target must not be null");
        }

        Field[] fields = source.getClass().getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true); // Allow access to private fields
                Object value = field.get(source);
                if (value != null) {
                    // Check if the target object has the same field
                    Field targetField = target.getClass().getDeclaredField(field.getName());
                    targetField.setAccessible(true); // Allow access to private fields
                    targetField.set(target, value); // Copy non-null value
                }
            } catch (NoSuchFieldException e) {
                // Field does not exist in the target class, ignore
            } catch (IllegalAccessException e) {
                // Handle potential exceptions (e.g., log them)
                e.printStackTrace();
            }
        }
    }
}