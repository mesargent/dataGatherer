package com.detroitteatime.datagatherer;

/**
 * Created by marksargent on 10/18/15.
 */
public abstract class Predictor {
    private long id;
    private String category;
    private String name;
    private String model; //JSON string
    private String parameterString; //JSON string
    private String method;
    private String rHhtml;
    protected double[] parameters;

    public Predictor(){};


//Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public double[] getParameters() {
        return parameters;
    }

    public void setParameters(double[] parameters) {
        this.parameters = parameters;
    }

    public String getParameterString() {
        return parameterString;
    }

    public void setParameterString(String parameterString) {
        this.parameterString = parameterString;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getrHhtml() {
        return rHhtml;
    }

    public void setrHhtml(String rHhtml) {
        this.rHhtml = rHhtml;
    }
}
