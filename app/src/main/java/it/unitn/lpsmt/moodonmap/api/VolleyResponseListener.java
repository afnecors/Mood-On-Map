package it.unitn.lpsmt.moodonmap.api;

import org.json.JSONArray;

import it.unitn.lpsmt.moodonmap.utils.Marker;

/**
 * Created by francesco on 17/05/16.
 */
public interface VolleyResponseListener {
    void onResponse(JSONArray response);
    void onError(String message);
}
