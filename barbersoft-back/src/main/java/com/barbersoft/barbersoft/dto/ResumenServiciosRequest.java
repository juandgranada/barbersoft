package com.barbersoft.barbersoft.dto;

import java.util.List;

public class ResumenServiciosRequest {

    private List<Long> serviciosIds;

    public ResumenServiciosRequest() {
    }

    public List<Long> getServiciosIds() {
        return serviciosIds;
    }

    public void setServiciosIds(List<Long> serviciosIds) {
        this.serviciosIds = serviciosIds;
    }
}