package com.example.androidgooglemapsexample.data;

import androidx.room.ColumnInfo;
import androidx.room.PrimaryKey;

import java.util.Objects;

@androidx.room.Entity(tableName = "entity_db")
public class EntityDB {
    @PrimaryKey
    private long id;
    @ColumnInfo(name="latitude")
    private long latitude;
    @ColumnInfo(name="longitude")
    private long longitude;

    public EntityDB(long id, long latitude, long longitude) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getLatitude() {
        return latitude;
    }

    public void setLatitude(long latitude) {
        this.latitude = latitude;
    }

    public long getLongitude() {
        return longitude;
    }

    public void setLongitude(long longitude) {
        this.longitude = longitude;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EntityDB entityDB = (EntityDB) o;
        return id == entityDB.id &&
                latitude == entityDB.latitude &&
                longitude == entityDB.longitude;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, latitude, longitude);
    }

    @Override
    public String toString() {
        return "EntityDB{" +
                "id=" + id +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
}
