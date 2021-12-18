FROM dmissud/ubuntupi4gpio:0.1-SNAPSHOT

CMD mkdir /app
COPY target/myRestGpio-0.0.1-SNAPSHOT.jar /app/myRestApp.jar

EXPOSE 9095

CMD java -jar /app/myRestApp.jar
