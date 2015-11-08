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
    protected double[] features;
    protected double[] parameters;


    public Predictor(String name, String category){
        this.name = name;
        this.category = category;
    }

    public Predictor(){};

    public abstract double predictProb(double[] features);

    public abstract boolean predict(double[] features);

//    public double[] unpackModel(){
//        return
//    }

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

    public double[] getFeatures() {
        return features;
    }

    public void setFeatures(double[] features) {
        this.features = features;
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
}
