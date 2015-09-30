#!/bin/sh

# Builds everything ready for the other batch/scripts to use.
# Requires Maven 3.x and a JDK available.

mvn clean dependency:copy-dependencies package
