package com.example.livraison.acitvity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.example.livraison.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.osmdroid.config.Configuration;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViewItineraire extends AppCompatActivity {
    private MapView map;
    private FirebaseFirestore db;
    Button deliveryClearButton;
    private GeoPoint warehouse= new GeoPoint(49.4336728, 1.0839967);;
    private List<GeoPoint> deliveryPoints = new ArrayList<>();
    private MyLocationNewOverlay myLocationOverlay;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_itineraire);

        Button goBackButton = findViewById(R.id.goback_button);
        Button deliveryClearButton = findViewById(R.id.delivery_clear_button);
        deliveryClearButton.setOnClickListener(v -> {
            ArrayList<String> orderIds = getIntent().getStringArrayListExtra("ORDER_ID_LIST");
            if (orderIds != null && !orderIds.isEmpty()) {
                updateOrdersAsDelivered(orderIds);
            }

            Intent intent = new Intent(this, ChauffeurHome.class);
            startActivity(intent);
            finish();
        });
        goBackButton.setOnClickListener(v -> finish());

        db = FirebaseFirestore.getInstance();
        Configuration.getInstance().load(this, getPreferences(MODE_PRIVATE));

        map = findViewById(R.id.map);
        map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);

        deliveryPoints.add(warehouse);
        addMarker(warehouse, "Entrepot",R.drawable.warehouse);

        ArrayList<String> orderIds = getIntent().getStringArrayListExtra("ORDER_ID_LIST");
        if (orderIds != null && !orderIds.isEmpty()) {
            fetchDeliveryPoints(orderIds);
        } else {
            Toast.makeText(this, "Liste des ID de commande non fournie", Toast.LENGTH_LONG).show();
        }
        drawPolyline();
    }

    private void fetchDeliveryPoints(ArrayList<String> orderIds) {
        Map<Integer, GeoPoint> tempDeliveryPoints = new HashMap<>();

        for (String orderId : orderIds) {
            db.collection("orders").document(orderId).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null && document.exists()) {
                        String latitudeStr = document.getString("latitude");
                        String longitudeStr = document.getString("longitude");
                        String stepStr = document.getString("step");

                        if (latitudeStr != null && longitudeStr != null && stepStr != null) {
                            try {
                                double latitude = Double.parseDouble(latitudeStr);
                                double longitude = Double.parseDouble(longitudeStr);
                                int step = Integer.parseInt(stepStr);
                                GeoPoint deliveryPoint = new GeoPoint(latitude, longitude);

                                tempDeliveryPoints.put(step, deliveryPoint);

                                // Vérifier si tous les points ont été récupérés
                                if (tempDeliveryPoints.size() == orderIds.size()) {
                                    List<Map.Entry<Integer, GeoPoint>> sortedEntries = new ArrayList<>(tempDeliveryPoints.entrySet());
                                    sortedEntries.sort(Map.Entry.comparingByKey());

                                    for (Map.Entry<Integer, GeoPoint> entry : sortedEntries) {
                                        deliveryPoints.add(entry.getValue());
                                        addMarker(entry.getValue(), "Commande ID: " + orderId, R.drawable.delivery_point);
                                    }
                                    // Pour ajouter le dépôt en tant que dernier point pour revenir au début du circuit
                                    deliveryPoints.add(warehouse);

                                    drawPolyline();
                                    map.getController().setCenter(deliveryPoints.get(0));
                                    map.getController().setZoom(14);
                                }
                            } catch (NumberFormatException e) {
                                Toast.makeText(ViewItineraire.this, "Format des coordonnées invalide", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(ViewItineraire.this, "Coordonnées de livraison manquantes", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(ViewItineraire.this, "Localisation de commande introuvable pour ID: " + orderId, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ViewItineraire.this, "Erreur lors de la récupération des localisations des commandes", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void addMarker(GeoPoint point, String title, int drawableResId) {
        Marker marker = new Marker(map);
        marker.setPosition(point);
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        marker.setTitle(title);
        marker.setIcon(getResources().getDrawable(drawableResId, getApplicationContext().getTheme()));
        map.getOverlays().add(marker);

        marker.setOnMarkerClickListener((marker1, mapView) -> {
            map.getController().animateTo(marker1.getPosition());
            map.getController().setZoom(18);
            return true;
        });
    }



    private void drawPolyline() {
        Polyline line = new Polyline();
        line.setPoints(deliveryPoints);
        map.getOverlays().add(line);

        if (!deliveryPoints.isEmpty()) {
            setMapViewport(deliveryPoints);
        }

        map.invalidate(); // Redessinez la carte pour montrer la polyline
    }

    private void setMapViewport(List<GeoPoint> points) {
        if (points.size() == 1) {
            // Si un seul point, centrez la carte sur ce point
            map.getController().setCenter(points.get(0));
            map.getController().setZoom(14);
        } else {
            // Calcul des bornes pour inclure tous les points
            double minLat = Double.MAX_VALUE;
            double maxLat = Double.MIN_VALUE;
            double minLon = Double.MAX_VALUE;
            double maxLon = Double.MIN_VALUE;
            for (GeoPoint point : points) {
                minLat = Math.min(point.getLatitude(), minLat);
                maxLat = Math.max(point.getLatitude(), maxLat);
                minLon = Math.min(point.getLongitude(), minLon);
                maxLon = Math.max(point.getLongitude(), maxLon);
            }

            // Ajustez la vue de la carte pour inclure tous les points
            map.zoomToBoundingBox(new BoundingBox(maxLat, maxLon, minLat, minLon), true);
        }
    }

    // Méthode pour mettre à jour les commandes comme "delivered"
    private void updateOrdersAsDelivered(ArrayList<String> orderIds) {
        for (String orderId : orderIds) {
            db.collection("orders").document(orderId)
                    .update("state", "delivered")
                    .addOnSuccessListener(aVoid -> Toast.makeText(ViewItineraire.this, "Commande livrée (ID: " + orderId + ")", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(ViewItineraire.this, "Erreur lors de la mise à jour de la commande (ID: " + orderId + ")", Toast.LENGTH_SHORT).show());
        }
    }
}
