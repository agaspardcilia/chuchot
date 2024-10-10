package fr.agaspardcilia.chuchot.shared;

import java.time.Duration;

public class FormatUtil {
    private FormatUtil() {
        // Do not instantiate! >:(
    }

    public static String formatTimeCode(Duration duration) {
        return FormatUtil.formatTimeCode(duration != null ? duration.getSeconds() : 0);
    }

    public static String formatTimeCode(long timeInSeconds) {
        return "%02d:%02d:%02d".formatted(timeInSeconds/3600, (timeInSeconds%3600)/60, timeInSeconds%60);
    }
}
