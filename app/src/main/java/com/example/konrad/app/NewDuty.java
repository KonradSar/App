package com.example.konrad.app;

import android.graphics.Color;
import android.icu.text.SimpleDateFormat;

import java.text.DateFormat;

/**
 * Created by Konrad on 21.09.2018.
 */

public class NewDuty {

    // Pola opisujace kazdy nowy obiekt typu NewDuty

    public String labelName;
    public int numberOfDuty;
    public String dateOfAddingDuty;
    public int numberOfViews;
    public String randomColor;
    public String idDuty;

    // Ponizej przeciazamy konstrutkor dwukrotnie w celu zapewnienia swobodnego tworzenia nowego obiektu na dwa sposoby

    public NewDuty(String labelName, int numberOfDuty, String dateOfAddingDuty, int numberOfViews, String randomColor, String idDuty) {
        this.labelName = labelName;
        this.numberOfDuty = numberOfDuty;
        this.dateOfAddingDuty = dateOfAddingDuty;
        this.numberOfViews = numberOfViews;
        this.randomColor = randomColor;
        this.idDuty = idDuty;
    }

    public NewDuty(String labelName, int numberOfDuty) {
        this.labelName = labelName;
        this.numberOfDuty = numberOfDuty;
    }

    // Ponizej oczywiscie gettery i settery

    public String getLabelName() {
        return labelName;
    }

    public void setLabelName(String labelName) {
        this.labelName = labelName;
    }

    public int getNumberOfDuty() {
        return numberOfDuty;
    }

    public void setNumberOfDuty(int numberOfDuty) {
        this.numberOfDuty = numberOfDuty;
    }

    public String getDateOfAddingDuty() {
        return dateOfAddingDuty;
    }

    public void setDateOfAddingDuty(String dateOfAddingDuty) {
        this.dateOfAddingDuty = dateOfAddingDuty;
    }

    public int getNumberOfViews() {
        return numberOfViews;
    }

    public void setNumberOfViews(int numberOfViews) {
        this.numberOfViews = numberOfViews;
    }

    public String getRandomColor() {
        return randomColor;
    }

    public void setRandomColor(String randomColor) {
        this.randomColor = randomColor;
    }

    public String getIdDuty() {
        return idDuty;
    }

    public void setIdDuty(String idDuty) {
        this.idDuty = idDuty;
    }
}
