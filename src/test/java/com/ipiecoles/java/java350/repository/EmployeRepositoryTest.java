package com.ipiecoles.java.java350.repository;

import com.ipiecoles.java.java350.model.Employe;
import com.ipiecoles.java.java350.model.Entreprise;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;

@DataJpaTest
public class EmployeRepositoryTest {

    @Autowired
    private EmployeRepository employeRepository;

    @BeforeEach
    @AfterEach
    public void setup(){
        employeRepository.deleteAll();
    }

    @Test
    public void testFindLastMatriculeEmpty(){
        //Given

        //When
        String lastMatricule = employeRepository.findLastMatricule();

        //Then
        Assertions.assertNull(lastMatricule);
    }

    @Test
    public void testFindLastMatriculeSingle(){
        //Given
        employeRepository.save(new Employe("Doe", "John", "T12345", LocalDate.now(), Entreprise.SALAIRE_BASE, 1, 1.0));

        //When
        String lastMatricule = employeRepository.findLastMatricule();

        //Then
        Assertions.assertEquals("12345", lastMatricule);
    }

    @Test
    public void testFindLastMatriculeMultiple(){
        //Given
        employeRepository.save(new Employe("Doe", "John", "T12345", LocalDate.now(), Entreprise.SALAIRE_BASE, 1, 1.0));
        employeRepository.save(new Employe("Doe", "Jane", "M40325", LocalDate.now(), Entreprise.SALAIRE_BASE, 1, 1.0));
        employeRepository.save(new Employe("Doe", "Jim", "C06432", LocalDate.now(), Entreprise.SALAIRE_BASE, 1, 1.0));

        //When
        String lastMatricule = employeRepository.findLastMatricule();

        //Then
        Assertions.assertEquals("40325", lastMatricule);
    }

    @Test
    public void avgPerformanceWhereMatriculeStartsWithNull(){
        //GIVEN
        employeRepository.save(new Employe("Doe", "John", "T12345", LocalDate.now(), 1500d, null, 1.0));
        employeRepository.save(new Employe("Doe", "John", "T40325", LocalDate.now(), 1500d, null, 1.0));
        employeRepository.save(new Employe("Doe", "John", "T06432", LocalDate.now(), 1500d, null, 1.0));        //WHEN
        Double moyenne = employeRepository.avgPerformanceWhereMatriculeStartsWith("T");
        //THEN
        org.assertj.core.api.Assertions.assertThat(moyenne).isNull();
    }

    @Test
    public void avgPerformanceWhereMatriculeStartsWithCalcul(){
        //GIVEN
        employeRepository.save(new Employe("Doe", "John", "M12345", LocalDate.now(), 1500d, 100, 1.0));
        employeRepository.save(new Employe("Doe", "John", "M40325", LocalDate.now(), 1500d, 200, 1.0));
        employeRepository.save(new Employe("Doe", "John", "M06432", LocalDate.now(), 1500d, 0, 1.0));        //WHEN
        Double moyenne = employeRepository.avgPerformanceWhereMatriculeStartsWith("M");
        //THEN
        org.assertj.core.api.Assertions.assertThat(moyenne).isEqualTo(100);
    }

    @Test
    public void avgPerformanceWhereMatriculeStartsWith0(){
        //GIVEN
        employeRepository.save(new Employe("Doe", "John", "C12345", LocalDate.now(), 1500d, 0, 1.0));
        employeRepository.save(new Employe("Doe", "John", "C40325", LocalDate.now(), 1500d, 0, 1.0));
        employeRepository.save(new Employe("Doe", "John", "C06432", LocalDate.now(), 1500d, 0, 1.0));        //WHEN
        Double moyenne = employeRepository.avgPerformanceWhereMatriculeStartsWith("C");
        //THEN
        org.assertj.core.api.Assertions.assertThat(moyenne).isZero();
    }
    @Test
    public void avgPerformanceWhereMatriculeStartsWithOneNull(){
        //GIVEN
        employeRepository.save(new Employe("Doe", "John", "Z12345", LocalDate.now(), 1500d, 100, 1.0));
        employeRepository.save(new Employe("Doe", "John", "Z40325", LocalDate.now(), 1500d, null, 1.0));
        employeRepository.save(new Employe("Doe", "John", "Z06432", LocalDate.now(), 1500d, 200, 1.0));        //WHEN
        Double moyenne = employeRepository.avgPerformanceWhereMatriculeStartsWith("Z");
        //THEN
        // Si un employé possede une performance null il n'est pas compter dans la moyenne
        org.assertj.core.api.Assertions.assertThat(moyenne).isEqualTo(150);
    }

    @BeforeEach
    @AfterEach
    public void purgeBDD(){
        employeRepository.deleteAll();
    }
}