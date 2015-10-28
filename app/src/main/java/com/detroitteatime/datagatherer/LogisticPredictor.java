package com.detroitteatime.datagatherer;

/**
 * Created by marksargent on 10/18/15.
 */
public class LogisticPredictor extends Predictor{

    private double yIntercept;

    @Override
    public double predictProb(double[] features) {
        if(parameters.length != features.length) throw new IllegalArgumentException("Feature and parameter arrays must be the same length.");
        double pTX = 0;

        for(int i = 0; i < features.length; i++){
            pTX += parameters[i] * features[i];
        }

        return sigmoid(pTX + yIntercept);
    }


    @Override
    public boolean predict(double[] features) {
        return predictProb(features) > 0.5;
    }

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
