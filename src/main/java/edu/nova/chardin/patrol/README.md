The main class is edu.nova.chardin.patrol.App It sets up the experiment class object, with the associated Scenario, Match, and Game objects.

An Experiment object is composed of Scenario objects. A Scenario object is composed of Match objects, and a Match object is composed of Game objects. 

An Experiment is run via an ExperimentRunner objects, which runs the Games concurrently and can take advantage of many, many CPU cores.

The output is a csv file in the current directory with the experiment results.

There is a required command line option of "--sampling". I suggest you set it to "1" or else the experiment will take a VERY long time to run.

The project can be built using maven with "mvn install"

To run the experiment, execute "java --jar target/chardin-nsu-patrol-1.0-SNAPSHOT.jar --sampling 1"
