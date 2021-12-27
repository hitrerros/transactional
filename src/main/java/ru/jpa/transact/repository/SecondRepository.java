package ru.jpa.transact.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.jpa.transact.entity.SecondTab;

public interface SecondRepository extends JpaRepository<SecondTab,Long> {
}
