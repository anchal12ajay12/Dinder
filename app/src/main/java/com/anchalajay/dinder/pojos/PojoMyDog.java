package com.anchalajay.dinder.pojos;

public class PojoMyDog {
    String breed_name;
    String image_url;
    long date_my_petted;

    public PojoMyDog() {
    }

    public PojoMyDog(String breed_name, String image_url, long date_my_petted) {
        this.breed_name = breed_name;
        this.image_url = image_url;
        this.date_my_petted = date_my_petted;
    }

    public String getBreed_name() {
        return breed_name;
    }

    public void setBreed_name(String breed_name) {
        this.breed_name = breed_name;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public long getDate_my_petted() {
        return date_my_petted;
    }

    public void setDate_my_petted(long date_my_petted) {
        this.date_my_petted = date_my_petted;
    }
}
