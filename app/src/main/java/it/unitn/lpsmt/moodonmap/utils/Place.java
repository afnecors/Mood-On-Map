package it.unitn.lpsmt.moodonmap.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;

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

    public Integer getId_emo() {
        return id_emo;
    }

    public void setId_emo(Integer id_emo) {
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