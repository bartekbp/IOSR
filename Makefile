RELEASE_NAME = "kaflog-0.1"

all: deploy

package_kaflog:
	mvn -f Kaflog/kaflog/pom.xml clean install
	mvn -f Kaflog/hadoopconsumer/pom.xml package assembly:single
	mvn -f Kaflog/hiveviewcreator/pom.xml package assembly:single
	mvn -f Kaflog/logproducer/pom.xml package assembly:single
	mvn -f Kaflog/stormconsumer/pom.xml package assembly:single
	mvn -f Kaflog/mastermonitoring/pom.xml clean package

deploy: package_kaflog
	rm -rf ${RELEASE_NAME}
	mkdir ${RELEASE_NAME}
	cp -R bin ${RELEASE_NAME}/
	mkdir -p ${RELEASE_NAME}/lib
	find Kaflog -name *-with-dependencies.jar | xargs cp -t ${RELEASE_NAME}/lib/
	cp Kaflog/mastermonitoring/target/kaflog-mastermonitoring-0.1.war ${RELEASE_NAME}/lib/
	rm -rf vagrant/files/${RELEASE_NAME}
	mv ${RELEASE_NAME} vagrant/files/${RELEASE_NAME}

spring: 
	mvn -f Kaflog/mastermonitoring/pom.xml clean package
	cp -f Kaflog/mastermonitoring/target/kaflog-mastermonitoring-0.1.war vagrant/files/${RELEASE_NAME}/lib/