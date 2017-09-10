package com.amul.dc.pojos;


public class RoutesDto {
    private String cityId;
    private String routeId;
    private String routeName;


    public RoutesDto() {
    }

    public RoutesDto(String cityId, String routeId, String routeName) {
        this.cityId = cityId;
        this.routeId = routeId;
        this.routeName = routeName;
    }

    public String getCityId() {
        return cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }

    public String getRouteId() {
        return routeId;
    }

    public void setRouteId(String routeId) {
        this.routeId = routeId;
    }

    public String getRouteName() {
        return routeName;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("RoutesDto{");
        sb.append("cityId='").append(cityId).append('\'');
        sb.append(", routeId='").append(routeId).append('\'');
        sb.append(", routeName='").append(routeName).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
