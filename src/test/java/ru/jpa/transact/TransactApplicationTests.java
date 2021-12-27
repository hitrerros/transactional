package ru.jpa.transact;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import ru.jpa.transact.worker.WorkerPriopagationAndRollbacks;

@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TransactApplication.class)
class TransactApplicationTests {

    @Autowired
    private WorkerPriopagationAndRollbacks workerValueAndRollbacks;

    @Test
    void dontRollbackOn() {
        try {
            workerValueAndRollbacks.saveDontRollbackOn();
        } catch (Exception e) {
        }
    }

    @Test
    void neverTransaction() {
        try {
            workerValueAndRollbacks.saveWithoutTransaction();
        } catch (Exception e) {
        }
    }

    @Test
    void defaultTransaction() {
        try {
            workerValueAndRollbacks.saveOrdinary();
        } catch (Exception e) {
        }
    }

    @Test
    void neverSupportsTransaction() {
        try {
            workerValueAndRollbacks.neverThenSupports();
        } catch (Exception e) {
        }
    }

    @Test
    void requiredThenNotSupported() {
        try {
            workerValueAndRollbacks.requiredThenNotSupported();
        } catch (Exception e) {
        }
    }

    @Test
    void readOnly() {
        try {
            workerValueAndRollbacks.readOnly();
        } catch (Exception e) {
        }
    }

}
