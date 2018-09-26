package com.example.konrad.app;

import android.content.Context;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.media.MediaPlayer;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


import static com.example.konrad.app.GlobalListsAndVariablesForApp.globalDutiesList;

import static com.example.konrad.app.GlobalListsAndVariablesForApp.removedElementPosition;
import static com.example.konrad.app.GlobalListsAndVariablesForApp.updatedHoursCounted;

/**
 * Created by Konrad on 21.09.2018.
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder>{

    private Context context1;
    private List<NewDuty> dutiesList;
    int THRESHOLD = 26;
    int acceptedNumber = 999;
    private int position;
    private static  MediaPlayer mySound;

    public int getPosition() {

        return position;
    }

    public int setPosition(int position) {
        this.position = position;
        return position;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.singleline, parent, false);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context1, GlobalListsAndVariablesForApp.position, Toast.LENGTH_LONG).show();

            }
        });
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final RecyclerViewAdapter.MyViewHolder holder, final int position) {

        final NewDuty singleDuty = dutiesList.get(position);

        // Zabezpieczenie przed zbyt dlugim ciagiem znakow jako nazwa wydarzenia na liscie
        holder.dutyName.setText((singleDuty.labelName.length() > THRESHOLD) ? singleDuty.labelName.substring(0, THRESHOLD) + ".." : singleDuty.labelName);

        // OnnClick w przypadku nacisku na pojedynczy element listy
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                // Zaciagam date dodania wybranego elementu z listy obowiazkow metoda getDateOfAddingDuty i przypisuje do zmiennej date typu String
                String date = singleDuty.getDateOfAddingDuty();

                // Zaciagam rowniez aktualna date i przypisuje do zmiennej typu String o nazwie reportDate
                DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

                Date today = Calendar.getInstance().getTime();
                String reportDate = df.format(today);

                // Przypisujemy dzisiejszy dzien/miesiac/rok do zmiennych
                Integer monthToday = Integer.valueOf(reportDate.substring(0,2));
                Integer dayToday = Integer.valueOf(reportDate.substring(3,5));
                Integer yearToday = Integer.valueOf(reportDate.substring(6,10));
                // Przypisujemy dzisiejsze godziny/minuty/sekundy do zmiennych
                Integer hourToday = Integer.valueOf(reportDate.substring(11,13));
                Integer minuteToday = Integer.valueOf(reportDate.substring(14,16));
                Integer secondToday = Integer.valueOf(reportDate.substring(17,19));

                // Przypisujemy daty dodania dzien/miesiac/rok do zmiennych
                Integer monthAgo = Integer.valueOf(date.substring(0,2));
                Integer dayAgo = Integer.valueOf(date.substring(3,5));
                Integer yearAgo = Integer.valueOf(date.substring(6,10));

                // Przypisujemy daty dodania godziny/minuty/sekundy do zmiennych
                Integer hourAgo = Integer.valueOf(date.substring(11,13));
                Integer minuteAgo = Integer.valueOf(date.substring(14,16));
                Integer secondAgo = Integer.valueOf(date.substring(17,19));
                // Stala do dzielenia dni, ktore uplynely od dodania wydarzenia. Oznacza usredniona liczbe dni w miesiacu i otrzymano ja przez podzielenie 365 dni roku przez 12 miesiecy
                double daysCounter = 30.41666666667;
                // Ponizej roznica lat
                Integer yearDifference = yearToday-yearAgo;
                Integer monthDifference = monthToday-monthAgo;
                Integer dayDifference = dayToday-dayAgo;
                Integer monthsPassed = yearDifference + monthDifference;
                double daysPassed = (monthsPassed*daysCounter)+dayDifference;
                // Ponizej metoda Math zaokraglam do calosci otrzymany wynik np 0.9 daje 0 a 2.0 daje 2
                long roundedDaysPassedValue = Math.round(daysPassed);
                double valueToCompare = roundedDaysPassedValue/daysCounter;

                if(roundedDaysPassedValue % daysCounter != 0){
                    long valueToCompareRounded = Math.round(valueToCompare);
                    double theRestOfMonthsToAdd = valueToCompare - valueToCompareRounded;
                    double theRestOfDaysToAddFromMonthsValue = theRestOfMonthsToAdd * daysCounter;
                    double daysToAddFinalValue = (double)Math.round(theRestOfDaysToAddFromMonthsValue * 100) / 100;
                    // Ponizej liczba miesiecy i dni do wyswietlenia
                    double daysValue = daysToAddFinalValue;
                    GlobalListsAndVariablesForApp.daysForSecondPartOfIfElse = daysValue;
                    double monthsValue = valueToCompareRounded;
                    if(yearDifference.equals(0) && monthDifference.equals(0) && dayDifference.equals(0)){
                        // Obliczanie godzin i minut gdy nie ma roznicy godzin
                        if(hourToday.equals(hourAgo)){
                            GlobalListsAndVariablesForApp.years = 0;
                            GlobalListsAndVariablesForApp.months = 0;
                            GlobalListsAndVariablesForApp.days = 0;
                            GlobalListsAndVariablesForApp.hours = 0;
                            GlobalListsAndVariablesForApp.minutes = minuteAgo-minuteToday;
                        }else{
                            // Obliczanie godzin i minut gdy jest roznica godzin
                            Integer hours = hourToday-hourAgo;
                            Integer hoursParsedToMinutes = hours*60;
                            Integer minutes = minuteToday - minuteAgo;
                            Integer totalMinutesCount = hoursParsedToMinutes + minutes;
                            Integer hoursFinalCount = totalMinutesCount/60;
                            if(totalMinutesCount % 60 != 0){
                                // Zaokraglamy do calosci w linijce ponizej
                                Integer valueRoundedOfHours = (int)Math.round(hoursFinalCount * 100) / 100;
                                Integer valueOfTheRestOfHours = hoursFinalCount - valueRoundedOfHours;
                                Integer valueOfMinutesToAddFromTheRestOfHours = valueOfTheRestOfHours*60;
                                GlobalListsAndVariablesForApp.years = 0;
                                GlobalListsAndVariablesForApp.months = 0;
                                GlobalListsAndVariablesForApp.days = 0;
                                GlobalListsAndVariablesForApp.hours = valueRoundedOfHours;
                                GlobalListsAndVariablesForApp.minutes = valueOfMinutesToAddFromTheRestOfHours;
                            }else{
                                GlobalListsAndVariablesForApp.years = 0;
                                GlobalListsAndVariablesForApp.months = 0;
                                GlobalListsAndVariablesForApp.days = 0;
                                GlobalListsAndVariablesForApp.hours = hoursFinalCount;
                                GlobalListsAndVariablesForApp.minutes = 0;
                            }

                        }

                    }else{
                        // Wyliczanie godzin, minut gdy istnieje roznica dni miedzy dwoma datami
                        Integer hoursToCount = hourAgo+1;
                        Integer minutesToCount = minuteAgo;
                        Integer hoursCountAgo = 24-hoursToCount;
                        Integer minutesCountAgo = 60 - minutesToCount;
                        Integer totalHoursCounted = hourToday + hoursCountAgo;
                        Integer totalMinutesCounted = minuteToday + minutesCountAgo;

                        GlobalListsAndVariablesForApp.years = yearDifference;
                        GlobalListsAndVariablesForApp.months = monthsValue;
                        GlobalListsAndVariablesForApp.days = daysValue;
                        GlobalListsAndVariablesForApp.hours = totalHoursCounted;
                        GlobalListsAndVariablesForApp.minutes = totalMinutesCounted;


                        if(totalMinutesCounted > 59) {
                            //Odejmujemy jedna godzine i mamy zakres minut do sumowania
                            Integer totalMinutesCountedMinusHour = totalMinutesCounted - 60;
                            //Ponizej zakres godzin do dodania
                            Integer updatedHoursCounted = totalHoursCounted + 1;
                            // Sprawdzamy czy po dodaniu godziny nie zwiekszyl sie ponad 24 zakres godzin
                            GlobalListsAndVariablesForApp.years = yearDifference;
                            GlobalListsAndVariablesForApp.months = monthsValue;
                            GlobalListsAndVariablesForApp.days = daysValue;
                            GlobalListsAndVariablesForApp.hours = updatedHoursCounted;
                            GlobalListsAndVariablesForApp.minutes = totalMinutesCountedMinusHour;
                        }

                        if(totalHoursCounted > 23){
                                Integer hoursCountUpdated = GlobalListsAndVariablesForApp.hours -24;
                                double daysCount = daysValue+1;
                                GlobalListsAndVariablesForApp.years = yearDifference;
                                GlobalListsAndVariablesForApp.months = monthsValue;
                                GlobalListsAndVariablesForApp.days = daysCount;
                                GlobalListsAndVariablesForApp.hours = hoursCountUpdated;
                                GlobalListsAndVariablesForApp.minutes = totalMinutesCounted;

                            }
                    }

                }else{
                    double monthsValue = valueToCompare;
                    if(yearDifference.equals(0) && monthDifference.equals(0) && dayDifference.equals(0)){
                           // Obliczanie godzin i minut gdy nie ma roznicy godzin
                        if(hourToday.equals(hourAgo)){
                            GlobalListsAndVariablesForApp.years = 0;
                            GlobalListsAndVariablesForApp.months = 0;
                            GlobalListsAndVariablesForApp.days = 0;
                            GlobalListsAndVariablesForApp.hours = 0;
                            GlobalListsAndVariablesForApp.minutes = minuteAgo-minuteToday;
                        }else{
                            // Obliczanie godzin i minut gdy jest roznica godzin
                            Integer hours = hourToday-hourAgo;
                            Integer hoursParsedToMinutes = hours*60;
                            Integer minutes = minuteToday - minuteAgo;
                            Integer totalMinutesCount = hoursParsedToMinutes + minutes;
                            Integer hoursFinalCount = totalMinutesCount/60;
                            if(totalMinutesCount % 60 != 0){
                                // Zaokraglamy do calosci w linijce ponizej
                                Integer valueRoundedOfHours = (int)Math.round(hoursFinalCount * 100) / 100;
                                Integer valueOfTheRestOfHours = hoursFinalCount - valueRoundedOfHours;
                                Integer valueOfMinutesToAddFromTheRestOfHours = valueOfTheRestOfHours*60;
                                GlobalListsAndVariablesForApp.years = 0;
                                GlobalListsAndVariablesForApp.months = 0;
                                GlobalListsAndVariablesForApp.days = 0;
                                GlobalListsAndVariablesForApp.hours = valueRoundedOfHours;
                                GlobalListsAndVariablesForApp.minutes = valueOfMinutesToAddFromTheRestOfHours;
                            }else{
                                GlobalListsAndVariablesForApp.years = 0;
                                GlobalListsAndVariablesForApp.months = 0;
                                GlobalListsAndVariablesForApp.days = 0;
                                GlobalListsAndVariablesForApp.hours = hoursFinalCount;
                                GlobalListsAndVariablesForApp.minutes = 0;
                            }

                        }
                    }else{
                        // Wyliczanie godzin, minut gdy istnieje roznica dni miedzy dwoma datami, nie mamy dni bo dzielenie dni przez
                        //miesiace nie daje reszty wiec sa tylko lata,miesiace,godziny,minuty
                        Integer hoursToCount = hourAgo+1;
                        Integer minutesToCount = minuteAgo;
                        Integer hoursCountAgo = 24-hoursToCount;
                        Integer minutesCountAgo = 60 - minutesToCount;
                        Integer totalHoursCounted = hourToday + hoursCountAgo;
                        Integer totalMinutesCounted = minuteToday + minutesCountAgo;

                        GlobalListsAndVariablesForApp.years = yearDifference;
                        GlobalListsAndVariablesForApp.months = monthsValue;
                        GlobalListsAndVariablesForApp.days = GlobalListsAndVariablesForApp.daysForSecondPartOfIfElse;
                        GlobalListsAndVariablesForApp.hours = totalHoursCounted;
                        GlobalListsAndVariablesForApp.minutes = totalMinutesCounted;

                        if(totalMinutesCounted > 59) {
                            //Odejmujemy jedna godzine i mamy zakres minut do sumowania
                            Integer totalMinutesCountedMinusHour = totalMinutesCounted - 60;
                            //Ponizej zakres godzin do dodania
                            Integer updatedHoursCounted = totalHoursCounted + 1;
                            GlobalListsAndVariablesForApp.years = yearDifference;
                            GlobalListsAndVariablesForApp.months = monthsValue;
                            GlobalListsAndVariablesForApp.days = GlobalListsAndVariablesForApp.daysForSecondPartOfIfElse;
                            GlobalListsAndVariablesForApp.hours = updatedHoursCounted;
                            GlobalListsAndVariablesForApp.minutes = totalMinutesCountedMinusHour;
                        }
                        if(totalHoursCounted > 23){
                                Integer hoursCountUpdated = totalHoursCounted -24;
                                double daysCount = GlobalListsAndVariablesForApp.daysForSecondPartOfIfElse+1;
                                GlobalListsAndVariablesForApp.years = yearDifference;
                                GlobalListsAndVariablesForApp.months = monthsValue;
                                GlobalListsAndVariablesForApp.days = daysCount;
                                GlobalListsAndVariablesForApp.hours = hoursCountUpdated;
                                GlobalListsAndVariablesForApp.minutes = totalMinutesCounted;
                        }

//                        GlobalListsAndVariablesForApp.years = yearDifference;
//                        GlobalListsAndVariablesForApp.months = monthsValue;
//                        GlobalListsAndVariablesForApp.days = GlobalListsAndVariablesForApp.daysForSecondPartOfIfElse;
//                        GlobalListsAndVariablesForApp.hours = totalHoursCounted;
//                        GlobalListsAndVariablesForApp.minutes = totalMinutesCounted;
                    }
                }

                mySound = MediaPlayer.create(context1, R.raw.klikanie);
                // Utwór w formacie mp3 o nazwie Click On na licencji Attribution 3.0 pobrany dnia 04.09.2017 godz. 20:00 ze strony https://soundbible.com/
                mySound.start();
                GlobalListsAndVariablesForApp.position = setPosition(holder.getAdapterPosition());
                // Ponizej tekst Stringa wyswietlany jako pozycja kliknieta na liscie

                String toastMessageSelectedPositionNumber = String.valueOf(GlobalListsAndVariablesForApp.position);
                // inkrementuje ilosc odslon ponizej

                GlobalListsAndVariablesForApp.globalDutiesList.get(position).setNumberOfViews(GlobalListsAndVariablesForApp.globalDutiesList.get(position).getNumberOfViews()+1);
                // pobieram do zmiennej globalnej zwiekszona ilosc odslon w celu przekazania do metody uaktualniajacej ilosc odslon ponizej

                GlobalListsAndVariablesForApp.numberOfViewsForSelectedDuty = singleDuty.getNumberOfViews();
                // Ponizej odwoluje sie do metody z klasy main za pomoca adaptera WOW!

                if(context1 instanceof MainActivity){
                    ((MainActivity)context1).SaveToExternalMEmory(singleDuty);
                }
                // Ponizej tworze lineral layout w ktorym wyswietle dane szczegolowe na temat wybranego Duty

                LinearLayout linearLayout = new LinearLayout(context1);
                linearLayout.setOrientation(LinearLayout.HORIZONTAL);
                ImageView imageView = new ImageView(context1);
                int width = 600;
                int height = 600;
                LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams(width,height, Gravity.CENTER_HORIZONTAL);
                parms.bottomMargin = 15;
                parms.topMargin = 30;
                parms.leftMargin = 10;
                parms.rightMargin = 10;
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(context1);
                alertDialog.setView(linearLayout);
                String nameOfDuty = singleDuty.getLabelName();
                String dutyId = singleDuty.getIdDuty();
                String numberOfViews = String.valueOf(singleDuty.getNumberOfViews());
                long valueOfPassedDays = Math.round(GlobalListsAndVariablesForApp.days);
                long valueOfPassedMonths = Math.round(GlobalListsAndVariablesForApp.months);

                // Ponizej przypisanie dialogowi wiadomosci do wyswietlenia za pomoca zmiennych globalnych uaktualnianych na biezaco
                alertDialog.setTitle("Selected Duty Position "+toastMessageSelectedPositionNumber);
                alertDialog.setMessage("Description below:"+"\n"+"Duty Name: "+nameOfDuty+"\n"+"Duty Id: "+dutyId+"\n"+
                        "Added on: "+singleDuty.getDateOfAddingDuty()+"\n"+"Counter:"+"\n"+"Years Passed: "+GlobalListsAndVariablesForApp.years+"\n"+"Months Passed: "+valueOfPassedMonths+"\n"+
                        "Days Passed: "+valueOfPassedDays+"\n"+"Hours Passed: "+GlobalListsAndVariablesForApp.hours+"\n"+"Minutes Passed: "+
                        GlobalListsAndVariablesForApp.minutes+"\n"+"Views: " + singleDuty.getNumberOfViews());

                alertDialog.create();
                alertDialog.show();


            }
        });
        holder.deleteButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                // Ponizej akcja na okazje klikniecia image buttona w postaci smietnika, ktory usuwa wybrana pozycje z listy
                mySound = MediaPlayer.create(context1, R.raw.emptybin);
                // Utwór  w formacie mp3 o nazwie A Tone na licencji Public Domain pobrany dnia 04.09.2017 godz. 20:00 ze strony https://soundbible.com/
                mySound.start();
                globalDutiesList.remove(position);
                removedElementPosition = position;
                GlobalListsAndVariablesForApp.deletingCondition = true;
                notifyItemRemoved(position);
                // Odwolanie do metody z klasy main z poziomu adaptera WOW!!! Wcześniej takiego sposobu nie wykorzystano u mnie!!!!
                notifyDataSetChanged();
                if(context1 instanceof MainActivity){
                    // Po usunieciu elementu z listy zapisujemy ja do ExternalMemory pobierajac ja w kolejnej metodzie 
                    ((MainActivity)context1).updateAndSaveDutiesListToExternalMemory();
                }
            }
        });

        // W zaleznosci od przypisanej wartosci typu String oznaczajacego kolor do kazdego elementu listy na biezaco potczas tworzenia widoku listy dobierane jest tlo kazdego elementu
        if (singleDuty.getRandomColor().equals("Red")){
            holder.relativeLay.setBackgroundResource(R.drawable.red);
        }else if(singleDuty.getRandomColor().equals("Green")){
            holder.relativeLay.setBackgroundResource(R.drawable.green);
        }else if(singleDuty.getRandomColor().equals("Yellow")){
            holder.relativeLay.setBackgroundResource(R.drawable.yellow);
        }else if(singleDuty.getRandomColor().equals("Pink")){
            holder.relativeLay.setBackgroundResource(R.drawable.pink);
        }else{
            holder.relativeLay.setBackgroundResource(R.drawable.blue);
        }
    }


    @Override
    public int getItemCount() {
        return dutiesList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public ImageButton deleteButton;
        public TextView dutyName;

        public RelativeLayout relativeLay;

        public MyViewHolder(View itemView) {
            super(itemView);
            deleteButton = (ImageButton) itemView.findViewById(R.id.deletebtn);
            dutyName = (TextView) itemView.findViewById(R.id.dutyLabel);

            relativeLay = (RelativeLayout) itemView.findViewById(R.id.singleLineLay);

        }

        @Override
        public void onClick(View v) {

        }
    }
    public RecyclerViewAdapter(List<NewDuty> newDutiesList, Context context){
        this.dutiesList = newDutiesList;
        this.context1 = context;


    }
//    public void updateIndexes(){
//        List<NewDuty> listOfDuties = new ArrayList<>();
//        listOfDuties = globalDutiesList;
//        for(int i = 0; i < listOfDuties.size(); i++){
//            if(listOfDuties.get(i).getNumberOfDuty() > removedElementPosition){
//                listOfDuties.get(i).setNumberOfDuty(listOfDuties.get(i).getNumberOfDuty()-1);
//            }
//        }
//    }

    }

