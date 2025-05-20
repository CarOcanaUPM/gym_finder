package com.example.gymfindermadrid;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Delete;

import java.util.List;

@Dao
public interface GimnasioDao {

    @Insert
    void insertarGimnasio(Gimnasio gimnasio);

    @Query("SELECT * FROM gimnasios")
    List<Gimnasio> obtenerTodos();

    @Delete
    void borrarGimnasio(Gimnasio gimnasio);

    @Query("SELECT * FROM gimnasios WHERE placeId = :placeIdBuscar LIMIT 1")
    Gimnasio obtenerGimnasioPorId(String placeIdBuscar);
}