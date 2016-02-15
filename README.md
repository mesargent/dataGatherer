# MyClassifier
This is the first component in MyClassifier. It's an Android application that gathers sensor data. It also allows a user to label the sensor data positive or negative, thus defining an activity (whatever's happening when the data is being labeled positive). The application then can use the second component, classifierapi, to train the data, producing a file of trained parameters that can be used with the third component, classifierandservicelibrary, to detect the activity in other Android applications. The application also displays an accuracy report (including precision, recall, F1, confusion matrix, ideal C, etc) after each training call. 

This app also can store sensor data in .csv format for data analysis. 

This is a working prototype: refactoring in process. 

More documentation coming soon!


