package it.unitn.lpsmt.moodonmap.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

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
    String id_emo;

    LatLng mPosition;
    BitmapDescriptor immagine;

    // Costruttore
    public Place(String device_id, double lat, double lng, String mess, String snippet, String icon) {

        mPosition = new LatLng(lat, lng);

        latitude = lat;
        longitude = lng;
        id_emo = icon;
        message = mess;
        snippet = snippet;
        id_device = device_id;

    }

    public void forcePosition() {
        this.mPosition = new LatLng(this.latitude, this.longitude);
    }

    public void forceImageFromIdEmo(Context ctx) {
        //String bored_name = this.getResources().getResourceName(R.drawable.bored);
        //Toast.makeText(MainActivity.this, "bored: "+bored, Toast.LENGTH_LONG).show();
        //int bored_id = this.getResources().getIdentifier("bored", "drawable", this.getPackageName());
        //Toast.makeText(MainActivity.this, "bored: "+bored_id, Toast.LENGTH_LONG).show();


        /*
         * Questo è necessario per prevenire immagini casuali nel caso in cui nel DB fossero
         * salvati degli identificatori di emoji non più riconosciuti.
         * Altrimenti sarebbe sufficiente fare:
         *
         * int id = ctx.getResources().getIdentifier(id_emo, "drawable", ctx.getPackageName());
         * immagine = BitmapDescriptorFactory.fromResource(id);
         *
         */
        int sad = ctx.getResources().getIdentifier("sad", "drawable", ctx.getPackageName());
        int lol = ctx.getResources().getIdentifier("lol", "drawable", ctx.getPackageName());
        int bored = ctx.getResources().getIdentifier("bored", "drawable", ctx.getPackageName());
        int vomit = ctx.getResources().getIdentifier("vomit", "drawable", ctx.getPackageName());

        switch (id_emo) {
            case "lol":
                immagine = BitmapDescriptorFactory.fromResource(lol);
                break;

            case "sad":
                immagine = BitmapDescriptorFactory.fromResource(sad);
                break;

            case "bored":
                immagine = BitmapDescriptorFactory.fromResource(bored);
                break;

            case "vomit":
                immagine = BitmapDescriptorFactory.fromResource(vomit);
                break;

            default:
                immagine = BitmapDescriptorFactory.fromResource(R.drawable.broken);
                id_emo = ctx.getResources().getResourceEntryName(R.drawable.broken);
                break;
        }

    }

    @Override
    public LatLng getPosition() {
        return mPosition;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getId_device() {
        return id_device;
    }

    public void setId_device(String id_device) {
        this.id_device = id_device;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSnippet() {
        return snippet;
    }

    public void setSnippet(String snippet) {
        this.snippet = snippet;
    }

    public String getId_emo() {
        return id_emo;
    }

    public void setId_emo(String id_emo) {
        this.id_emo = id_emo;
    }

    public LatLng getmPosition() {
        return mPosition;
    }

    public void setmPosition(LatLng mPosition) {
        this.mPosition = mPosition;
    }

    public BitmapDescriptor getImmagine() {
        return immagine;
    }

    public void setImmagine(BitmapDescriptor immagine) {
        this.immagine = immagine;
    }

    public String getAddress(Context context) {
        Geocoder gcd = new Geocoder(context);
        List<Address> addresses = null;
        try {
            addresses = gcd.getFromLocation(this.latitude, this.longitude, 1);
        } catch (IOException e) {
            return "";
        }
        if (addresses.size() > 0)
            return addresses.get(0).getAddressLine(0)+" "+addresses.get(0).getLocality();
        return "";
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