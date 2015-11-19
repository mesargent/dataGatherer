package com.detroitteatime.datagatherer;

/**
 * Created by marksargent on 10/18/15.
 */
public class LogisticPredictor extends Predictor{

    private double yIntercept;

    public double sigmoid(double pTransposeF) throws IllegalArgumentException{

        return 1 / (1 + Math.exp(- pTransposeF));

    }

    public double getyIntercept() {
        return yIntercept;
    }

    public void setyIntercept(double yIntercept) {
        this.yIntercept = yIntercept;
    }
}
