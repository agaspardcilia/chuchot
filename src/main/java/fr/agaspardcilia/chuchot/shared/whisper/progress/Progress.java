package fr.agaspardcilia.chuchot.shared.whisper.progress;



import fr.agaspardcilia.chuchot.shared.Precondition;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Progress {
    private final Lock lock;
    private int current;
    private final int total;

    public Progress(int total) {
        Precondition.assertTrue(total > 0, "Total must be greater than 0");
        this.lock = new ReentrantLock();
        this.total = total;
        this.current = 0;
    }

    public void setCurrent(int current) {
        lock.lock();
        try {
            Precondition.assertTrue(current <= total, "Current cannot be greater than total");
            Precondition.assertTrue(current >= 0, "Current cannot be less than 0");
            this.current = current;
        } finally {
            lock.unlock();
        }
    }

    public int getProgressInPercent() {
        lock.lock();
        try {
            return current * 100 / total;
        } finally {
            lock.unlock();
        }
    }

    public void setComplete() {
        lock.lock();
        try {
            this.current = total;
        } finally {
            lock.unlock();
        }
    }
}
