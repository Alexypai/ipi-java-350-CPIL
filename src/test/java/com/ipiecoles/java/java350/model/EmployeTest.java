package com.ipiecoles.java.java350.model;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class EmployeTest {

    @Test
    public void testGetAnneeAcienneteDateEmbaucheNull(){
        //GIVEN
        Employe employe = new Employe();
        employe.setDateEmbauche(null);
        // WHEN
        Integer anneeAnciennete = employe.getNombreAnneeAnciennete();
        //THEN
        Assertions.assertThat(anneeAnciennete).isNull();
    }

    @Test
    public void testGetAnneeAcienneteDateEmbaucheInfNow() {
        //GIVEN
        Employe employe = new Employe("Doe", "Jonh", "T12345", LocalDate.now().minusYears(6), 1500d, 1, 1.0);
        // WHEN
        Integer anneeAnciennete = employe.getNombreAnneeAnciennete();
        //THEN
        Assertions.assertThat(anneeAnciennete).isEqualTo(6);
    }

    @Test
    public void testGetAnneeAcienneteDateEmbaucheSupNow(){
        //GIVEN
        Employe employe = new Employe("Doe","Jonh","T12345", LocalDate.now().plusYears(6),1500d,1,1.0);
        // WHEN
        Integer anneeAnciennete = employe.getNombreAnneeAnciennete();
        //THEN
        Assertions.assertThat(anneeAnciennete).isNull();
    }

    @ParameterizedTest(name = "Perf{0}, matricule {1} txActivite{2}, anciennete {3} => prime {4} ")
    @CsvSource({"1,'T12345',1.0,0,1000.0",
            "1,'T12345',0.5,0,500.0",
            "2,'T12345',1.0,0,2300.0",
            "1,'T12345',1.0,2,1200.0",
            "2,'T12345',1.0,1,2400.0",
            "1,'M12345',1.0,0,1700.0",
            "1,'M12345',1.0,3,2000.0",
            "1,'null',1.0,3,1300.0"})
    public void testGetPrimeAnnuelle(Integer perforance,String matricule,Double tauxActivite,Long nbAnneesAnciennete,Double primeAttendue){

        //GIVEN
        Employe employe = new Employe("Doe","Jonh",matricule, LocalDate.now().minusYears(nbAnneesAnciennete),1500d,perforance,tauxActivite);
        // WHEN
        Double prime = employe.getPrimeAnnuelle();
        //THEN
        Assertions.assertThat(prime).isEqualTo(primeAttendue);
    }


    @Test
    public void testGetPrimeAnnuellePerformanceNull(){
        //GIVEN
        Employe employe = new Employe("Doe","John",null,LocalDate.now(),1500d,null,1.0);
        //WHEN
        Double prime = employe.getPrimeAnnuelle();
        //THEN
        Assertions.assertThat(prime).isEqualTo(1000.0);
    }

    @ParameterizedTest(name = "pourcentage{0}, salaire {1}, NewSalaire{2}")
    @CsvSource({"10,'1500d',1673.22",
            "-10,'1700d',1700",
            "20,,1825.22",
            "0.001,'1700d',1700",
            "-0.001,'1700d',1700",
            "0.1,'1500d',1523.22",
            "-0.1,'1500d',1521.22",
            "0.0,'1500d',1521.22",
            "0.00001,'1700d',1700",
            "0,'1700d',1700"})
    public void testAaugmenterSalairePourcentageManyValue(Double pourcentage,Double salaire,Double NewSalaire){
        //GIVEN
        Employe employe = new Employe("Doe","John",null,LocalDate.now(),salaire,1,1.0);
        //WHEN
        Double SalaireAttendu = employe.augmenterSalaire(pourcentage);
        //THEN
        Assertions.assertThat(NewSalaire).isEqualTo(SalaireAttendu);
    }

    @Test
    public void testAaugmenterSalairePourcentageNull(){
        //GIVEN
        Employe employe = new Employe("Doe","John",null,LocalDate.now(),1700d,1,1.0);
        Double pourcentage = null;
        //WHEN
        Double NewSalaire = employe.augmenterSalaire(pourcentage);
        //THEN
        Assertions.assertThat(NewSalaire).isEqualTo(1700d);
    }
}