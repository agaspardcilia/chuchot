package fr.agaspardcilia.chuchot.job.exception;

public class BadJobStateException extends Exception {
    public BadJobStateException() {
    }

    public BadJobStateException(String message) {
        super(message);
    }
}
