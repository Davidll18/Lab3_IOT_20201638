package com.example.lab3_20201638;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class Tareas implements Parcelable {

    private int id;

    @SerializedName("todo")
    private String tareas;

    @SerializedName("completed")
    private boolean completado;

    private int userId;

    protected Tareas(Parcel in) {
        id = in.readInt();
        tareas = in.readString();
        completado = in.readByte() != 0;
        userId = in.readInt();
    }

    public static final Creator<Tareas> CREATOR = new Creator<Tareas>() {
        @Override
        public Tareas createFromParcel(Parcel in) {
            return new Tareas(in);
        }

        @Override
        public Tareas[] newArray(int size) {
            return new Tareas[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(tareas);
        dest.writeByte((byte) (completado ? 1 : 0));
        dest.writeInt(userId);
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTarea() {
        return tareas;
    }

    public void setTarea(String tarea) {
        this.tareas = tarea;
    }

    public boolean isCompletado() {
        return completado;
    }

    public void setCompletado(boolean completado) {
        this.completado = completado;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "Tarea{" +
                "id=" + id +
                ", tarea='" + tareas + '\'' +
                ", completado=" + completado +
                ", userId=" + userId +
                '}';
    }
}
