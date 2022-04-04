package es.carlossc212.consultaagenda_archivos.model;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

public class Repository {


    public Repository(){

    }

    public String search(ContentResolver contentResolver, String number) {


        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String proyeccion[] = new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME};
        String seleccion = ContactsContract.Contacts.IN_VISIBLE_GROUP + " = ? and " +
                ContactsContract.Contacts.HAS_PHONE_NUMBER + "= ?";
        String argumentos[] = new String[]{"1","1"};
        seleccion = null;
        argumentos = null;
        String orden = ContactsContract.Contacts.DISPLAY_NAME;
        Cursor cursor = contentResolver.query(uri, proyeccion, seleccion, argumentos, orden);


        String displayname;


        int columnaNombre = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
        int columnaNumero = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);

        String contactos = "";

        while (cursor.moveToNext()){

            displayname = cursor.getString(columnaNombre);
            System.out.println(displayname);

            if (cursor.getString(columnaNumero).contains(number)){
                contactos = contactos+displayname+"\n";
            }
        }
        return contactos;



        //SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        //String email = sharedPreferences.getString("email", "no existe");
    }
}
