package es.carlossc212.consultaagenda_archivos.view;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import es.carlossc212.consultaagenda_archivos.R;
import es.carlossc212.consultaagenda_archivos.viewmodel.ContactsViewModel;

public class MainActivity extends AppCompatActivity {

    private final static int PERMISSION_CONTACTS = 1;

    private Button btSearch;
    private EditText etPhone;
    private TextView tvResult, tvUltimaBusqueda;

    //Repository rep;

    private SharedPreferences sh;
    private SharedPreferences.Editor editor;
    private int modoBusqueda;
    private int guardarBusqueda;
    private boolean busquedaPrincipio;
    private boolean guardarUltimaBusqueda;
    private String ultimaBusqueda = "";
    private ContactsViewModel cvm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //rep = new Repository();
        initialize();
    }

    /*@Override
    protected void onResume() {
        super.onResume();
        modoBusqueda = sh.getInt("formaBusqueda", 0);
        guardarBusqueda = sh.getInt("guardarBusqueda", 1);
    }*/

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){
            case PERMISSION_CONTACTS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults.length >0){
                    //permiso
                    //rep.search(getContentResolver(), ETPhone.getText().toString());
                    tvResult.setText(cvm.search(etPhone.getText().toString()));
                }else{
                    //sin permiso
                }
                break;
        }
    }



    //Mensaje de explicacion de los permisos
    private void explain() {
        showRationaleDialog("Permisos requeridos", "Para que esta app funcione se necesita acceder a los contactos");
    }
    //Se inicializan los componentes
    private void initialize() {

        btSearch = findViewById(R.id.BtSearch);
        etPhone = findViewById(R.id.ETPhone);
        tvResult = findViewById(R.id.tvResult);
        tvUltimaBusqueda = findViewById(R.id.tvUltimaBusqueda);
        cvm = new ContactsViewModel(getApplication());

        tvUltimaBusqueda.setOnClickListener(v->{
            etPhone.setText(tvUltimaBusqueda.getText().toString());
        });

        etPhone.setOnFocusChangeListener((v, hasFocus) -> {
            if (guardarUltimaBusqueda) {
                if (hasFocus) {
                    tvUltimaBusqueda.setVisibility(View.VISIBLE);
                    tvUltimaBusqueda.setText(sh.getString("ultimaBusqueda", ""));
                } else {
                    tvUltimaBusqueda.setVisibility(View.GONE);
                }
            }
        });


        btSearch.setOnClickListener(view -> {
            etPhone.clearFocus();
            searchIfPermitted();
            editor.putString("ultimaBusqueda", etPhone.getText().toString());
            editor.apply();
            cvm.guardarBusquedaCsv(etPhone.getText().toString());
        });

        sh = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sh.edit();
        busquedaPrincipio = cvm.getBusquedaPrincipio();
        guardarUltimaBusqueda = cvm.guardarBusqueda();
        if(guardarUltimaBusqueda) {
            ultimaBusqueda = cvm.getUltimaBusqueda();
        }
    }

    //Pedir permisos
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestPermission() {
        requestPermissions(new String[]{Manifest.permission.READ_CONTACTS},PERMISSION_CONTACTS);
    }


    // Buscar si esta permitido
    private void searchIfPermitted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //La version de android es posterior a la 6 (incluida)
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
                //Ya tengo el permiso
                tvResult.setText(cvm.search(etPhone.getText().toString()));
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS)) {
                explain();
            } else {
                requestPermission();
            }
        } else { //La version de android es anterior a la 6
            //Ya tengo el permiso
            tvResult.setText(cvm.search(etPhone.getText().toString()));
        }
    }

    private void showRationaleDialog (String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title).setMessage(message).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //nada
            }
        }).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermission();
                }
            }
        });
        builder.create().show();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.app_menu, menu);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.ajustes:
                //Intent i = new Intent(this, AjustesActivity.class);
                Intent i = new Intent(this, SettingsActivity.class);
                startActivity(i);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


}