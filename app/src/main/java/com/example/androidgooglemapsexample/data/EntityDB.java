package com.example.androidgooglemapsexample.data;

import androidx.room.ColumnInfo;
import androidx.room.PrimaryKey;

import java.util.Objects;

@androidx.room.Entity(tableName = "entity_db")
public class EntityDB {
    @PrimaryKey
    private long id;
    @ColumnInfo(name="latitude")
    private double latitude;
    @ColumnInfo(name="longitude")
    private double longitude;
    @ColumnInfo(name="title")
    private String title;

    public EntityDB(long id, double latitude, double longitude) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.title = "Post" + id;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EntityDB entityDB = (EntityDB) o;
        return id == entityDB.id &&
                Double.compare(entityDB.latitude, latitude) == 0 &&
                Double.compare(entityDB.longitude, longitude) == 0 &&
                Objects.equals(title, entityDB.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, latitude, longitude, title);
    }

    @Override
    public String toString() {
        return "EntityDB{" +
                "id=" + id +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", title='" + title + '\'' +
                '}';
    }
}
