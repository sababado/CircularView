#!/bin/bash
# This script initiates the Gradle publishing task when pushes to snapshot branch occur.
# NOTE: Travis-CI can only publish SNAPSHOT versions.

if [ "$TRAVIS_BRANCH" == "snapshot" ]; then

  echo -e "Starting publish to Sonatype...\n"

  ./gradlew clean lib:build lib:uploadArchives -PNEXUS_USERNAME="${NEXUS_USERNAME}" -PNEXUS_PASSWORD="${NEXUS_PASSWORD}"
  RETVAL=$?

  if [ $RETVAL -eq 0 ]; then
    echo 'Completed publish!'
  else
    echo 'Publish failed.'
    return 1
  fi

fi