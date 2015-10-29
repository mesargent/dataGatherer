# MyClassifier
So far completed: Sensor-data-gathering prototype

To do: Everything else 

My Classifier
Android Machine Learning Development Tool

● A tool to help researchers and developers to visualize
and experiment with classification using phone sensor
data, all in the same app </ br>
  ■ Classification: takes a set of features associated
  with a condition and finds a model that predicts
  when the condition obtains
  ■ User chooses condition to test
  ■ Used for preliminary research

The Tool: Data Gathering
  ● Generates, stores, and formats data
  from selected phone sensors
    ○ Accelerometer, gyroscope, speed,
    light, and others
    ○ Data stored in SQLite database,
    CSV formatted files
  ● Allows boolean labeling of data for
  conditions a user chooses
 
The Tool: Machine Learning
  ● Can divide data into
  training/cross-validation/test sets
  ● Allows selection of machine
  learning algorithms on which to
  train the data
  ● Measures accuracy of predictions
  ● Exports results into a report
    ○ Model parameter values
    ○ Cost before/after
    ○ Accuracy, precision/recall
  
Machine Learning Algorithms
  ● Supervised learning/Classification
  ● Developers can code their own machine
  learning algorithms by implementing an
  interface (MLAlgorithm)
  ● A handful of ready-made implementations
  of MLAlgorithm will be provided
    ○ SVM, KNN, Random Forest, Naive
    Bayes, Neural Net?, . . .
  ■ Weka Machine Learning library
