package com.example.konrad.app;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
    final static String NAZWA_PLIKU = "DutyList.ks";
    MediaPlayer mySeventhSound;
    private Context context1;

    ImageButton deleteButton;
    FloatingActionButton fab;
    private static MediaPlayer mySound;
    RecyclerViewAdapter recyclerViewAdapter;



    private void GoToNewActivity() {
        Intent intent = new Intent(MainActivity.this, AddDuty.class);
        startActivity(intent);
    }

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


    public boolean czyPlikIstnieje(){
        File plik = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), NAZWA_PLIKU);
        boolean zwrotka = false;
        if(plik.exists()){
            zwrotka = true;
        }
        return zwrotka;
    }
    public void removeFromExternalMemoryAndUpdate() {
        if (czyPlikIstnieje()){
            List<NewDuty> myList1 = readFromExternalMemory();
            myList1.remove(removedElementPosition);
            zapiszDoPamieci(myList1);
            String bb = "fw";
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

    public void checkIfDeletingElementWasClickedInRecyclerView(){
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    while(deletingCondition) {
                        sleep(10);
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                removeFromExternalMemoryAndUpdate();
                                deletingCondition = false;
                            }
                        });

                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        thread.start();
    }
    public void adjustDataForAdapter() {
        GlobalListsAndVariablesForApp.globalDutiesList = readFromExternalMemory();
        recyclerViewAdapter = new RecyclerViewAdapter(globalDutiesList, MainActivity.this);
        recyclerView.setAdapter(recyclerViewAdapter);

    }





}
