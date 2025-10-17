package com.example.miniProjekt.repository;

import com.example.miniProjekt.model.Roster;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

/**
 * Repository for Roster (vagtplan).
 * Spring Data afleder queries fra metodenavne.
 */
public interface RosterRepository extends JpaRepository<Roster, Long> {

    /**
     * Find alle vagter på en bestemt dato.
     * *@param *date dato (lokal kalenderdag)
     * @return liste af Roster
     */
    List<Roster> findByWorkDate(LocalDate date);



    /**
     * Find alle vagter i et datointerval.
     * <p>
     * Bemærk: Spring Data / JPQL {@code Between} er inklusiv i begge ender
     * for sammenlignelige typer som {@link LocalDate}.
     *
     * @param from startdato (inkl.)
     * @param to   slutdato (inkl.)
     * @return liste af Roster i intervallet
     */
    List<Roster> findByWorkDateBetween(LocalDate from, LocalDate to);




    /**
     * Find en medarbejders vagter på en bestemt dato.
     * @param employeeId ID på medarbejder
     * @param *date dato (lokal kalenderdag)
     * @return liste af Roster
     */
    boolean existsByEmployee_IdAndWorkDate(Long employeeId, LocalDate workDate);

}
