package es.carlossc212.consultaagenda_archivos.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import es.carlossc212.consultaagenda_archivos.model.Repository;

public class ContactsViewModel extends AndroidViewModel {
    private Repository rep;

    public ContactsViewModel(@NonNull Application application) {
        super(application);
        rep = new Repository(application);
    }

    public String search(String number){
        return rep.search(number);
    }

    public void guardarBusquedaCsv(String number){
        rep.guardarBusquedaCsv(number);
    }

    public boolean getBusquedaPrincipio(){
        return rep.getBusquedaPrincipio();
    }

    public String getUltimaBusqueda(){
        return rep.getUltimaBusqueda();
    }

    public boolean guardarBusqueda(){
        return rep.guardarBusqueda();
    }
}
