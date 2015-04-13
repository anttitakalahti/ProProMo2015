# ProProMo2015
Baseline for Project in Probabilistic Models 2015

Build the docker:

    docker build -t tade/propromo github.com/anttitakalahti/ProProMo2015

The dockerfile includes build instructions for javac.

Run the docker:

    docker run -it tade/propromo

Once again the actual run command is given in the Dockerfile.
The -i stands for interactive, so you can simulate the client and send messages.

To access your docker image:

    docker run -it tade/propromo /bin/bash

Ideas:
- see if already seen values have higher prob.
- see if each position has similar value prob. distribution.
- check the 5k pattern and see how early we can be sure we are seeing that pattern.