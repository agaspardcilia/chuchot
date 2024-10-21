package fr.agaspardcilia.chuchot.shared.whisper;

import fr.agaspardcilia.chuchot.shared.whisper.progress.Progress;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProgressTest {

    @Test
    void testProgress() {
        Progress progress = new Progress(200);

        assertEquals(0, progress.getProgressInPercent());
        progress.setCurrent(50);
        assertEquals(25, progress.getProgressInPercent());
        progress.setCurrent(150);
        assertEquals(75, progress.getProgressInPercent());
        progress.setCurrent(200);
        assertEquals(100, progress.getProgressInPercent());
    }

    @Test
    void testIllegalStates() {
        Progress progress = new Progress(200);

        try {
            progress.setCurrent(201);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Current cannot be greater than total", e.getMessage());
        }

        try {
            progress.setCurrent(-1);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Current cannot be less than 0", e.getMessage());
        }

        try {
            new Progress(-1);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Total must be greater than 0", e.getMessage());
        }


    }

    @Test
    void testComplete() {
        Progress progress = new Progress(200);

        assertEquals(0, progress.getProgressInPercent());
        progress.setComplete();
        assertEquals(100, progress.getProgressInPercent());
    }
}
