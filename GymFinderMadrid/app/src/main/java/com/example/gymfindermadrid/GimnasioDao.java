package com.example.gymfindermadrid;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Delete;

import java.util.List;

@Dao
public interface GimnasioDao {

    @Insert
    void insertarGimnasio(GimnasioEntity gimnasio);

    @Query("SELECT * FROM gimnasios")
    List<GimnasioEntity> obtenerTodos();

    @Delete
    void borrarGimnasio(GimnasioEntity gimnasio);

    @Query("SELECT * FROM gimnasios WHERE placeId = :placeIdBuscar LIMIT 1")
    GimnasioEntity obtenerGimnasioPorId(String placeIdBuscar);
}