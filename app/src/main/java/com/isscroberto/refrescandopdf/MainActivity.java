package com.isscroberto.refrescandopdf;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.voghdev.pdfviewpager.library.PDFViewPager;
import es.voghdev.pdfviewpager.library.RemotePDFViewPager;
import es.voghdev.pdfviewpager.library.adapter.PDFPagerAdapter;
import es.voghdev.pdfviewpager.library.remote.DownloadFile;

public class MainActivity extends AppCompatActivity implements DownloadFile.Listener {

    @BindView(R.id.layout_configuracion)
    LinearLayout layoutConfiguracion;
    @BindView(R.id.text_url)
    EditText textUrl;
    @BindView(R.id.view_pdf)
    PDFViewPager viewPdf;

    PDFPagerAdapter adapter;
    RemotePDFViewPager viewPager;
    String url = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        // Obtener url si se ha guardado previamente.
        this.url = getSharedPreferences("com.isscroberto.refrescandopdf", 0).getString("url", "");
        if(!this.url.equals("")){
            IniciarTimer();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.adapter.close();
    }

    @Override
    public void onSuccess(String url, String destinationPath) {
        this.adapter = new PDFPagerAdapter(this, destinationPath);
        this.adapter.notifyDataSetChanged();
        this.viewPdf.setAdapter(this.adapter);
    }

    @Override
    public void onFailure(Exception e) {

    }

    @Override
    public void onProgressUpdate(int progress, int total) {

    }

    private void IniciarTimer() {
        TimerTask local1 = new TimerTask() {
            @Override
            public void run() {
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        MainActivity.this.CargarPdf();
                    }
                });
            }
        };
        new Timer().schedule(local1, 10L, 300000L);
    }

    private void CargarPdf() {
        // Ocultar configuración.
        this.layoutConfiguracion.setVisibility(View.GONE);

        // Actualizar hora de última actualización.
        Calendar localCalendar = Calendar.getInstance();
        SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat("HH:mm");
        getSupportActionBar().setTitle("Última Actualización: " + localSimpleDateFormat.format(localCalendar.getTime()));

        // Cargar y mostrar imagen.
        this.viewPdf.setVisibility(View.VISIBLE);
        this.viewPager = new RemotePDFViewPager(this, this.url, this);
    }

    @OnClick(R.id.button_aceptar)
    public void buttonAceptarOnClick() {
        this.url = this.textUrl.getText().toString();

        SharedPreferences.Editor editor = getSharedPreferences("com.isscroberto.refrescandopdf", 0).edit();
        editor.putString("url", this.url);
        editor.apply();

        IniciarTimer();
    }
}
