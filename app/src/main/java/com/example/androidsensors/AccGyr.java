package com.example.androidsensors;

public class AccGyr {
    private float dXacc;
    private float dYacc;
    private float dZacc;

    private float dXgyr;
    private float dYgyr;
    private float dZgyr;

    public AccGyr(){
        dXacc = 0;
        dYacc = 0;
        dZacc = 0;
        dXgyr = 0;
        dYgyr = 0;
        dZgyr = 0;
    }
    public float getdXacc() {
        return dXacc;
    }

    public void setdXacc(float dXacc) {
        this.dXacc = dXacc;
    }

    public float getdYacc() {
        return dYacc;
    }

    public void setdYacc(float dYacc) {
        this.dYacc = dYacc;
    }

    public float getdZacc() {
        return dZacc;
    }

    public void setdZacc(float dZacc) {
        this.dZacc = dZacc;
    }

    public float getdXgyr() {
        return dXgyr;
    }

    public void setdXgyr(float dXgyr) {
        this.dXgyr = dXgyr;
    }

    public float getdYgyr() {
        return dYgyr;
    }

    public void setdYgyr(float dYgyr) {
        this.dYgyr = dYgyr;
    }

    public float getdZgyr() {
        return dZgyr;
    }

    public void setdZgyr(float dZgyr) {
        this.dZgyr = dZgyr;
    }

}
