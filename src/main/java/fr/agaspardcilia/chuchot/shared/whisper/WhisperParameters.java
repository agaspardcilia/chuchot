package fr.agaspardcilia.chuchot.shared.whisper;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public record WhisperParameters(
        @NotNull
        Language language,
        @NotNull
        Model model,
        @NotNull
        Task task
) {

    public List<String> toCommandParams() {
        return List.of(
                "--task", task.getValue(),
                "--language", language.getValue(),
                "--model", model.getValue()
        );
    }
}
