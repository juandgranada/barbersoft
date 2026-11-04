package com.barbersoft.barbersoft.dto;

import java.math.BigDecimal;

public class ResumenServiciosResponse {

    private Integer cantidadServicios;
    private BigDecimal precioTotal;
    private Integer duracionTotal;

    public ResumenServiciosResponse() {
    }

    public ResumenServiciosResponse(
            Integer cantidadServicios,
            BigDecimal precioTotal,
            Integer duracionTotal) {

        this.cantidadServicios = cantidadServicios;
        this.precioTotal = precioTotal;
        this.duracionTotal = duracionTotal;
    }

    public Integer getCantidadServicios() {
        return cantidadServicios;
    }

    public void setCantidadServicios(Integer cantidadServicios) {
        this.cantidadServicios = cantidadServicios;
    }

    public BigDecimal getPrecioTotal() {
        return precioTotal;
    }

    public void setPrecioTotal(BigDecimal precioTotal) {
        this.precioTotal = precioTotal;
    }

    public Integer getDuracionTotal() {
        return duracionTotal;
    }

    public void setDuracionTotal(Integer duracionTotal) {
        this.duracionTotal = duracionTotal;
    }
}