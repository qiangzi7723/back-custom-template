FROM java:8
ADD target/via-custom.jar app.jar
ENTRYPOINT ["java","-jar","-XX:MetaspaceSize=128m","-XX:MaxMetaspaceSize=128m","-Xms1024m","-Xmx1024m","-Xmn256m","-Xss256k","-XX:SurvivorRatio=8","app.jar"]
