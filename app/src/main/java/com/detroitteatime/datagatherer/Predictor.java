package com.detroitteatime.datagatherer;

/**
 * Created by marksargent on 10/18/15.
 */
public abstract class Predictor {
    private String category;
    private String name;
    private String model; //JSON string
    private String parameters; //JSON string

    public Predictor(String name, String category){
        this.name = name;
        this.category = category;
    }

    public Predictor(){};

    public abstract double predictProb(int[] parameters, int[] features, int yIntercept);

    public abstract boolean predict(int[] parameters, int[] features, int yIntercept);

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

    public String getParameters() {
        return parameters;
    }

    public void setParameters(String parameters) {
        this.parameters = parameters;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
