package com.ipiecoles.java.java350.service;

import com.ipiecoles.java.java350.exception.EmployeException;
import com.ipiecoles.java.java350.model.Employe;
import com.ipiecoles.java.java350.model.NiveauEtude;
import com.ipiecoles.java.java350.model.Poste;
import com.ipiecoles.java.java350.repository.EmployeRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.EntityExistsException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmployeServiceTest {

    @InjectMocks
    EmployeService employeService;

    @Mock
    EmployeRepository employeRepository;

    @BeforeEach
    public void setup(){
        MockitoAnnotations.initMocks(this.getClass());
    }

    @Test
    public void testEmbaucheEmployeTechnicienPleinTempsBts() throws EmployeException {
        //Given
        String nom = "Doe";
        String prenom = "John";
        Poste poste = Poste.TECHNICIEN;
        NiveauEtude niveauEtude = NiveauEtude.BTS_IUT;
        Double tempsPartiel = 1.0;
        when(employeRepository.findLastMatricule()).thenReturn("00345");
        when(employeRepository.findByMatricule("T00346")).thenReturn(null);

        //When
        employeService.embaucheEmploye(nom, prenom, poste, niveauEtude, tempsPartiel);

        //Then
        ArgumentCaptor<Employe> employeArgumentCaptor = ArgumentCaptor.forClass(Employe.class);
        verify(employeRepository, times(1)).save(employeArgumentCaptor.capture());
        Assertions.assertEquals(nom, employeArgumentCaptor.getValue().getNom());
        Assertions.assertEquals(prenom, employeArgumentCaptor.getValue().getPrenom());
        Assertions.assertEquals(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")), employeArgumentCaptor.getValue().getDateEmbauche().format(DateTimeFormatter.ofPattern("yyyyMMdd")));
        Assertions.assertEquals("T00346", employeArgumentCaptor.getValue().getMatricule());
        Assertions.assertEquals(tempsPartiel, employeArgumentCaptor.getValue().getTempsPartiel());

        //1521.22 * 1.2 * 1.0
        Assertions.assertEquals(1825.464, employeArgumentCaptor.getValue().getSalaire().doubleValue());
    }

    @Test
    public void testEmbaucheEmployeManagerMiTempsMaster() throws EmployeException {
        //Given
        String nom = "Doe";
        String prenom = "John";
        Poste poste = Poste.MANAGER;
        NiveauEtude niveauEtude = NiveauEtude.MASTER;
        Double tempsPartiel = 0.5;
        when(employeRepository.findLastMatricule()).thenReturn("00345");
        when(employeRepository.findByMatricule("M00346")).thenReturn(null);

        //When
        employeService.embaucheEmploye(nom, prenom, poste, niveauEtude, tempsPartiel);

        //Then
        ArgumentCaptor<Employe> employeArgumentCaptor = ArgumentCaptor.forClass(Employe.class);
        verify(employeRepository, times(1)).save(employeArgumentCaptor.capture());
        Assertions.assertEquals(nom, employeArgumentCaptor.getValue().getNom());
        Assertions.assertEquals(prenom, employeArgumentCaptor.getValue().getPrenom());
        Assertions.assertEquals(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")), employeArgumentCaptor.getValue().getDateEmbauche().format(DateTimeFormatter.ofPattern("yyyyMMdd")));
        Assertions.assertEquals("M00346", employeArgumentCaptor.getValue().getMatricule());
        Assertions.assertEquals(tempsPartiel, employeArgumentCaptor.getValue().getTempsPartiel());

        //1521.22 * 1.4 * 0.5
        Assertions.assertEquals(1064.854, employeArgumentCaptor.getValue().getSalaire().doubleValue());
    }

    @Test
    public void testEmbaucheEmployeManagerMiTempsMasterNoLastMatricule() throws EmployeException {
        //Given
        String nom = "Doe";
        String prenom = "John";
        Poste poste = Poste.MANAGER;
        NiveauEtude niveauEtude = NiveauEtude.MASTER;
        Double tempsPartiel = 0.5;
        when(employeRepository.findLastMatricule()).thenReturn(null);
        when(employeRepository.findByMatricule("M00001")).thenReturn(null);

        //When
        employeService.embaucheEmploye(nom, prenom, poste, niveauEtude, tempsPartiel);

        //Then
        ArgumentCaptor<Employe> employeArgumentCaptor = ArgumentCaptor.forClass(Employe.class);
        verify(employeRepository, times(1)).save(employeArgumentCaptor.capture());
        Assertions.assertEquals("M00001", employeArgumentCaptor.getValue().getMatricule());
    }

    @Test
    public void testEmbaucheEmployeManagerMiTempsMasterExistingEmploye(){
        //Given
        String nom = "Doe";
        String prenom = "John";
        Poste poste = Poste.MANAGER;
        NiveauEtude niveauEtude = NiveauEtude.MASTER;
        Double tempsPartiel = 0.5;
        when(employeRepository.findLastMatricule()).thenReturn(null);
        when(employeRepository.findByMatricule("M00001")).thenReturn(new Employe());

        //When/Then
        EntityExistsException e = Assertions.assertThrows(EntityExistsException.class, () -> employeService.embaucheEmploye(nom, prenom, poste, niveauEtude, tempsPartiel));
        Assertions.assertEquals("L'employé de matricule M00001 existe déjà en BDD", e.getMessage());
    }

    @Test
    public void testEmbaucheEmployeManagerMiTempsMaster99999(){
        //Given
        String nom = "Doe";
        String prenom = "John";
        Poste poste = Poste.MANAGER;
        NiveauEtude niveauEtude = NiveauEtude.MASTER;
        Double tempsPartiel = 0.5;
        when(employeRepository.findLastMatricule()).thenReturn("99999");

        //When/Then
        EmployeException e = Assertions.assertThrows(EmployeException.class, () -> employeService.embaucheEmploye(nom, prenom, poste, niveauEtude, tempsPartiel));
        Assertions.assertEquals("Limite des 100000 matricules atteinte !", e.getMessage());
    }

    @Test
    public void testEmbaucheEmployeExisteDeja() throws EmployeException {
        //Given Pas d'employés en base
        String nom = "Doe";
        String prenom = "John";
        Poste poste = Poste.TECHNICIEN;
        NiveauEtude niveauEtude = NiveauEtude.BTS_IUT;
        Double tempsPartiel = 1.0;
        Employe employeExistant = new Employe("Doe", "Jane", "T00001", LocalDate.now(), 1500d, 1, 1.0);
        //Simuler qu'aucun employé n'est présent (ou du moins aucun matricule)
        Mockito.when(employeRepository.findLastMatricule()).thenReturn(null);
        //Simuler que la recherche par matricule renvoie un employé (un employé a été embauché entre temps)
        Mockito.when(employeRepository.findByMatricule("T00001")).thenReturn(employeExistant);
        //When
        try {
            employeService.embaucheEmploye(nom, prenom, poste, niveauEtude, tempsPartiel);
            org.assertj.core.api.Assertions.fail("embaucheEmploye aurait dû lancer une exception");
        } catch (Exception e){
            //Then
            org.assertj.core.api.Assertions.assertThat(e).isInstanceOf(EntityExistsException.class);
            org.assertj.core.api.Assertions.assertThat(e.getMessage()).isEqualTo("L'employé de matricule T00001 existe déjà en BDD");
        }
    }

    @ParameterizedTest(name = "Performance{0}, matricule {1} objectifCa{2}, Catraite {3}, majPerformance {4}")
    @CsvSource({"1,'C12345',1000,0,1",
            "1,'C12345',1000,900,1",
            "2,'C12345',1000,900,1",
            "3,'C12345',1000,900,1",
            "4,'C12345',1000,900,2",
            "1,'C12345',1000,999,1",
            "4,'C12345',1000,1001,4",
            "1,'C12345',1000,1051,2",
            "100,'C12345',1000,1200,101",
            "1,'C12345',1000,1201,5",
            "100,'C12345',1000,10000,104",
            "5,'C12345',1000,0,1"})
    public void testCalculPerfCommercialParametre(Integer performance, String matricule, Long objectifCa, Long caTraite, Integer majPerformance) throws EmployeException {
        //Given
        Employe commercial = new Employe("Doe", "Jane", matricule, LocalDate.now(), 1500d, performance, 1.0);
        Mockito.when(employeRepository.findByMatricule(matricule)).thenReturn(commercial);
        Mockito.when(employeRepository.avgPerformanceWhereMatriculeStartsWith("C")).thenReturn(null);
        //When
        employeService.calculPerformanceCommercial(matricule,caTraite,objectifCa);
        //Then
        org.assertj.core.api.Assertions.assertThat(commercial.getPerformance()).isEqualTo(majPerformance);
    }

    @ParameterizedTest(name = "Perf{0}, matricule {1} objectifCa{2}, Catraite {3}")
    @CsvSource({"1,'M12345',0,10000",
            "1,'',0,10000",
            "1, ,0,10000",
            "1,'T12345',0,2000"})
    public void testCalculPerfCommercialMatriculeNotCorNull(Integer performance,String matricule,Long objectifCa,Long caTraite) {
        //Given
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
    @CsvSource({"1,'C12345',,10000",
            "1,'C12345',-1000,10000"})
    public void testCalculPerfCommercialObjectifCa(Integer performance,String matricule,Long objectifCa,Long caTraite) {
        //Given

        //When
        try {
            employeService.calculPerformanceCommercial(matricule,caTraite,objectifCa);
            org.assertj.core.api.Assertions.fail("calculPerformanceCommercial aurait dû lancer une exception");
        } catch (EmployeException e) {
            //Then
            org.assertj.core.api.Assertions.assertThat(e.getMessage()).isEqualTo("L'objectif de chiffre d'affaire ne peut être négatif ou null !");
        }
    }


    @ParameterizedTest(name = "Perf{0}, matricule {1} objectifCa{2}, Catraite {3}")
    @CsvSource({"1,'C12345',10000,",
            "1,'C12345',10000,-10000"})
    public void testCalculPerfCommercialCatraite(Integer performance,String matricule,Long objectifCa,Long caTraite)  {
        //Given
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
    public void testCalculPerfCommercialEmployeNull() {
        //Given
        String matricule = "C06432";
        Long caTraite = 100000L;
        Long objectifCa = 100000L;
        //When
        try {
            employeService.calculPerformanceCommercial(matricule,caTraite,objectifCa);
            org.assertj.core.api.Assertions.fail("calculPerformanceCommercial aurait dû lancer une exception");
        } catch (EmployeException e) {
            //Then
            org.assertj.core.api.Assertions.assertThat(e.getMessage()).isEqualTo("Le matricule C06432 n'existe pas !");
        }
    }

    @ParameterizedTest(name = "Perf{0}, matricule {1} objectifCa{2}, Catraite {3}")
    @CsvSource({"0,'C12345',0,10000",
            "-1,'C12345',0,10000",
            "-100,'C12345' ,0,10000",
            ",'C12345',0,2000"})
    public void testCalculPerfCommercialPerfomanceNullOrInferieur1(Integer performance,String matricule,Long objectifCa,Long caTraite) {
        //Given
        Employe commercial = new Employe("Doe", "Jane", matricule, LocalDate.now(), 1500d, performance, 1.0);
        Mockito.when(employeRepository.findByMatricule(matricule)).thenReturn(commercial);
        //When
        try {
            employeService.calculPerformanceCommercial(matricule,caTraite,objectifCa);
            org.assertj.core.api.Assertions.fail("calculPerformanceCommercial aurait dû lancer une exception");
        } catch (EmployeException e) {
            //Then
            org.assertj.core.api.Assertions.assertThat(e.getMessage()).isEqualTo("La performance ne peut être null ou inférieur a 1 !");
        }
    }

}