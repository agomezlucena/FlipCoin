package com.agomezlucena.pruebaanimationdrawable;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private final int VIDAS_MAXIMAS = 3;
    private final int PUNTOS_PARA_GANAR = 10;
    private final int VIDAS_PARA_PERDER = 0;
    private final int FRAME_CORRECTO = 0;

    private boolean animacionCorriendo = false;

    private int puntuacion = 0;
    private int vidasRestantes = VIDAS_MAXIMAS;

    private SoundPool       sonidos;
    private MediaPlayer     reproductorMusica;
    private int             posicionEnReproduccion = 0;

    private int[]       posicionSonidos = new int[3];

    private AnimationDrawable   marcoAnimacion;
    private ImageView           fondoAnimacion;
    private ImageView[]         representacionVidas = new ImageView[3];
    private TextView            txtContador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init(){
        cargarElementosVisuales();
        cargarSonidos();
        reproductorMusica = MediaPlayer.create(this,R.raw.the_house_of_rising_sun);
        reproductorMusica.setLooping(true);
        reproductorMusica.setVolume(1f,1f);
        reproductorMusica.start();
        fondoAnimacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                manejarAccionAnimacion();
            }
        });
    }

    private void acierta(){
        puntuacion++;
        txtContador.setText(String.format(getString(R.string.contador_acertados_formato), puntuacion));
        sonidos.play(posicionSonidos[1], 1, 1, 1, 0, 1);

        if (puntuacion == PUNTOS_PARA_GANAR) gana();
    }

    private void falla(){
        vidasRestantes--;
        representacionVidas[vidasRestantes].setVisibility(View.INVISIBLE);
        sonidos.play(posicionSonidos[2],1,1,1,0,1);

        if (vidasRestantes == VIDAS_PARA_PERDER) pierde();
    }

    private void haDado(){
        boolean haDado = (marcoAnimacion.getCurrent().equals(marcoAnimacion.getFrame(FRAME_CORRECTO)));
        if(haDado) acierta();
        else falla();
    }

    private void pierde(){
        new AlertDialog.Builder(MainActivity.this)
                .setTitle("has perdido")
                .setMessage("Has perdido empenzando de nuevo")
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                       reiniciarJuego();
                    }
                })
                .show();
    }

    private void gana(){
        new AlertDialog.Builder(MainActivity.this)
                .setTitle("has ganado")
                .setMessage("Has ganado empenzando de nuevo")
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        reiniciarJuego();
                    }
                })
                .show();
    }

    private void manejarAccionAnimacion(){
        if (animacionCorriendo){
            marcoAnimacion.stop();
            animacionCorriendo = false;
            haDado();
            reproductorMusica.seekTo(posicionEnReproduccion);
            reproductorMusica.start();
        } else{
            sonidos.play(posicionSonidos[0],1,1,1,0,1);
            animacionCorriendo = true;
            marcoAnimacion.start();
            posicionEnReproduccion = reproductorMusica.getCurrentPosition();
            reproductorMusica.pause();
        }
    }

    private void cargarElementosVisuales(){
        this.txtContador = findViewById(R.id.contador);
        this.fondoAnimacion = findViewById(R.id.imageView);
        fondoAnimacion.setBackgroundResource(R.drawable.animacion_monedas);
        this.marcoAnimacion = (AnimationDrawable) fondoAnimacion.getBackground();

        representacionVidas[0]= findViewById(R.id.vida1);
        representacionVidas[1]= findViewById(R.id.vida2);
        representacionVidas[2]= findViewById(R.id.vida3);
    }

    private void cargarSonidos(){
       AudioAttributes atributes = new AudioAttributes.Builder()
               .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
               .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
               .build();

       sonidos = new SoundPool.Builder()
               .setMaxStreams(3)
               .setAudioAttributes(atributes)
               .build();

       posicionSonidos[0] = sonidos.load(this,R.raw.monedas,1);
       posicionSonidos[1] = sonidos.load(this,R.raw.correcto,1);
       posicionSonidos[2] = sonidos.load(this,R.raw.fallo,1);
    }

    private void reiniciarJuego(){
        puntuacion = 0;
        vidasRestantes = VIDAS_MAXIMAS;
        marcoAnimacion.selectDrawable(0);
        txtContador.setText(String.format(getString(R.string.contador_acertados_formato), puntuacion));

        for (int i = 0; i < representacionVidas.length; i++) {
            representacionVidas[i].setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        reproductorMusica.stop();
    }
}
