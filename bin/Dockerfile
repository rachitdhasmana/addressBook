FROM openjdk:21
EXPOSE 8080

## install `tar`
RUN \
  microdnf install -y tar tzdata-java \
  && microdnf clean all

## install `mvn`
RUN \
	curl https://dlcdn.apache.org/maven/maven-3/3.9.4/binaries/apache-maven-3.9.4-bin.tar.gz \
  | tar -C /opt -xzf - \
  && ln -s /opt/apache-maven-3.9.4 /opt/maven
ENV M2_HOME=/opt/maven MAVEN_HOME=/opt/maven
ENV PATH=/opt/maven/bin:$PATH

COPY . addressBook
RUN cd addressBook && mvn install && cp target/addressBook-0.0.1.jar /app.jar

ENTRYPOINT ["java","-jar","/app.jar"]