#!/bin/bash

# args
NEW_VERSION=$1


POM_FILES=(
  "./core/chaintest-core-java/pom.xml"
  "./plugins/chaintest-cucumber-jvm/pom.xml"
  "./plugins/chaintest-junit5/pom.xml"
  "./plugins/chaintest-testng/pom.xml"
  "./examples/chaintest-cucumber-jvm-example/pom.xml"
  "./examples/chaintest-junit5-example/pom.xml"
  "./examples/chaintest-testng-example/pom.xml"
)

update_versions() {
  local new_version=$1
  if [ -z "$new_version" ]; then
    echo "No version provided to update."
    exit 1
  fi

  for POM_FILE in "${POM_FILES[@]}"; do
    sed -i '' "1,/<version>[^<]*<\/version>/s/<version>[^<]*<\/version>/<version>$new_version<\/version>/" "$POM_FILE"
    echo "Updated version to $new_version in $POM_FILE"
  done
}

update_versions $NEW_VERSION
