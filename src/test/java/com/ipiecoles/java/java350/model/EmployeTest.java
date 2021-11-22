package com.ipiecoles.java.java350.model;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

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
    public void testGetAnneeAcienneteDateEmbaucheInfNow(){
        //GIVEN
        Employe employe = new Employe("Doe","Jonh","T12345", LocalDate.now().minusYears(6),1500d,1,1.0);
        // WHEN
        Integer anneeAnciennete = employe.getNombreAnneeAnciennete();
        //THEN
        Assertions.assertThat(anneeAnciennete).isGreaterThanOrEqualTo(6);
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

}