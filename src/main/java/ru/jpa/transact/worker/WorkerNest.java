package ru.jpa.transact.worker;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.jpa.transact.repository.FirstRepository;
import ru.jpa.transact.repository.SecondRepository;

import static org.springframework.transaction.annotation.Propagation.NOT_SUPPORTED;
import static org.springframework.transaction.annotation.Propagation.REQUIRED;

@Service(value = "nest")
public class WorkerNest extends WorkerPriopagationAndRollbacks {
    public WorkerNest(FirstRepository firstRepository, SecondRepository secondRepository, ApplicationContext context) {
        super(firstRepository, secondRepository, context);
    }


    ////////////////////// REQUIRED + NESTED
    @Transactional(propagation = REQUIRED)
    public void required1() {
        saveToFirst("outer_requires_new");
        getAnotherBean().requiredNew();
    }

    @Transactional(propagation = NOT_SUPPORTED)
    public void requiredNew() {
        saveToSecond("inner_requires_new");
        //     throw  new ArithmeticException();
    }

    private WorkerNest getAnotherBean() {
        return context.getBean(WorkerNest.class);
    }

}
