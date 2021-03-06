package com.example.konrad.app;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;

import static com.example.konrad.app.GlobalListsAndVariablesForApp.deletingCondition;
import static com.example.konrad.app.GlobalListsAndVariablesForApp.globalDutiesList;
import static com.example.konrad.app.GlobalListsAndVariablesForApp.removedElementPosition;

public class MainActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    TextView dutyLabel;

    // Nazwa pliku zapisywana w ExternalMEmory jako lista DutyList tworzona w aplikacji

    final static String NAZWA_PLIKU = "DutyList.ks";
    MediaPlayer mySeventhSound;
    private Context context1;

    ImageButton deleteButton;
    FloatingActionButton fab;
    private static MediaPlayer mySound;
    RecyclerViewAdapter recyclerViewAdapter;
    static String NUMBEROFVIEWS = "NUMBEROFVIEWS";

    // Metoda uruchomienia nowego activity za pomoca intenta

    private void GoToNewActivity() {
        Intent intent = new Intent(MainActivity.this, AddDuty.class);
        startActivity(intent);
    }

    // Ponizej metoda obslugujaca wyjscie z aplikacji do Android

    public void onBackPressed() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
        alertDialog.setTitle("Do you want to quit app?");
        alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finishAffinity();

            }
        });

        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alertDialog.create();
        alertDialog.show();
        return;
    }


    // Ponizej w dwoch metodach obsluga obrotu ekranu gdzie jako parametr przekazuje liste typu Integer z wartosciami odpowiadajacymi ilosci odslon kazdego elementu listy DutyList
    // Indeks kazdego elementu listy to jednoczesnie indeksy kolejnych pozycji na liscie DutyList

    @Override
    protected void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);

        // Laduje do listy ilosci odslon dla kazdego Duty z osobna i zapisuje jako Klucz, Wartosc w outState i przekazuje dalej do onRestore

        ArrayList<Integer> list = new ArrayList<>();
        for(int i = 0; i < GlobalListsAndVariablesForApp.globalDutiesList.size(); i++){
            list.add(i, GlobalListsAndVariablesForApp.globalDutiesList.get(i).getNumberOfViews());
        }
        outState.putIntegerArrayList(NUMBEROFVIEWS, list);
    }
    @Override

    // Pobieram zapisana liste z o rozmiarze listy obowiazkow i przypisuje wartosci int do starej listy w generalmethods z ktorej
    // adapter poniera dane do wyswietlenia
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // Ponizej przypisuje do Listy typu Integer zapisana powyzej liste integerow. Iterujac po elementach listy z kazdego z nich wyciagam wartosc int i przypisuje do
        // odpowiednio takiego samego indeksu w liscie DutyList i ustawiam setterem wartosc ilosci odslon przekazana w liscie przenosnej stworzonej w linijce 97-99

        GlobalListsAndVariablesForApp.numberOfViewsFromOnSavedInstanceState = savedInstanceState.getIntegerArrayList(NUMBEROFVIEWS);
        for(int i = 0; i < GlobalListsAndVariablesForApp.numberOfViewsFromOnSavedInstanceState.size(); i++){
            GlobalListsAndVariablesForApp.globalDutiesList.get(i).setNumberOfViews(GlobalListsAndVariablesForApp.numberOfViewsFromOnSavedInstanceState.get(i));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        recyclerView = (RecyclerView) findViewById(R.id.mainRecycler);
        dutyLabel = (TextView) findViewById(R.id.dutyLabel);
        mySeventhSound = MediaPlayer.create(this, R.raw.error);
        // Utwór w formacie mp3 o nazwie Computer Error na licencji Attribution 3.0 pobrany dnia 04.09.2017 godz. 20:00 ze strony https://soundbible.com/
        deleteButton = (ImageButton) findViewById(R.id.deletebtn);
        fab = (FloatingActionButton) findViewById(R.id.floatingbtn);
        mySound = MediaPlayer.create(this, R.raw.klikanie);
        // Utwór w formacie mp3 o nazwie Click On na licencji Attribution 3.0 pobrany dnia 04.09.2017 godz. 20:00 ze strony https://soundbible.com/

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerViewAdapter = new RecyclerViewAdapter(globalDutiesList, MainActivity.this);
        recyclerView.setAdapter(recyclerViewAdapter);

        adjustDataForAdapter();

        File plik = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), NAZWA_PLIKU);
        if (plik.exists()) {
            adjustDataForAdapter();
            Toast.makeText(getApplicationContext(), "Downloaded From External Memory", Toast.LENGTH_SHORT).show();
        } else {
            mySeventhSound.start();
            Toast.makeText(getApplicationContext(), "No Data Saved In Memory", Toast.LENGTH_SHORT).show();

            // Alert dialog informujacy o braku elementow na liscie ponizej

            AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
            alertDialog.setTitle(R.string.AdditionalInfoAlert);
            alertDialog.setMessage(R.string.would_like_to_add);
            alertDialog.setPositiveButton(R.string.Okey, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mySound.start();
                    GoToNewActivity();

                }
            });

            alertDialog.setNegativeButton(R.string.Deny, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mySound.start();
                    dialog.dismiss();
                }
            });
            alertDialog.create();
            alertDialog.show();

        }
        // On Click dla FAB, ktory dodaje kolejne elementy do listy
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mySound.start();
                Toast.makeText(getApplicationContext(), R.string.NewProductAddingNow, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, AddDuty.class);
                startActivity(intent);
            }
        });
    }
    // Ponizej metody zapisujace i odczytujace dane z ExternalMemory w formacie JSON i konwertujace je do wyswietlenia w postaci listy typu NewDuty

    public List<NewDuty> readFromExternalMemory() {
        File plik = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), NAZWA_PLIKU);
        StringBuilder sb = new StringBuilder();
        try {
            FileInputStream fis = new FileInputStream(plik);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
//            Toast.makeText(getApplicationContext(), R.string.Error, Toast.LENGTH_SHORT).show();
        }
        Type type = new TypeToken<List<NewDuty>>() {
        }.getType();

        List<NewDuty> odczytaneProdukty;

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        odczytaneProdukty = gson.fromJson(sb.toString(), type);


        return odczytaneProdukty;

    }

    public void SaveToExternalMEmory(NewDuty duty) {
        if (czyPlikIstnieje() == false){

            List<NewDuty> myList1 = new ArrayList<>();
            myList1.add(duty);
            zapiszDoPamieci(myList1);

        }
        else{
            List<NewDuty> myList1 = readFromExternalMemory();


            myList1.get(GlobalListsAndVariablesForApp.position).setNumberOfViews(GlobalListsAndVariablesForApp.numberOfViewsForSelectedDuty);
            zapiszDoPamieci(myList1);

        }
    }
    public boolean czyPlikIstnieje(){
        File plik = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), NAZWA_PLIKU);
        boolean zwrotka = false;
        if(plik.exists()){
            zwrotka = true;
        }
        return zwrotka;
    }
    public void updateAndSaveDutiesListToExternalMemory() {
        if (czyPlikIstnieje()){
            List<NewDuty> myList1 = GlobalListsAndVariablesForApp.globalDutiesList;
            zapiszDoPamieci(myList1);
        }
    }

    public void zapiszDoPamieci(List<NewDuty> dutyList) {
        String lokalizacjFolderu = Environment.getExternalStorageDirectory().toString();
        File file = new File(lokalizacjFolderu, NAZWA_PLIKU);
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        String jsonDutiesList = gson.toJson(dutyList);

        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);

            fileOutputStream.write(jsonDutiesList.getBytes());
            fileOutputStream.close();
//            Toast.makeText(getApplicationContext(), "Zakup zapisany do External Memory", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            e.printStackTrace();
//            Toast.makeText(getApplicationContext(), "Błąd zapisu do External Memory", Toast.LENGTH_SHORT).show();

        }
    }

    public void adjustDataForAdapter() {
        GlobalListsAndVariablesForApp.globalDutiesList = readFromExternalMemory();
        recyclerViewAdapter = new RecyclerViewAdapter(globalDutiesList, MainActivity.this);
        recyclerView.setAdapter(recyclerViewAdapter);

    }
}