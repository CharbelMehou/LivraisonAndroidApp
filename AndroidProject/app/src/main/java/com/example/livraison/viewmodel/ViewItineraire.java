package com.example.livraison.viewmodel;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.livraison.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;

import java.util.ArrayList;
import java.util.List;

public class ViewItineraire extends AppCompatActivity {
    private MapView map;
    private IMapController mapController;
    private Button goBackButton;
    private  Button delivery_clear_button;
    private FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_itineraire);

        goBackButton = findViewById(R.id.goback_button);
        goBackButton.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), OnGoingDelivery.class);
            startActivity(intent);
            finish();
        });
        delivery_clear_button=findViewById(R.id.delivery_clear_button);


        db = FirebaseFirestore.getInstance();

        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

        map = findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.SHOW_AND_FADEOUT);
        map.setMultiTouchControls(true);

        mapController = map.getController();
        mapController.setZoom(17.0);

        final MapEventsReceiver mapEventsReceiver = new MapEventsReceiver() {
            @Override
            public boolean singleTapConfirmedHelper(GeoPoint p) {
                Toast.makeText(getBaseContext(), "(" + String.format("%.4f", p.getLatitude()) +
                        " , " + String.format("%.4f", p.getLongitude()) + ")", Toast.LENGTH_LONG).show();
                return false;
            }

            @Override
            public boolean longPressHelper(GeoPoint p) {
                return false;
            }
        };
        map.getOverlays().add(new MapEventsOverlay(mapEventsReceiver));

        // Récupérer l'ID de la commande passée à cette activité
        String orderId = getIntent().getStringExtra("ORDER_ID");

        if (orderId != null && !orderId.isEmpty()) {
            fetchDeliveryPoint(orderId);
        } else {
            Toast.makeText(this, "ID de commande non fourni", Toast.LENGTH_LONG).show();
        }
        delivery_clear_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Mettez à jour le champ 'state' de ce document à 'delivered'.
                db.collection("orders").document(orderId)
                        .update("state", "delivered")
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(ViewItineraire.this,"Commande livrée",Toast.LENGTH_LONG).show();
                            Intent intent =new Intent(getApplicationContext(),OnGoingDelivery.class);
                            startActivity(intent);
                            finish();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(ViewItineraire.this, "Erreur lors de la mise à jour de la commande.", Toast.LENGTH_SHORT).show();
                            // Gérez l'échec ici.
                        });
            }
        });
    }

    private void fetchDeliveryPoint(String orderId) {
        db.collection("orders").document(orderId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document != null && document.exists()) {
                    String latitudeStr = document.getString("latitude");
                    String longitudeStr = document.getString("longitude");
                    if (latitudeStr != null && longitudeStr != null) {
                        try {
                            double latitude = Double.parseDouble(latitudeStr);
                            double longitude = Double.parseDouble(longitudeStr);
                            GeoPoint deliveryPoint = new GeoPoint(latitude, longitude);
                            mapController.setCenter(deliveryPoint);
                            addMarker(deliveryPoint, "Lieu de Livraison");
                        } catch (NumberFormatException e) {
                            Toast.makeText(this, "Format des coordonnées invalide", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "Coordonnées de livraison manquantes", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "Localisation de livraison introuvable", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Erreur lors de la récupération du lieu de livraison", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void addMarker(GeoPoint point, String title) {
        Marker marker = new Marker(map);
        marker.setPosition(point);
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        marker.setTitle(title);
        map.getOverlays().add(marker);
        map.invalidate();
    }

}
