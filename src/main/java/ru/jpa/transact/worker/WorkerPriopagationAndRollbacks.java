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
public class WorkerPriopagationAndRollbacks {

    private final FirstRepository firstRepository;
    private final SecondRepository secondRepository;
    private final ApplicationContext context;

    //// "dontrollbackon" will be recorded despite throw being invoked
    @Transactional(noRollbackFor = ArithmeticException.class)
    public void saveDontRollbackOn() {
        saveToFirst("dontrollbackon");
        throw new ArithmeticException();
    }

    //////  "nevertransaction" will be recorded  because there is no transaction
    @Transactional(propagation = NEVER)  // without transaction
    public void saveWithoutTransaction() {
        saveToFirst("nevertransaction");
        throw new ArithmeticException();
    }

    /////////
    @Transactional(propagation = REQUIRED) // nothing will be recordec
    public void saveOrdinary() {
        saveToFirst("ordinary");
        throw new ArithmeticException();
    }

    /////////
    @Transactional(propagation = NEVER) // neverfordescendant will be record because no transaction
    public void neverThenSupports() {
        saveToFirst("neverfordescendant");
        getAnotherBean().descendantSupports();
    }

    @Transactional(propagation = SUPPORTS)
    //  an active transaction exists. If a transaction exists, then the existing transaction will be used.
    // If there isn't a transaction, it is executed non-transactional:

    // secondtab will be recorded because there is no tr
    public void descendantSupports() {
        saveToSecond("secondtab");
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

    ////
    @Transactional(readOnly = true)
    public void readOnly() {
        saveToSecond("readonly");
//        throw new ArithmeticException();
    }


    private WorkerPriopagationAndRollbacks getAnotherBean()
    {
        return context.getBean(WorkerPriopagationAndRollbacks.class);

    }

    private void saveToFirst(String name) {
        FirstTab firstTab = new FirstTab();
        firstTab.setName(name);
        firstRepository.save(firstTab);
    }

    private void saveToSecond(String name) {
        SecondTab secondTab = new SecondTab();
        secondTab.setName(name);
        secondRepository.save(secondTab);
    }


}

