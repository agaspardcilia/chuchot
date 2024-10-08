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
                // TODO: enable that back once, it's sure that we have the right models and that the default is the best one.
//                "--model", model.getValue(),
                "--language", language.getValue()
        );
    }
}
