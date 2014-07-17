all:
	mvn clean package -DskipTests

test:
	mvn clean test

clean:
	mvn clean
	rm -f src/test/resources/*plot.tiff
	rm -f src/test/resources/*out.tiff
