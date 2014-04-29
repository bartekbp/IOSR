RELEASE_NAME = "kaflog-0.1"

all: kaflog deploy

kaflog:
	cd Kaflog && mvn package

deploy:
	rm -rf ${RELEASE_NAME}
	mkdir ${RELEASE_NAME}
	cp -R bin ${RELEASE_NAME}/
	cp -R Kaflog/lib ${RELEASE_NAME}/
	cp Kaflog/target/*.jar ${RELEASE_NAME}/lib/
	rm -rf vagrant/files/${RELEASE_NAME}
	mv ${RELEASE_NAME} vagrant/files/${RELEASE_NAME}
