package fr.agaspardcilia.chuchot.shared.whisper;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Device {
    CPU("cpu"),
    CUDA("cuda");
    private final String value;

}
