package es.carlossc212.consultaagenda_archivos.view.old;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Switch;

import es.carlossc212.consultaagenda_archivos.R;

public class AjustesActivity extends AppCompatActivity {
    RadioButton rbCPos, rbPrincipio;
    Switch sGuardarB;
    Button btGuardar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ajustes);
        init();



        SharedPreferences sh = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sh.edit();



        int formaBusqueda = sh.getInt("formaBusqueda", 0); //0 cualquier posición - 1 desde el principio
        int guardarBusqueda = sh.getInt("guardarBusqueda", 1); //0 no guardar - 1 guardar
        String ultimaBusqueda = sh.getString("ultimaBusqueda", "");


        if (formaBusqueda == 0) {
            rbCPos.setChecked(true);
        }
        if (formaBusqueda == 1) {
            rbPrincipio.setChecked(true);
        }
        if (guardarBusqueda == 1){
            sGuardarB.setChecked(true);
        }


        sGuardarB.setOnClickListener(v->{
            if (sGuardarB.isChecked()) {
                editor.putInt("guardarBusqueda", 1);
            }else{
                editor.putInt("guardarBusqueda", 0);
            }
            System.out.println(sGuardarB.isChecked());
        });
       rbCPos.setOnClickListener(v->{
            editor.putInt("formaBusqueda", 0);
        });
        rbPrincipio.setOnClickListener(v->{
            editor.putInt("formaBusqueda", 1);
        });
        btGuardar.setOnClickListener(v->{
            editor.apply();
            finish();
        });
    }


    private void init(){
        rbCPos = findViewById(R.id.rbCPos);
        rbPrincipio = findViewById(R.id.rbPrincipio);
        btGuardar = findViewById(R.id.btGuardar);
        sGuardarB = findViewById(R.id.switchGuardarBusqueda);
    }
}