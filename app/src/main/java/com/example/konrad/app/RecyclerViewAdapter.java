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


        holder.dutyName.setText((singleDuty.labelName.length() > THRESHOLD) ? singleDuty.labelName.substring(0, THRESHOLD) + ".." : singleDuty.labelName);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                mySound = MediaPlayer.create(context1, R.raw.klikanie);
                // Utwór w formacie mp3 o nazwie Click On na licencji Attribution 3.0 pobrany dnia 04.09.2017 godz. 20:00 ze strony https://soundbible.com/
                mySound.start();
                GlobalListsAndVariablesForApp.position = setPosition(holder.getAdapterPosition());
                String toastMessageSelectedPositionNumber = String.valueOf(GlobalListsAndVariablesForApp.position);
                GlobalListsAndVariablesForApp.globalDutiesList.get(position).setNumberOfViews(GlobalListsAndVariablesForApp.globalDutiesList.get(position).getNumberOfViews()+1);
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

                alertDialog.setTitle("Selected Duty Position "+toastMessageSelectedPositionNumber);
                alertDialog.setMessage("Description below:"+"\n"+"Duty Name: "+nameOfDuty+"\n"+"Duty Id: "+dutyId+"\n"
                +"\n"+"Counter: XX");
                alertDialog.create();
                alertDialog.show();

                String date = singleDuty.getDateOfAddingDuty();
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

                // Sumujemy i wyliczamy zmienne
                if(!yearToday.equals(yearAgo)){
                    Integer yearDifference = yearToday - yearAgo;
                }
                if(!monthToday.equals(monthAgo)){
                    Integer monthDifference = monthToday - monthAgo;
                }
                if(!dayToday.equals(dayAgo)){
                    Integer dayDifference = dayToday - dayAgo;
                }
                if(!hourToday.equals(hourAgo)){
                    Integer hourDifference = hourToday - hourAgo;
                }
                if(!minuteToday.equals(minuteAgo)){
                    Integer minuteDifference = minuteToday - minuteAgo;
                }
                if(!secondToday.equals(secondAgo)){
                    Integer secondDifference = secondToday - secondAgo;
                }
                if(GlobalListsAndVariablesForApp.years != 0){


                }else if(GlobalListsAndVariablesForApp.months != 0){

                }else if(GlobalListsAndVariablesForApp.days != 0){

                }






            }
        });
        holder.deleteButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                mySound = MediaPlayer.create(context1, R.raw.emptybin);
                // Utwór  w formacie mp3 o nazwie A Tone na licencji Public Domain pobrany dnia 04.09.2017 godz. 20:00 ze strony https://soundbible.com/
                mySound.start();
                globalDutiesList.remove(position);
                removedElementPosition = position;
                GlobalListsAndVariablesForApp.deletingCondition = true;
                notifyItemRemoved(position);
//                updateIndexes();
                notifyDataSetChanged();
                if(context1 instanceof MainActivity){
                    ((MainActivity)context1).checkIfDeletingElementWasClickedInRecyclerView();
                }
            }
        });
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
    public void updateIndexes(){
        List<NewDuty> listOfDuties = new ArrayList<>();
        listOfDuties = globalDutiesList;
        for(int i = 0; i < listOfDuties.size(); i++){
            if(listOfDuties.get(i).getNumberOfDuty() > removedElementPosition){
                listOfDuties.get(i).setNumberOfDuty(listOfDuties.get(i).getNumberOfDuty()-1);
            }
        }
    }

    }

