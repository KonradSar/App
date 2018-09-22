package com.example.konrad.app;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.icu.text.StringPrepParseException;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import butterknife.ButterKnife;

import static com.example.konrad.app.GlobalListsAndVariablesForApp.globalDutiesList;

public class AddDuty extends AppCompatActivity {
    Button addBtn;
    EditText nameOfDuty;
    EditText idDuty;
    NewDuty dutyOne;

    public static String selectColorRandomly(){

        List<String> colors = new ArrayList<>();
        colors.add("Yellow");
        colors.add("Red");
        colors.add("Green");
        colors.add("Pink");
        colors.add("Blue");
        Random rand = new Random();
        return colors.get(rand.nextInt(colors.size()));
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static SimpleDateFormat getCurrentDate() {
        String dataFormat = "dd-MM-yyyy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dataFormat, Locale.GERMAN);
        return simpleDateFormat;

    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    public static String getDateInStringFormat(){
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        Date today = Calendar.getInstance().getTime();
        String reportDate = df.format(today);
        return reportDate;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_duty);
        ButterKnife.bind(this);
        addBtn = (Button) findViewById(R.id.addBtn);
        nameOfDuty = (EditText) findViewById(R.id.nameOfDutyEditText);
        idDuty = (EditText) findViewById(R.id.nameOfidDuty);


        addBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                if (!nameOfDuty.getText().toString().equals("") && !idDuty.getText().toString().equals("")){
                    String customColor = selectColorRandomly();
                    String currentDate = getDateInStringFormat();
                    globalDutiesList.add(new NewDuty(nameOfDuty.getText().toString(), globalDutiesList.size(), currentDate, 0, customColor, idDuty.getText().toString()));
                    sprawdzCzyJestDostep();
                    dutyOne = (new NewDuty(nameOfDuty.getText().toString(), globalDutiesList.size(), currentDate, 0, customColor, idDuty.getText().toString()));
                    SaveToExternalMEmory(dutyOne);
                    Intent intent = new Intent(AddDuty.this, MainActivity.class);
                    startActivity(intent);
                }else{
                    Toast.makeText(getApplicationContext(), "Please Fill in Each Line", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
    final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void sprawdzCzyJestDostep(){

        if(android.support.v4.app.ActivityCompat.checkSelfPermission(AddDuty.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){


            if (ActivityCompat.shouldShowRequestPermissionRationale(AddDuty.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)){

                showExplanation("Potrzebujemy pozwolenia", "Chcemy zapisać w pamięci zewnętrznej menu",
                        Manifest.permission.WRITE_EXTERNAL_STORAGE, MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
            }else{

                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
            }
        }
    }

    private void requestPermissions(String permissionName, int permissionRequestCode){
        ActivityCompat.requestPermissions(this, new String[]{permissionName} , permissionRequestCode);
    }


    private void showExplanation(String title, String message, final String permission, final int permissionRequestCode){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                requestPermissions(permission, permissionRequestCode);
            }
        });
        builder.show();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {
                // jesli uzytkownik dal anuluj to dlugosc listy bedzie pusta
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    SaveToExternalMEmory(dutyOne); // dostep przyznany - mozemy zrobic co chcemy
                    Log.d(GlobalListsAndVariablesForApp.LOG_TAG, "Dostęp przyznany");
                } else {
                    Log.d(GlobalListsAndVariablesForApp.LOG_TAG, "Dostęp nie przyznany");
                    //  dostep nie przyznany ! musimy obsluzyc ten problem w aplikacji
                    // ponizej dodatkowo sprawdzamy czy zaznaczyl never ask again
                    if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                        // user rejected the permission
                        boolean showRationale = ActivityCompat.shouldShowRequestPermissionRationale(AddDuty.this,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE);
                        if (!showRationale) {
                            Log.d(GlobalListsAndVariablesForApp.LOG_TAG, "Uzytkownik zaznaczyl never ask again");
                        }
                    }
                }
                return;
            }

            // za pomoca swticha mozna przejrzec czasmi wiele prosb
        }
    }
    public List<NewDuty> readFromExternalMemory(){
        File plik = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), NAZWA_PLIKU);
        StringBuilder sb = new StringBuilder();
        try {
            FileInputStream fis =  new FileInputStream(plik);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            sb = new StringBuilder();
            String line;
            while((line = br.readLine()) != null){
                sb.append(line);
            }
//            Toast.makeText(getApplicationContext(), R.string.OdczytaneZakupy, Toast.LENGTH_SHORT).show();
//            coordinator = (android.support.design.widget.CoordinatorLayout) findViewById(R.id.coordinator);
//            Snackbar.make(coordinator, "Odczytane", Snackbar.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "The Luck Of Duties", Toast.LENGTH_SHORT).show();
//            Snackbar.make(coordinator, "Wystąpił błąd odczytu", Snackbar.LENGTH_SHORT).show();
        }
        Type type = new TypeToken<List<NewDuty>>(){}.getType();

        List<NewDuty> odczytaneProdukty;

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        odczytaneProdukty = gson.fromJson(sb.toString(), type);


        return odczytaneProdukty;

    }
    final static String NAZWA_PLIKU = "DutyList.ks";
    public boolean czyPlikIstnieje(){
        File plik = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), NAZWA_PLIKU);
        boolean zwrotka = false;
        if(plik.exists()){
            zwrotka = true;
        }
        return zwrotka;
    }
    public void SaveToExternalMEmory(NewDuty duty) {
        if (czyPlikIstnieje() == false){

            List<NewDuty> myList1 = new ArrayList<>();
            myList1.add(duty);
            zapiszDoPamieci(myList1);

        }
        else{
            List<NewDuty> myList1 = readFromExternalMemory();

            myList1.add(dutyOne);
            zapiszDoPamieci(myList1);

        }
    }
    public void SaveToExternalMEmory2(NewDuty duty) {
        if (czyPlikIstnieje() == false){

            List<NewDuty> myList1 = new ArrayList<>();
            myList1.add(duty);
            zapiszDoPamieci(myList1);

        }
        else{
            List<NewDuty> myList1 = readFromExternalMemory();
            for(int i = 0; i < myList1.size(); i++){
                if(myList1.get(i).getLabelName().equals(duty.getLabelName())){
                    myList1.get(i).setLabelName(duty.getLabelName());
                }
            }

            myList1.add(dutyOne);
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
}
