package fr.agaspardcilia.chuchot.job;

import java.util.List;

public record JobLogs(
        List<String> logs,
        List<String> errorLogs
) {
}
