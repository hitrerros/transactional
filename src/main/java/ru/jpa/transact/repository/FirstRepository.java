package ru.jpa.transact.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.jpa.transact.entity.FirstTab;

public interface FirstRepository extends JpaRepository<FirstTab,Long> {
}
