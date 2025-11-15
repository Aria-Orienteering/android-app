package com.lxdnz.nz.ariaorienteering.customisers

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem

class StringClusterItem(private val titleText: String, val latLng: LatLng) : ClusterItem {
    override fun getPosition(): LatLng {
        return latLng
    }

    override fun getTitle(): String? {
        return titleText
    }

    override fun getSnippet(): String? {
        TODO("Not yet implemented")
    }

    override fun getZIndex(): Float? {
        TODO("Not yet implemented")
    }

}