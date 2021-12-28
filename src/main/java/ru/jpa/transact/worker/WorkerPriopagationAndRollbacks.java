package ru.jpa.transact.worker;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.jpa.transact.entity.FirstTab;
import ru.jpa.transact.entity.SecondTab;
import ru.jpa.transact.exception.MyException;
import ru.jpa.transact.repository.FirstRepository;
import ru.jpa.transact.repository.SecondRepository;

import static org.springframework.transaction.annotation.Propagation.MANDATORY;
import static org.springframework.transaction.annotation.Propagation.NESTED;
import static org.springframework.transaction.annotation.Propagation.NEVER;
import static org.springframework.transaction.annotation.Propagation.NOT_SUPPORTED;
import static org.springframework.transaction.annotation.Propagation.REQUIRED;
import static org.springframework.transaction.annotation.Propagation.REQUIRES_NEW;
import static org.springframework.transaction.annotation.Propagation.SUPPORTS;


@Service(value = "root")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class WorkerPriopagationAndRollbacks {

    protected final FirstRepository firstRepository;
    protected final SecondRepository secondRepository;
    protected final ApplicationContext context;

    //// "dontrollbackon" will be recorded despite throw being invoked
    @Transactional(noRollbackFor = ArithmeticException.class)  // run-time exception
    public void noRollbackFor() {
        saveToFirst("norollbackFor");
        throw new ArithmeticException();
    }

    //// "rollback for "
    @Transactional(rollbackFor = MyException.class)  // checked exception
    public void rollbackFor() throws MyException {
        saveToFirst("rollbackFor");
        throw new MyException();
    }

    //// "rollback for "
    @Transactional  // checked exception will be recorded
    public void checkedException() throws MyException {
        saveToFirst("checked_exception");
        throw new MyException();
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

    //// readOnly = true nothing happens
    @Transactional(readOnly = true)
    public void readOnly() {
        saveToSecond("readonly");
//        throw new ArithmeticException();
    }

    //// propagation = MANDATORY
    @Transactional(propagation = MANDATORY)
    public void mandatory() {
        saveToFirst("mandatory");
    }


    // required then never  rollbacks required
    @Transactional(propagation = REQUIRED)
    public void requiredThenNever() {
        saveToFirst("required_then_never");
        getAnotherBean().requiredThenNever2();
    }

    @Transactional(propagation = NEVER)
    public void requiredThenNever2() {
        saveToSecond("required_then_never");

    }

    // required then never doesn't rollback because itself inkoved, never is ignored
    @Transactional(propagation = REQUIRED)
    public void requiredThenNeverItself() {
        saveToFirst("required_then_never_itself");
        requiredThenNever2Itself();
    }

    @Transactional(propagation = NEVER)
    public void requiredThenNever2Itself() {
        saveToSecond("required_then_itself_ignored");

    }


    ////////////////////// REQUIRED + NESTED
    @Transactional(propagation = REQUIRED)
    public void requiredForNested() {
        saveToFirst("required_then_nested");
        getAnotherBean().invokeNested();
        throw new ArithmeticException();
    }

    @Transactional(propagation = NESTED)
    public void invokeNested() {
        saveToSecond("required_nested");
    }


    ////////////////////// REQUIRED + NESTED
    @Transactional(propagation = REQUIRED)
    public void requiredThenRequiredNew() {
        saveToFirst("required_then_required_new");
        getAnotherBean().invokeRequiredNew();
        throw new ArithmeticException();
    }

    @Transactional(propagation = REQUIRES_NEW)
    public void invokeRequiredNew() {
        saveToSecond("second_requires_new");
    }


    private WorkerPriopagationAndRollbacks getAnotherBean() {
        return context.getBean(WorkerPriopagationAndRollbacks.class);
    }

    protected void saveToFirst(String name) {
        FirstTab firstTab = new FirstTab();
        firstTab.setName(name);
        firstRepository.save(firstTab);
    }

    protected void saveToSecond(String name) {
        SecondTab secondTab = new SecondTab();
        secondTab.setName(name);
        secondRepository.save(secondTab);
    }

}

