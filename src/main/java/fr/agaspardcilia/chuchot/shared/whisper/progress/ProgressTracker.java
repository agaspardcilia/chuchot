package fr.agaspardcilia.chuchot.shared.whisper.progress;

import java.util.Optional;

public class ProgressTracker {
    private final Progress progress;

    public ProgressTracker(int mediaDurationInSeconds) {
        this.progress = new Progress(mediaDurationInSeconds);
    }

    public void onLogNewEntry(String logEntry) {
        extractProgressFromLogs(logEntry)
                .ifPresent(progress::setCurrent);
    }

    private Optional<Integer> extractProgressFromLogs(String logEntry) {
        return Optional.empty(); // TODO!
    }
}
