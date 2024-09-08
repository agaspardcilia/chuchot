package fr.agaspardcilia.chuchot.job;

import fr.agaspardcilia.chuchot.shared.whisper.WhisperParameters;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record JobDescription(
        @Size(max = 64)
        @NotBlank
        String name,
        @NotBlank
        String sourceItemName,
        WhisperParameters parameters
) {
}
