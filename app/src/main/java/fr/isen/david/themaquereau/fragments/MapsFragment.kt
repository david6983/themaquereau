package fr.isen.david.themaquereau.fragments

import androidx.fragment.app.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import fr.isen.david.themaquereau.R

class MapsFragment : Fragment() {
    private val callback = OnMapReadyCallback { googleMap ->
        val toulon = LatLng(43.1207309,5.9391841)
        googleMap.addMarker(MarkerOptions().position(toulon).title("Marqueur sur L'ISEN Toulon"))
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(toulon, 14.0f))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }
}