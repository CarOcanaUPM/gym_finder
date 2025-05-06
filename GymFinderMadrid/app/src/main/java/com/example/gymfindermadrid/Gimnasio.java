package com.example.gymfindermadrid;

import com.google.gson.annotations.SerializedName;
import java.util.List;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.Ignore;

@Entity(tableName = "gimnasios")
public class Gimnasio {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @SerializedName("name")
    private String nombre;

    @SerializedName("vicinity")
    private String direccion;

    @SerializedName("rating")
    private float puntuacion;

    @Ignore
    @SerializedName("geometry")
    private Geometry geometry;

    @Ignore
    @SerializedName("photos")
    private List<Photo> photos;

    @SerializedName("place_id")
    private String placeId;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public float getPuntuacion() { return puntuacion; }
    public void setPuntuacion(float puntuacion) { this.puntuacion = puntuacion; }

    public String getPlaceId() { return placeId; }
    public void setPlaceId(String placeId) { this.placeId = placeId; }


    public Geometry getGeometry() {
        return geometry;
    }


    public String getPhotoUrl(String apiKey) {
        if (photos != null && !photos.isEmpty()) {
            String photoReference = photos.get(0).photo_reference;
            return "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference="
                    + photoReference + "&key=" + apiKey;
        }
        return null;
    }


    public class Geometry {
        @SerializedName("location")
        private Location location;

        public Location getLocation() {
            return location;
        }
    }

    public class Location {
        @SerializedName("lat")
        private double lat;

        @SerializedName("lng")
        private double lng;

        public double getLat() {
            return lat;
        }

        public double getLng() {
            return lng;
        }
    }


    public static class Photo {
        @SerializedName("photo_reference")
        private String photo_reference;

        public String getPhotoReference() {
            return photo_reference;
        }

        public void setImagenPath(String imagenPath) {
            this.photo_reference = imagenPath;
        }
    }
}
