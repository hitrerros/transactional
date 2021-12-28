package ru.jpa.transact;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import ru.jpa.transact.worker.WorkerNest;
import ru.jpa.transact.worker.WorkerPriopagationAndRollbacks;

/*
1. MANDATORY — использует существующую транзакцию. Если ее нет — бросает exception. Если используется для класса,
 то действует на все public методы.
2. NESTED — вложенная транзакция (подтранзакция). Подтвержается вместе с внешней транзакцией. Если нет существующей
 транзакции — работает как REQUIRED.   Можно применять в таких случаях, когда: Сработает — норм, не сработает — тоже норм.
 Самое главное чтобы внешняя транзакция не пострадала.
3. NEVER — означает, что данный метод не должен выполняться в транзакции. Если транзакция запущена — бросает exception.
4. NOT_SUPPORTED — означает не выполнять в текущей транзакции. Если транзакция запущена — она останавливается на время
   выполнения метода. Метод выполняется вне транзакции. Когда метод выполнился — транзакция запускается.
5. REQUIRED — (по умолчанию) означает, что если запущена транзакция — выполнять внутри нее, иначе создает
 новую транзакцию. Если ошибка в запросе, то в базу ничего на запишется.
6. REQUIRES_NEW — создает в любом случае новую транзакцию. Если запущена существующая транзакция — она
останавливается на время выполнения метода, новый метод выполняется в новой транзакции, и
дальше выполняется внешняя транзакция, если она есть.
7. SUPPORTS — может выполняться внутри транзакции, если она запущена, иначе выполнять без транзакции
 (новую транзакцию не создает), т.е. методу не важно, будет транзакция или нет, он в любом случае выполнится,
  но если будет транзакция, то он выполнится внутри нее.
*/

@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TransactApplication.class)
@Slf4j
class TransactApplicationTests {

    @Qualifier("root")
    @Autowired
    private WorkerPriopagationAndRollbacks workerValueAndRollbacks;

    @Qualifier("nest")
    @Autowired
    private WorkerNest workerNest;


    @Test
    void dontRollbackOn() {
        log.info("don't rollback on");
        try {
            workerValueAndRollbacks.noRollbackFor();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    void rollbackOn() {
        log.info("rollback for");
        try {
            workerValueAndRollbacks.rollbackFor();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    void rollbackOnChecked() {
        log.info("checked commit");
        try {
            workerValueAndRollbacks.checkedException();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    void neverTransaction() {
        log.info("propagation = never");
        try {
            workerValueAndRollbacks.saveWithoutTransaction();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    void defaultTransaction() {
        log.info("propagation = required (default)");
        try {
            workerValueAndRollbacks.saveOrdinary();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    void neverSupportsTransaction() {
        log.info("propagation = never + supports");
        try {
            workerValueAndRollbacks.neverThenSupports();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    void requiredThenNotSupported() {
        log.info("propagation = required + not supported");
        try {
            workerValueAndRollbacks.requiredThenNotSupported();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    void readOnly() {
        log.info("readyonly = true");
        try {
            workerValueAndRollbacks.readOnly();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    void mandatory() {
        log.info("propagation = mandatory");
        try {
            workerValueAndRollbacks.mandatory();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    void requiredThenNever() {
        log.info("required then never");
        try {
            workerValueAndRollbacks.requiredThenNever();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    void requiredThenNeverItself() {
        log.info("required then never itself");
        try {
            workerValueAndRollbacks.requiredThenNeverItself();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
        // If the outer transaction is rolled back,the nested tra is rolled back as well.
    void requiredThenNested() {
        log.info("required then nested");
        try {
            workerValueAndRollbacks.requiredForNested();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
        // If the outer transaction is rolled back, the changes of the inner tra will not be rolled back in case of rolback of the outer tra.
    void requiredThenRequiredNew() {
        log.info("required then required new");
        try {
            workerValueAndRollbacks.requiredThenRequiredNew();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    void pureRequiredNew() {
        try {
            workerNest.required1();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

}
