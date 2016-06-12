package it.unitn.lpsmt.moodonmap.utils;

import android.graphics.Bitmap;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

import java.io.Serializable;

import it.unitn.lpsmt.moodonmap.R;

/**
 * Created by Mattia on 12/05/2016.
 */
// Fa il cluster dei marker passando latitudine-longitudine, titolo, descrizione e immagine del marker
public class Place implements ClusterItem {

    int id;
    String id_device;
    double latitude;
    double longitude;
    String message;
    String snippet;
    Integer id_emo;

    LatLng mPosition;
    BitmapDescriptor immagine;

    // Costruttore
    public Place(String device_id, double lat, double lng, String mess, String snippet, Integer icon) {
        mPosition = new LatLng(lat, lng);


        //immagine = BitmapDescriptorFactory.fromResource(icon);

        latitude = lat;
        longitude = lng;
        id_emo = icon;
        message = mess;
        snippet = snippet;
        id_device = device_id;

        this.forceImageFromIdEmo();
    }

    public void forcePosition() {
        this.mPosition = new LatLng(this.latitude, this.longitude);
    }

    public void forceImageFromIdEmo() {
        Integer sad = R.drawable.sad;
        Integer lol = R.drawable.lol;
        Integer bored = R.drawable.bored;

        if (this.id_emo == sad.intValue())
            this.immagine = BitmapDescriptorFactory.fromResource(sad);
        else if (this.id_emo == lol.intValue())
            this.immagine = BitmapDescriptorFactory.fromResource(lol);
        else if (this.id_emo == bored.intValue())
            this.immagine = BitmapDescriptorFactory.fromResource(bored);
        else
            this.immagine = BitmapDescriptorFactory.fromResource(lol);
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
        return snippet;
    }

    public String getMessage() {
        return message;
    }


    // Setter
    public void setImmagine(BitmapDescriptor immagine) {
        this.immagine = immagine;
    }

    public void setDescrizione(String descrizione) {
        this.message = descrizione;
    }

    public void setMessage(String titolo) {
        this.message = titolo;
    }

    public void setmPosition(LatLng mPosition) {
        this.mPosition = mPosition;
    }

    @Override
    public String toString() {
        String json = "{";
        json += "\"latitude\": \"" + this.latitude + "\",";
        json += "\"longitude\": \"" + this.longitude + "\",";
        json += "\"message\": \"" + this.message + "\",";
        json += "\"id_emo\": \"" + this.id_emo + "\",";
        json += "\"id_device\": \"" + this.id_device + "\"";
        json += "}";
        return json;
    }
}