package com.example.gymfindermadrid;

import com.google.gson.annotations.SerializedName;

public class GimnasiosDetailsResponse {

    @SerializedName("result")
    private GimnasioDetails result;

    public GimnasioDetails getResult() {
        return result;
    }

    public void setResult(GimnasioDetails result) {
        this.result = result;
    }

    public static class GimnasioDetails {

        @SerializedName("name")
        private String name;

        @SerializedName("formatted_address")
        private String address;

        @SerializedName("formatted_phone_number")
        private String phoneNumber;

        @SerializedName("website")
        private String website;

        @SerializedName("rating")
        private Float rating;


        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }


        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }


        public String getPhoneNumber() {
            return phoneNumber;
        }

        public void setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
        }

        public String getWebsite() {
            return website;
        }

        public void setWebsite(String website) {
            this.website = website;
        }


        public Float getRating() {
            return rating;
        }

        public void setRating(Float rating) {
            this.rating = rating;
        }
    }
}
