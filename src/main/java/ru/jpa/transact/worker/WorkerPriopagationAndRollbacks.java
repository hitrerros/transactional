package ru.jpa.transact.worker;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.jpa.transact.entity.FirstTab;
import ru.jpa.transact.entity.SecondTab;
import ru.jpa.transact.repository.FirstRepository;
import ru.jpa.transact.repository.SecondRepository;

import static org.springframework.transaction.annotation.Propagation.NEVER;
import static org.springframework.transaction.annotation.Propagation.NOT_SUPPORTED;
import static org.springframework.transaction.annotation.Propagation.REQUIRED;
import static org.springframework.transaction.annotation.Propagation.SUPPORTS;


@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class WorkerValueAndRollbacks {

    private final FirstRepository firstRepository;
    private final SecondRepository secondRepository;
    private final ApplicationContext context;

    /////////
    @Transactional(noRollbackFor = ArithmeticException.class)
    public void saveDontRollbackOn() {
        FirstTab firstTab = new FirstTab();
        firstTab.setName("dontrollbackon");
        firstRepository.save(firstTab);
        throw new ArithmeticException();
    }

    /////////
    @Transactional(propagation = NEVER)  // without transaction
    public void saveWithoutTransaction() {
        FirstTab firstTab = new FirstTab();
        firstTab.setName("nevertransaction");
        firstRepository.save(firstTab);
        throw new ArithmeticException();
    }

    /////////
    @Transactional(propagation = REQUIRED) //default
    public void saveOrdinary() {
        FirstTab firstTab = new FirstTab();
        firstTab.setName("ordinary");
        firstRepository.save(firstTab);

        throw new ArithmeticException();
    }

    /////////
    @Transactional(propagation = NEVER)
    public void neverThenSupports() {
        FirstTab firstTab = new FirstTab();
        firstTab.setName("neverfordescendant");
        firstRepository.save(firstTab);
        descendantSupports();
    }

    @Transactional(propagation = SUPPORTS)
    //  an active transaction exists. If a transaction exists, then the existing transaction will be used.
    // If there isn't a transaction, it is executed non-transactional:
    public void descendantSupports() {
        SecondTab firstTab = new SecondTab();
        firstTab.setName("secondtab");
        secondRepository.save(firstTab);
        throw new ArithmeticException();
    }

    /////////
    @Transactional
    public void requiredThenNotSupported() {
        FirstTab firstTab = new FirstTab();
        firstTab.setName("ancestorfornonsupported");
        firstRepository.save(firstTab);

      // get another bean to get new transaction
        getAnotherBean().descendantNeverSupports();
    }

    @Transactional(propagation = NOT_SUPPORTED)
    public void descendantNeverSupports() {
        SecondTab secondTab = new SecondTab();
        secondTab.setName("descendantNeverSupports");
        secondRepository.save(secondTab);
        throw new ArithmeticException();
    }

    private WorkerValueAndRollbacks getAnotherBean()
    {
        return context.getBean(WorkerValueAndRollbacks.class);

    }

}

