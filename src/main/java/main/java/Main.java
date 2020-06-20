package main.java;

import preprocessing.model.EntitySchedule;
import preprocessing.model.ProfessorsScheduleCreation;

import java.io.*;

public class Main {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        //TODO Alterar atributos do domain e corrigir possiveis conflitos

        //Porcentagem para unir os cursos em conjuntos
        ProfessorsScheduleCreation psc = new ProfessorsScheduleCreation("src/Datasets/IFSCFiles/Dados_ifsc_2019.xlsx");
        psc.createReport();


        final int percentage = 30;
        EntitySchedule entitySchedule = new EntitySchedule(psc);
        entitySchedule.createSet(percentage);

    }


}
