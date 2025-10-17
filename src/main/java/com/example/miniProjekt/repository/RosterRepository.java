package com.example.miniProjekt.repository;

import com.example.miniProjekt.model.Roster;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface RosterRepository extends JpaRepository<Roster, Long> {
    List<Roster> findByWorkDate(LocalDate date);
    List<Roster> findByWorkDateBetween(LocalDate from, LocalDate to);
    List<Roster> findByInstructorNameIgnoreCase(String instructorName);
    boolean existsByEmployee_IdAndWorkDate(Long employeeId, LocalDate workDate);

}
