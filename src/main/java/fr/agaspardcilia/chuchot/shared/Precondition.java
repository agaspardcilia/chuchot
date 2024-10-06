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
}
