package fr.agaspardcilia.chuchot.shared.whisper;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Task {
    TRANSCRIBE("transcribe"),
    TRANSLATE("translate");

    private final String value;
}
