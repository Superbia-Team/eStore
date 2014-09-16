#!/bin/bash

SOURCE="${BASH_SOURCE[0]}"
while [ -h "$SOURCE" ]; do # resolve $SOURCE until the file is no longer a symlink
  PRG="$( cd -P "$( dirname "$SOURCE" )" && pwd )"
  SOURCE="$(readlink "$SOURCE")"
  [[ $SOURCE != /* ]] && SOURCE="$PRG/$SOURCE" # if $SOURCE was a relative symlink, we need to resolve it relative to the path where the symlink file was located
done
PRG="$( cd -P "$( dirname "$SOURCE" )" && pwd )"

SOLR_SERVER_OUT="$PRG/output"
ROOT_PATH=$(dirname "$PRG")
ROOT_PATH=$(dirname "$ROOT_PATH")

echo "Root folder: $ROOT_PATH"

if [ -d $SOLR_SERVER_OUT ];
then
   echo "Use output folder: $SOLR_SERVER_OUT"
else
   echo "Create output folder: $SOLR_SERVER_OUT"
   mkdir "$SOLR_SERVER_OUT"
fi

cd "$SOLR_SERVER_OUT"

echo "Start server ..."
echo "Solr admin: http://localhost:8983/solr/"
java -Dsolr.solr.home="$ROOT_PATH/yastore-core/yastore-services-search/src/main/resources/solr" -Dsolr.data.dir="$SOLR_SERVER_OUT/data" -jar ../start.jar
