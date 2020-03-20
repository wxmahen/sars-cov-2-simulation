package org.noimos.simulation;

import java.util.ArrayList;
import java.util.List;

public class Simulation {

    private final static int ICU_BEDS = 500;
    private final static int POPULATION = 21500000;
    private final static double SPREAD_FACTOR = 0.22;
    private final static double SERIOUSNESS_RATE = 0.2;
    private final static double AVG_INFECTION_DAYS = 2;
    private final static double AVG_SYMPTOM_DAYS = 7;
    private final static double AVG_CURE_DAYS = 7;
    private final static double DEATH_RATE = 0.04;

    private int preInfectedOutside = 0;
    private int infectedOutside = 70;
    private int spreadingOutside = 0;
    private int hospitalized = 70;
    private int serious = 11;
    private int recovered = 3;
    private int dead = 0;
    private List<Integer> admissions = new ArrayList<>();

    private void runSimulation() {
        System.out.println("Day\tInf\tSpread\tHosp\tSeri\tRecov\tDead");
        int i = 0;
        while (hospitalized > 0) {
            i++;
            runForADay();
            System.out.println(i + "\t" + infectedOutside + "\t" + spreadingOutside + "\t" + hospitalized + "\t" + serious + "\t" + recovered + "\t" + dead);
        }
        printResults();
    }

    private void runForADay() {
        if (admissions.size() >= AVG_CURE_DAYS) {
            int in = admissions.get(0);
            if (in == 0) {
                in = hospitalized;
            }
            double deathRate = DEATH_RATE;
            int r = (int) Math.round((double) in * (1.0 - deathRate));
            int d = (int) Math.round((double) in * deathRate);
            if (serious > ICU_BEDS) {
                int killing = (serious - ICU_BEDS);
                deathRate = (((double) d) + ((double) killing)) / ((double) d);
                if (deathRate > SERIOUSNESS_RATE) {
                    deathRate = SERIOUSNESS_RATE;
                }
            } else {
                deathRate = DEATH_RATE;
            }
            r = (int) Math.round((double) in * (1.0 - deathRate));
            d = (int) Math.round((double) in * deathRate);
            recovered += r;
            dead += d;
            hospitalized -= (r + d);
            serious -= (int) Math.round((double) (r + d) * SERIOUSNESS_RATE);
            if (serious < 0) {
                serious = 0;
            }
            admissions.remove(0);
        }
        if (preInfectedOutside == infectedOutside && infectedOutside > 0) {
            infectedOutside--;
        }
        preInfectedOutside = infectedOutside;
        int admitted = (int) Math.round((double) infectedOutside / (double) AVG_SYMPTOM_DAYS);
        hospitalized += admitted;
        admissions.add(admitted);
        spreadingOutside -= admitted;
        infectedOutside -= admitted;
        serious += (int) Math.round((double) admitted * SERIOUSNESS_RATE);
        spreadingOutside = (int) Math.round((double) infectedOutside * ((double) AVG_SYMPTOM_DAYS - (double) AVG_INFECTION_DAYS) / (double) AVG_SYMPTOM_DAYS);
        infectedOutside += Math.round((double) spreadingOutside * SPREAD_FACTOR * ((double) (POPULATION - infectedOutside - hospitalized - recovered - dead) / (double) POPULATION));
    }

    private void printResults() {
        System.out.println("Infected Outside: " + infectedOutside);
        System.out.println("Spreading Outside: " + spreadingOutside);
        System.out.println("Hospitalized: " + hospitalized);
        System.out.println("Serious: " + serious);
        System.out.println("Recovered: " + recovered);
        System.out.println("Dead: " + dead);
    }

    public static void main(String[] args) {
        Simulation simulation = new Simulation();
        simulation.runSimulation();
    }
}
