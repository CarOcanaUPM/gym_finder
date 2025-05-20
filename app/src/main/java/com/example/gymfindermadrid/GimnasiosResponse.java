package com.example.gymfindermadrid;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class GimnasiosResponse {
    @SerializedName("results")
    public List<Gimnasio> results;
}
