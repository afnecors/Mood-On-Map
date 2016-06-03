package it.unitn.lpsmt.moodonmap.utils;

import android.graphics.Bitmap;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;
import java.io.Serializable;

/**
 * Created by Mattia on 12/05/2016.
 */
// Fa il cluster dei marker passando latitudine-longitudine, titolo, descrizione e immagine del marker
public class Place implements ClusterItem {
    private final LatLng mPosition;
    private final String titolo;
    private final String descrizione;
    private final BitmapDescriptor immagine;

    // Costruttore
    public Place(double lat, double lng, String title, String snippet, BitmapDescriptor icon) {
        mPosition = new LatLng(lat, lng);
        titolo = title;
        descrizione = snippet;
        immagine = icon;
    }

    // Getter vari
    @Override
    public LatLng getPosition() {
        return mPosition;
    }

    public BitmapDescriptor getIcon() {
        return immagine;
    }

    public String getSnippet() {
        return descrizione;
    }

    public String getTitle() {
        return titolo;
    }
}