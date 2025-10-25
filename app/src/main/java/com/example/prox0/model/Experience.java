package com.example.prox0.model;

public class Experience {
    private int id;
    private String categoria; // Campo agregado
    private String producto;
    private String servicio;
    private double costo;
    private boolean isFavorite;
    private String fecha;
    private String descripcion;

    public Experience() {
        // Constructor corregido con valores por defecto
        this.categoria = "General";
        this.isFavorite = false;
        this.fecha = java.text.DateFormat.getDateInstance().format(new java.util.Date());
    }

    public Experience(String producto, String servicio, double costo, String descripcion) {
        this.categoria = "General"; // Valor por defecto
        this.producto = producto;
        this.servicio = servicio;
        this.costo = costo;
        this.descripcion = descripcion;
        this.isFavorite = false;
        this.fecha = java.text.DateFormat.getDateInstance().format(new java.util.Date());
    }

    public Experience(String categoria, String producto, String servicio, double costo, String descripcion) {
        this.categoria = categoria != null ? categoria : "General"; // Protección contra null
        this.producto = producto;
        this.servicio = servicio;
        this.costo = costo;
        this.descripcion = descripcion;
        this.isFavorite = false;
        this.fecha = java.text.DateFormat.getDateInstance().format(new java.util.Date());
    }

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getCategoria() { // Método corregido
        return categoria != null ? categoria : "General";
    }
    public void setCategoria(String categoria) {
        this.categoria = categoria != null ? categoria : "General";
    }

    public String getProducto() { return producto; }
    public void setProducto(String producto) { this.producto = producto; }

    public String getServicio() { return servicio; }
    public void setServicio(String servicio) { this.servicio = servicio; }

    public double getCosto() { return costo; }
    public void setCosto(double costo) { this.costo = costo; }

    public boolean isFavorite() { return isFavorite; }
    public void setFavorite(boolean favorite) { isFavorite = favorite; }

    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
}
