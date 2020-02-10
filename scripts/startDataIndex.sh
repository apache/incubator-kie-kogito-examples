
echo "Script requires your maven project to be compiled"

DATA_INDEX_VERSION=0.4.0
PERSISTENCE_FOLDER=target/classes/persistence

[ ! -d ${PERSISTENCE_FOLDER} ] && echo "Persistence folder is missing. Make sure that your project was compiled" && exit 0

wget -nc http://repo2.maven.org/maven2/org/kie/kogito/data-index-service/${DATA_INDEX_VERSION}/data-index-service-${DATA_INDEX_VERSION}-runner.jar
cp -rf ${PERSISTENCE_FOLDER} persistence
java -jar  -Dkogito.protobuf.folder=`pwd`/persistence data-index-service-${DATA_INDEX_VERSION}-runner.jar

