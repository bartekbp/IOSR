RELEASE_NAME = "kaflog-0.1"

all: deploy

package_kaflog: ${shell find Kaflog/src -type f} Kaflog/pom.xml
	mvn -f Kaflog clean compile assembly:single

deploy: package_kaflog
	rm -rf ${RELEASE_NAME}
	mkdir ${RELEASE_NAME}
	cp -R bin ${RELEASE_NAME}/
	mkdir -p ${RELEASE_NAME}/lib
	cp Kaflog/target/*.jar ${RELEASE_NAME}/lib/
	rm -rf vagrant/files/${RELEASE_NAME}
	mv ${RELEASE_NAME} vagrant/files/${RELEASE_NAME}
