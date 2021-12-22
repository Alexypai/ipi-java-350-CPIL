package com.ipiecoles.java.java350.service;

import com.ipiecoles.java.java350.exception.EmployeException;
import com.ipiecoles.java.java350.model.*;
import com.ipiecoles.java.java350.repository.EmployeRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class EmployeServiceIntegrationTest {

    @Autowired
    EmployeService employeService;

    @Autowired
    private EmployeRepository employeRepository;

    @BeforeEach
    @AfterEach
    public void setup(){
        employeRepository.deleteAll();
    }

    @Test
    public void integrationEmbaucheEmploye() throws EmployeException {
        //Given
        employeRepository.save(new Employe("Doe", "John", "T12345", LocalDate.now(), Entreprise.SALAIRE_BASE, 1, 1.0));
        String nom = "Doe";
        String prenom = "John";
        Poste poste = Poste.TECHNICIEN;
        NiveauEtude niveauEtude = NiveauEtude.BTS_IUT;
        Double tempsPartiel = 1.0;

        //When
        employeService.embaucheEmploye(nom, prenom, poste, niveauEtude, tempsPartiel);

        //Then
        Employe employe = employeRepository.findByMatricule("T12346");
        Assertions.assertNotNull(employe);
        Assertions.assertEquals(nom, employe.getNom());
        Assertions.assertEquals(prenom, employe.getPrenom());
        Assertions.assertEquals(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")), employe.getDateEmbauche().format(DateTimeFormatter.ofPattern("yyyyMMdd")));
        Assertions.assertEquals("T12346", employe.getMatricule());
        Assertions.assertEquals(1.0, employe.getTempsPartiel().doubleValue());

        //1521.22 * 1.2 * 1.0
        Assertions.assertEquals(1825.464, employe.getSalaire().doubleValue());
    }

    @Test
    public void testEmbauchePremierEmployeTempsNull() throws EmployeException {
        //Given Pas d'employés en base
        String nom = "Doe";
        String prenom = "John";
        Poste poste = Poste.TECHNICIEN;
        NiveauEtude niveauEtude = NiveauEtude.BTS_IUT;
        Double tempsPartiel = null;
        //When
        employeService.embaucheEmploye(nom, prenom, poste, niveauEtude, tempsPartiel);
        //Then
        Employe employe = employeRepository.findByMatricule("T00001");
        org.assertj.core.api.Assertions.assertThat(employe).isNotNull();
        org.assertj.core.api.Assertions.assertThat(employe.getNom()).isEqualTo(nom);
        org.assertj.core.api.Assertions.assertThat(employe.getPrenom()).isEqualTo(prenom);
        org.assertj.core.api.Assertions.assertThat(employe.getSalaire()).isEqualTo(1825.464);
        org.assertj.core.api.Assertions.assertThat(employe.getTempsPartiel()).isNull();
        org.assertj.core.api.Assertions.assertThat(employe.getDateEmbauche()).isEqualTo(LocalDate.now());
        org.assertj.core.api.Assertions.assertThat(employe.getMatricule()).isEqualTo("T00001");
    }


    @ParameterizedTest(name = "Performance{0}, matricule {1} objectifCa{2}, Catraite {3}, majPerformance {4}")
    @CsvSource({"1,'C12345',1000,0,1",
            "1,'C12345',1000,900,1",
            "2,'C12345',1000,900,1",
            "3,'C12345',1000,900,1",
            "4,'C12345',1000,900,2",
            "1,'C12345',1000,999,1",
            "4,'C12345',1000,1001,4",
            "1,'C12345',1000,1051,3",
            "100,'C12345',1000,1200,102",
            "1,'C12345',1000,1201,6",
            "100,'C12345',1000,10000,105",
            "5,'C12345',1000,0,1"})
    public void testCalculPerfCommercialParametre(Integer performance,String matricule,Long objectifCa,Long caTraite,Integer majPerformance) throws EmployeException {
        //Given
        employeRepository.save(new Employe("Doe", "John", matricule, LocalDate.now(), 1500d, performance, 1.0));

        //When
        employeService.calculPerformanceCommercial(matricule,caTraite,objectifCa);
        //Then
        Employe employe = employeRepository.findByMatricule(matricule);
        org.assertj.core.api.Assertions.assertThat(employe.getPerformance()).isEqualTo(majPerformance);
    }

    @Test
    public void testCalculPerfCommercialObjectifNull() {
        //Given
        employeRepository.save(new Employe("Doe", "John", "C06432", LocalDate.now(), 1500d, 1, 1.0));
        String matricule = "C06432";
        Long caTraite = 10000L;
        Long objectifCa = null;

        //When
        try {
            employeService.calculPerformanceCommercial(matricule,caTraite,objectifCa);
            org.assertj.core.api.Assertions.fail("calculPerformanceCommercial aurait dû lancer une exception");
        } catch (EmployeException e) {
            //Then
            org.assertj.core.api.Assertions.assertThat(e.getMessage()).isEqualTo("L'objectif de chiffre d'affaire ne peut être négatif ou null !");
        }
    }

    @Test
    public void testCalculPerfCommercialObjectif0() {
        //Given
        employeRepository.save(new Employe("Doe", "John", "C06432", LocalDate.now(), 1500d, 1, 1.0));
        String matricule = "C06432";
        Long caTraite = 10000L;
        Long objectifCa = -100L;

        //When
        try {
            employeService.calculPerformanceCommercial(matricule,caTraite,objectifCa);
            org.assertj.core.api.Assertions.fail("calculPerformanceCommercial aurait dû lancer une exception");
        } catch (EmployeException e) {
            //Then
            org.assertj.core.api.Assertions.assertThat(e.getMessage()).isEqualTo("L'objectif de chiffre d'affaire ne peut être négatif ou null !");
        }
    }

    @Test
    public void testCalculPerfCommercialCatraiteNull() {
        //Given
        employeRepository.save(new Employe("Doe", "John", "C06432", LocalDate.now(), 1500d, 1, 1.0));
        String matricule = "C06432";
        Long caTraite = null;
        Long objectifCa = 100000L;

        //When
        try {
            employeService.calculPerformanceCommercial(matricule,caTraite,objectifCa);
            org.assertj.core.api.Assertions.fail("calculPerformanceCommercial aurait dû lancer une exception");
        } catch (EmployeException e) {
            //Then
            org.assertj.core.api.Assertions.assertThat(e.getMessage()).isEqualTo("Le chiffre d'affaire traité ne peut être négatif ou null !");
        }
    }

    @Test
    public void testCalculPerfCommercialCatraite0() {
        //Given
        employeRepository.save(new Employe("Doe", "John", "C06432", LocalDate.now(), 1500d, 1, 1.0));
        String matricule = "C06432";
        Long caTraite = -100000L;
        Long objectifCa = 100000L;
        //When
        try {
            employeService.calculPerformanceCommercial(matricule,caTraite,objectifCa);
            org.assertj.core.api.Assertions.fail("calculPerformanceCommercial aurait dû lancer une exception");
        } catch (EmployeException e) {
            //Then
            org.assertj.core.api.Assertions.assertThat(e.getMessage()).isEqualTo("Le chiffre d'affaire traité ne peut être négatif ou null !");
        }
    }

    @ParameterizedTest(name = "Perf{0}, matricule {1} objectifCa{2}, Catraite {3}")
    @CsvSource({"1,'M12345',0,10000",
            "1,'',0,10000",
            "1, ,0,10000",
            "1,'T12345',0,2000"})
    public void testCalculPerfCommercialMatriculeNotCorNull(Integer performance,String matricule,Long objectifCa,Long caTraite) {
        //Given
        employeRepository.save(new Employe("Doe", "John", matricule, LocalDate.now(), 1500d, performance, 1.0));

        //When
        try {
            employeService.calculPerformanceCommercial(matricule,caTraite,objectifCa);
            org.assertj.core.api.Assertions.fail("calculPerformanceCommercial aurait dû lancer une exception");
        } catch (EmployeException e) {
            //Then
            org.assertj.core.api.Assertions.assertThat(e.getMessage()).isEqualTo("Le matricule ne peut être null et doit commencer par un C !");
        }
    }


    @ParameterizedTest(name = "Perf{0}, matricule {1} objectifCa{2}, Catraite {3}")
    @CsvSource({"0,'C12345',0,10000",
            "-1,'C12345',0,10000",
            "-100,'C12345' ,0,10000",
            ",'C12345',0,2000"})
    public void testCalculPerfCommercialPerfomanceNullOrInferieur1(Integer performance,String matricule,Long objectifCa,Long caTraite) {
        //Given
        employeRepository.save(new Employe("Doe", "John", matricule, LocalDate.now(), 1500d, performance, 1.0));

        //When
        try {
            employeService.calculPerformanceCommercial(matricule,caTraite,objectifCa);
            org.assertj.core.api.Assertions.fail("calculPerformanceCommercial aurait dû lancer une exception");
        } catch (EmployeException e) {
            //Then
            org.assertj.core.api.Assertions.assertThat(e.getMessage()).isEqualTo("La performance ne peut être null ou inférieur a 1 !");
        }
    }



    @BeforeEach
    @AfterEach
    public void purgeBDD(){
        employeRepository.deleteAll();
    }



}
