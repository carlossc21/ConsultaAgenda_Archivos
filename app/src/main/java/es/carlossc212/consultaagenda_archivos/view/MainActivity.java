package es.carlossc212.consultaagenda_archivos.view;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LiveData;

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
import android.provider.UserDictionary;
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
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import es.carlossc212.consultaagenda_archivos.R;
import es.carlossc212.consultaagenda_archivos.model.Repository;

public class MainActivity extends AppCompatActivity {

    private Button BtSearch;
    private EditText ETPhone;
    private TextView tvResult, tvUltimaBusqueda;
    private final int PERMISSION_CONTACTS = 1;
    Repository rep;

    SharedPreferences sh;
    SharedPreferences.Editor editor;
    int modoBusqueda;
    int guardarBusqueda;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rep = new Repository();

        initialize();
        sh = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sh.edit();
        modoBusqueda = sh.getInt("formaBusqueda", 0);
        guardarBusqueda = sh.getInt("guardarBusqueda", 1);

    }

    @Override
    protected void onResume() {
        super.onResume();
        modoBusqueda = sh.getInt("formaBusqueda", 0);
        guardarBusqueda = sh.getInt("guardarBusqueda", 1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){
            case PERMISSION_CONTACTS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults.length >0){
                    //permiso
                    rep.search(getContentResolver(), ETPhone.getText().toString());
                }else{
                    //sin permiso
                }
                break;
        }
    }



    //Mensaje de explicacion de los permisos
    private void explain() {
        showRationaleDialog("Permisos requeridos", "Para que esta app funcione se necesita acceder a los contactos", Manifest.permission.READ_CONTACTS, PERMISSION_CONTACTS);
    }
    //Se inicializan los componentes
    private void initialize() {

        BtSearch = findViewById(R.id.BtSearch);
        ETPhone = findViewById(R.id.ETPhone);
        tvResult = findViewById(R.id.tvResult);
        tvUltimaBusqueda = findViewById(R.id.tvUltimaBusqueda);
        tvUltimaBusqueda.setOnClickListener(v->{
            ETPhone.setText(tvUltimaBusqueda.getText().toString());
        });

        ETPhone.setOnFocusChangeListener((v, hasFocus) -> {
            if (guardarBusqueda == 1) {
                if (hasFocus) {
                    tvUltimaBusqueda.setVisibility(View.VISIBLE);
                    tvUltimaBusqueda.setText(sh.getString("ultimaBusqueda", ""));
                }else{
                    tvUltimaBusqueda.setVisibility(View.GONE);
                }
            }
        });


        BtSearch.setOnClickListener(view -> {
            ETPhone.clearFocus();
            searchIfPermitted();

        });
    }
    //Pedir permisos
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestPermission() {
        requestPermissions(new String[]{Manifest.permission.READ_CONTACTS},PERMISSION_CONTACTS);
    }
    //Buscar
    private void search() {
        guardarBusquedaCsv();

        editor.putString("ultimaBusqueda", ETPhone.getText().toString());
        editor.apply();
        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String proyeccion[] = new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME};
        String seleccion = ContactsContract.Contacts.IN_VISIBLE_GROUP + " = ? and " +
                ContactsContract.Contacts.HAS_PHONE_NUMBER + "= ?";
        String argumentos[] = new String[]{"1","1"};
        seleccion = null;
        argumentos = null;
        String orden = ContactsContract.Contacts.DISPLAY_NAME;
        Cursor cursor = getContentResolver().query(uri, proyeccion, seleccion, argumentos, orden);


        String displayname;


        int columnaNombre = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
        int columnaNumero = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);

        String contactos = "";

        while (cursor.moveToNext()){

            displayname = cursor.getString(columnaNombre);
            System.out.println(displayname);

            switch(modoBusqueda) {
                case 0:
                    if (cursor.getString(columnaNumero).contains(ETPhone.getText().toString())) {
                        contactos = contactos + displayname + "\n";
                    }
                    break;

                case 1:
                    if (cursor.getString(columnaNumero).startsWith(ETPhone.getText().toString())) {
                        contactos = contactos + displayname + "\n";
                    }
                    break;
            }
        }
        tvResult.setText(contactos);



        //SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        //String email = sharedPreferences.getString("email", "no existe");
    }
    // Buscar si esta permitido
    private void searchIfPermitted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //La version de android es posterior a la 6 (incluida)
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
                //Ya tengo el permiso
                search();
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS)) {
                explain();

            } else {
                requestPermission();
            }
        } else { //La version de android es anterior a la 6
            //Ya tengo el permiso
            search();
        }
    }

    private void showRationaleDialog (String title, String message, String permission, int requestCode) {
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
                Intent i = new Intent(this, AjustesActivity.class);
                startActivity(i);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void guardarBusquedaCsv(){
        File f = new File(getExternalFilesDir(null), "busquedas.csv");

        try {
            BufferedWriter bfw = new BufferedWriter(new FileWriter(f, true));


            //busqueda;dd/mm/aa-00:00
            Calendar c = Calendar.getInstance();
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy-HH:mm:ss");
            String fecha = formatter.format(c.getTime());
            //System.out.println(fecha);

            String csv = ETPhone.getText().toString()+";"+fecha+"\n";
            bfw.write(csv);
            bfw.flush();

            System.out.println("escrito");

        } catch (FileNotFoundException e) {
            System.out.println("Hoal");
        } catch (IOException e) {
            System.out.println("Hola");
        }

    }
}