package it.unitn.lpsmt.moodonmap.utils;

import android.content.Context;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

/**
 * Created by Mattia on 08/05/2016.
 */
public class OwnIconRendered extends DefaultClusterRenderer<Place> {

    public OwnIconRendered(Context context, GoogleMap map, ClusterManager<Place> clusterManager) {
        super(context, map, clusterManager);
    }

    @Override
    protected void onBeforeClusterItemRendered(Place item, MarkerOptions markerOptions) {
        markerOptions.icon(item.getImmagine());
        markerOptions.snippet(item.getSnippet());
        markerOptions.title(item.getMessage());
        super.onBeforeClusterItemRendered(item, markerOptions);
    }
}
