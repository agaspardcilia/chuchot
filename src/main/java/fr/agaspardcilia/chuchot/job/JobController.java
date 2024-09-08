package fr.agaspardcilia.chuchot.job;

import fr.agaspardcilia.chuchot.job.exception.BadJobStateException;
import fr.agaspardcilia.chuchot.job.exception.JobDuplicationException;
import fr.agaspardcilia.chuchot.job.exception.JobNotFoundException;
import fr.agaspardcilia.chuchot.store.ItemDescription;
import fr.agaspardcilia.chuchot.store.StoreService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/job")
@AllArgsConstructor
public class JobController {
    private final JobService jobService;
    private final StoreService storeService;

    @PostMapping
    public ResponseEntity<JobReport> create(@RequestBody JobDescription description) {
        try {
        ItemDescription item = storeService.getInputItem(description.sourceItemName());
        if (item == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

            return ResponseEntity.ok(jobService.update(null, description.name(), item, description.parameters()));
        } catch (BadJobStateException e) {
            // Cannot happen.
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (JobDuplicationException e) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }

    @PostMapping("/{id}")
    public ResponseEntity<JobReport> update(@PathVariable UUID id, @RequestBody JobDescription description) {
        try {
            ItemDescription item = storeService.getInputItem(description.sourceItemName());
            if (item == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return ResponseEntity.ofNullable(jobService.update(id, description.name(), item, description.parameters()));
        } catch (BadJobStateException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (JobDuplicationException e) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }

    @GetMapping("/all")
    public List<JobReport> getAll() {
        return jobService.getAll();
    }

    @PostMapping("/start/{id}")
    public ResponseEntity<Void> start(@PathVariable UUID id) {
        try {
            jobService.startJob(id);
            return ResponseEntity.ok(null);
        } catch (JobNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (BadJobStateException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/cancel/{id}")
    public ResponseEntity<Void> cancel(@PathVariable UUID id) {
        try {
            jobService.cancelJob(id);
            return ResponseEntity.ok(null);
        } catch (JobNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (BadJobStateException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        try {
            jobService.delete(id);
            return ResponseEntity.ok(null);
        } catch (JobNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (BadJobStateException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (IOException e) {
            log.error("Failed to delete job", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}/logs-from/{fromLine}")
    public ResponseEntity<JobLogs> getLogs(@PathVariable UUID id, @PathVariable int fromLine) {
        return ResponseEntity.ofNullable(jobService.getLogs(id, fromLine));
    }
}
