package fr.upec.elarbi.wifibygps_iut;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    //List<Mesure>lm;
    //String storemac;
    int[] viewCoords = new int[2];
    ImageView image;
    TextView texte;
    String valeurs;

    /**
     * Cette fonction permet de calculer la distance entre notre téléphone et le point d'accès wifi
     * @param levelInDb Puissance du signal reçu
     * @param freqInMHz Fréquence du signal reçu
     * @return retourne la distance
     */
    public double calculateDistance(double levelInDb, double freqInMHz)    {
        double exp = (27.55 - (20 * Math.log10(freqInMHz)) + Math.abs(levelInDb)) / 20.0;
        return Math.pow(10.0, exp);
    }

    /**
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        image = (ImageView) findViewById(R.id.imageView);
        image.getLocationOnScreen(viewCoords);
        texte = (TextView) findViewById(R.id.textView);

        /**
         * Cette fonction permet d'afficher les coordonnées de l'endroit où on touche l'écran, puis de calculer la distance avec le point d'accès
         */
        image.setOnTouchListener(new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            int touchX = (int) event.getX();
            int touchY = (int) event.getY();
            final int imageX = touchX - viewCoords[0];
            final int imageY = touchY - viewCoords[1];

            valeurs = "X = " + imageX + "\nY = " + imageY;
            texte.setText(valeurs);

            final  WifiManager wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            /**
             * Cette fonction lance le scan Wifi et permet d'afficher les résultats : adresse mac de la borne, la distance en mètre, le canal...
             */
            registerReceiver(new BroadcastReceiver()
                {
                public void onReceive(Context c, Intent intent) {
                    List<ScanResult> results = wifi.getScanResults();

                    int rssi = 100; //valeur rssi de test
                    int chan = -1; //valeur de test
                    String mac = "";


                    for (ScanResult s : results) {
                        if (s.SSID.equals("Etudiants-Paris12") & Math.abs(s.level) < rssi) {
                            rssi = Math.abs(s.level);
                            mac = s.BSSID;
                            chan = s.frequency;
                        }
                        DecimalFormat df = new DecimalFormat("#.##");
                        texte.setText("Etudiants-Paris12 BSSID : " + mac + "RSSI : " + rssi + " , Distance : " +
                                df.format(calculateDistance((double) rssi, chan)) + "m   -- " + "Canal : " + chan);

                        /**
                         * A partir de là, je n'ai pas réussi à faire fonctionner mon code... (c'est en cours de test).
                         */

                        //if (lm.isEmpty()) {
                            //storemac = mac;
                        //}
                        //if (mac == storemac) {
                            //lm.add(new Mesure(imageX, imageY, calculateDistance(double)rssi))
                        //}

                    //}
                    //if (lm.size() > 2) {
                    }
                }
            }, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

            wifi.startScan();

            return true;
        }
    });

    }

}