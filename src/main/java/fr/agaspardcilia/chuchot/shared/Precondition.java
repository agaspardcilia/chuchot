package fr.agaspardcilia.chuchot.shared;

public class Precondition {
    public static void notNull(Object object, String message) {
        if (object == null) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void notNull(Object object) {
        notNull(object, "Object must not be null");
    }

    public static void assertTrue(boolean condition) {
        assertTrue(condition, "Condition must not be false");
    }

    public static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new IllegalArgumentException(message);
        }
    }
}
