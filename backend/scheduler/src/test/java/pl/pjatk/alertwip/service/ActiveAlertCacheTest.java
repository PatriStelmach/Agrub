package pl.pjatk.alertwip.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.pjatk.alertwip.model.GlobalProblem;
import pl.pjatk.alertwip.repository.GlobalProblemRepository;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ActiveAlertCacheTest {

    @Mock
    private GlobalProblemRepository repository;

    @InjectMocks
    private ActiveAlertCache activeAlertCache;

    @Test
    void shouldAddProblemToCacheWhenStatusIsNotDone() {
        // Given
        GlobalProblem problem = new GlobalProblem();
        problem.setId(1L);
        problem.setUniqueKey("[TEST] Host - Awaria");
        problem.setStatus("Sent");

        // When
        activeAlertCache.updateAlert(problem);
        GlobalProblem cachedProblem = activeAlertCache.getByUniqueKey("[TEST] Host - Awaria");

        // Then
        assertNotNull(cachedProblem);
        assertEquals(1L, cachedProblem.getId());
    }

    @Test
    void shouldRemoveProblemFromCacheWhenStatusIsDone() {
        // Given
        GlobalProblem problem = new GlobalProblem();
        problem.setId(2L);
        problem.setStatus("Sent");

        // Dodajemy do cache
        activeAlertCache.updateAlert(problem);
        assertNotNull(activeAlertCache.getAlertById(2L));

        // When (zmieniamy status na Done i robimy update)
        problem.setStatus("Done");
        activeAlertCache.updateAlert(problem);

        // Then (powinno zostać wyrzucone z mapy)
        assertNull(activeAlertCache.getAlertById(2L));
    }

    @Test
    void shouldDirectlyRemoveAlertById() {
        // Given
        GlobalProblem problem = new GlobalProblem();
        problem.setId(3L);
        problem.setStatus("Sent");
        activeAlertCache.updateAlert(problem);

        // When
        activeAlertCache.removeAlert(3L);

        // Then
        assertNull(activeAlertCache.getAlertById(3L));
    }
}