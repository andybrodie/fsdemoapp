@echo off

rem Builds everything ready for the other batch/scripts to use.
rem Requires Maven 3.x and a JDK available.

mvn clean dependency:copy-dependencies package
