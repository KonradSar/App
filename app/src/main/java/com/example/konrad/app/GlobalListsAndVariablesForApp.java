package com.example.konrad.app;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Konrad on 21.09.2018.
 */

public class GlobalListsAndVariablesForApp {
    // Zmienne globalne wykorzystywane w aplikacji do aktualizacji i przechowywania danych o elementach listy
    public static List<NewDuty> globalDutiesList = new ArrayList<>();
    public static int position;
    public static int removedElementPosition;
    public static final String LOG_TAG = "KonradApp";
    public static boolean deletingCondition = false;
    public static Integer years = 0;
    public static double days = 0;
    public static double months = 0;
    public static Integer hours = 0;
    public static Integer minutes = 0;
    public static double daysForSecondPartOfIfElse = 0;
    public static Integer updatedHoursCounted = 0;
    public static Integer numberOfViewsForSelectedDuty = 0;
    public static ArrayList<Integer> numberOfViewsFromOnSavedInstanceState = new ArrayList<>();






















}
