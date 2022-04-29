package es.carlossc212.consultaagenda_archivos.model;

import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Repository {

    private Context context;
    private SharedPreferences sh;

    public Repository(Context c){

        this.context = c;
        sh = PreferenceManager.getDefaultSharedPreferences(context);

    }

    public String search(String number) {

        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String proyeccion[] = new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME};
        String seleccion = ContactsContract.Contacts.IN_VISIBLE_GROUP + " = 1 and " +
                ContactsContract.Contacts.HAS_PHONE_NUMBER + "= 1 and (" +
                ContactsContract.CommonDataKinds.Phone.NUMBER + " like ? or " +
                ContactsContract.CommonDataKinds.Phone.NUMBER + " like ?)";
        String argumentos[] = new String[]{"1","1"};
        // number -> %number% %n_u_m_b_e_r%
        seleccion = null;
        argumentos = null;
        String orden = ContactsContract.Contacts.DISPLAY_NAME;
        Cursor cursor = context.getContentResolver().query(uri, proyeccion, seleccion, argumentos, orden);

        String displayname;

        int columnaNombre = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
        int columnaNumero = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);

        String contactos = "";
        String numero="";
        String[] eliminar = {"(",")","-"," "};
        while (cursor.moveToNext()){
            numero = cursor.getString(columnaNumero);

            for (String c : eliminar) {
                numero = numero.replace(c,"");
            }
            System.out.println(numero);
            displayname = cursor.getString(columnaNombre);
            System.out.println(displayname);
            if(sh.getBoolean("buscar1", false)) {
                if (numero.startsWith(number)){
                    contactos = contactos + displayname + "\n";
                }
            } else {
                if (numero.contains(number)) {
                    contactos = contactos + displayname + "\n";
                }
            }
        }
        return contactos;



        //SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        //String email = sharedPreferences.getString("email", "no existe");
    }

    public void guardarBusquedaCsv(String number){
        File f = new File(context.getExternalFilesDir(null), "busquedas.csv");

        try {
            BufferedWriter bfw = new BufferedWriter(new FileWriter(f, true));


            //busqueda;dd/mm/aa-00:00
            Calendar c = Calendar.getInstance();
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy-HH:mm:ss");
            String fecha = formatter.format(c.getTime());
            //System.out.println(fecha);

            String csv = number+";"+fecha+"\n";
            bfw.write(csv);
            bfw.flush();

            System.out.println("escrito");

        } catch (FileNotFoundException e) {
            System.out.println("Hoal");
        } catch (IOException e) {
            System.out.println("Hola");
        }

    }

    public boolean getBusquedaPrincipio(){
        return sh.getBoolean("buscar1", false);
    }

    public String getUltimaBusqueda(){
        return sh.getString("ultimaBusqueda", "");
    }

    public boolean guardarBusqueda(){
        return sh.getBoolean("guardarUltimaBusqueda", true);
    }
}
