package ru.jpa.transact;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TransactApplication.class)
class TransactApplicationTests {

    @Autowired
    private WorkerValueAndRollbacks workerValueAndRollbacks;

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

}
