package com.lxdnz.nz.ariaorienteering.customisers

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem

class StringClusterItem(val title: String, val latLng: LatLng) : ClusterItem {
    override fun getPosition(): LatLng {
        return latLng
    }

    override fun getTitle(): String? {
        return title
    }

    override fun getSnippet(): String? {
        TODO("Not yet implemented")
    }

    override fun getZIndex(): Float? {
        TODO("Not yet implemented")
    }

}