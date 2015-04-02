# ProProMo2015
Baseline for Project in Probabilistic Models 2015

Build the docker:

    docker build -t tade/propromo github.com/anttitakalahti/ProProMo2015

The dockerfile includes build instructions for javac.

Run the docker:

    docker run -i -t tade/propromo

Once again the actual run command is given in the Dockerfile.
The -i stands for interactive, so you can simulate the client and send messages.
