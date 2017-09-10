package com.amul.dc.pojos;


public class CitiesDto {

    private String cityId;
    private String cityName;

    public CitiesDto() {
    }

    public CitiesDto(String cityId, String cityName) {
        this.cityId = cityId;
        this.cityName = cityName;
    }

    public String getCityId() {
        return cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("CitiesDto{");
        sb.append("cityId='").append(cityId).append('\'');
        sb.append(", cityName='").append(cityName).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
