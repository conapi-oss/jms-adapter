package at.conapi.oss.jms.adapter;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * Utility class for setting object properties via reflection using JavaBean conventions.
 * <p>
 * This class provides type-safe property setting by automatically converting String values
 * to the appropriate Java primitive or wrapper types. It is used internally by
 * {@link JmsFactory#createConnectionFactory(String, Map)} to configure JMS provider objects.
 * </p>
 *
 * @since 1.0.0
 */
public class GenericPropertySetter {

    /**
     * Private constructor to prevent instantiation of utility class.
     *
     * @since 1.0.0
     */
    private GenericPropertySetter() {
        throw new UnsupportedOperationException("Utility class - do not instantiate");
    }

    /**
     * Sets multiple properties on a target object using JavaBean-style setters.
     * <p>
     * For each property in the map, this method:
     * </p>
     * <ol>
     *   <li>Derives the setter method name (e.g., "brokerURL" → "setBrokerURL")</li>
     *   <li>Finds the appropriate setter method via reflection</li>
     *   <li>Converts the String value to the required parameter type</li>
     *   <li>Invokes the setter with the converted value</li>
     * </ol>
     * <p>
     * Supported type conversions:
     * String, int/Integer, long/Long, double/Double, float/Float,
     * boolean/Boolean, byte/Byte, char/Character, short/Short
     * </p>
     *
     * @param target the object to configure
     * @param props map of property names to String values
     * @throws Exception if a setter cannot be found, type conversion fails, or invocation fails
     * @since 1.0.0
     */
    public static void setProperties(Object target, Map<String, String> props) throws Exception {
        Class<?> targetClass = target.getClass();

        for (Map.Entry<String, String> entry : props.entrySet()) {
            String propertyName = entry.getKey();
            String stringValue = entry.getValue();

            String methodName = "set" + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1);

            Method method = findSuitableMethod(targetClass, methodName);

            if (method != null) {
                Class<?> paramType = method.getParameterTypes()[0];
                Object convertedValue = convertStringToType(stringValue, paramType);
                method.invoke(target, convertedValue);
            } else {
                throw new IllegalArgumentException("No suitable method found for property: " + propertyName);
            }
        }
    }

    private static Method findSuitableMethod(Class<?> clazz, String methodName) {
        Method[] methods = clazz.getMethods();
        for (Method method : methods) {
            if (method.getName().equals(methodName) && method.getParameterCount() == 1) {
                return method;
            }
        }
        return null;
    }

    private static Object convertStringToType(String value, Class<?> targetType) {
        if (targetType == String.class) {
            return value;
        } else if (targetType == int.class || targetType == Integer.class) {
            return Integer.parseInt(value);
        } else if (targetType == long.class || targetType == Long.class) {
            return Long.parseLong(value);
        } else if (targetType == double.class || targetType == Double.class) {
            return Double.parseDouble(value);
        } else if (targetType == float.class || targetType == Float.class) {
            return Float.parseFloat(value);
        } else if (targetType == boolean.class || targetType == Boolean.class) {
            return Boolean.parseBoolean(value);
        } else if (targetType == byte.class || targetType == Byte.class) {
            return Byte.parseByte(value);
        } else if (targetType == char.class || targetType == Character.class) {
            return value.charAt(0);
        } else if (targetType == short.class || targetType == Short.class) {
            return Short.parseShort(value);
        }

        throw new IllegalArgumentException("Unsupported type conversion: String to " + targetType);
    }
}