package it.unitn.lpsmt.moodonmap.utils;

/**
 * Created by francesco on 17/05/16.
 */
public class Marker {
    private int id;
    private int id_emo;
    private String id_device;
    private float latitude;
    private float longitude;
    private String message;
    private String timestamp;
    private boolean visible = true;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId_emo() {
        return id_emo;
    }

    public void setId_emo(int id_emo) {
        this.id_emo = id_emo;
    }

    public String getId_device() {
        return id_device;
    }

    public void setId_device(String id_device) {
        this.id_device = id_device;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }
}
