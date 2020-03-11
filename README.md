# ALFIS BPM

### Create project
```
mvn io.quarkus:quarkus-maven-plugin:1.0.0.CR2:create \
    -DprojectGroupId=it.tyto.arcadia.alfis \
    -DprojectArtifactId=alfis-bpm \
    -Dextensions="oidc, keycloak-authorization, resteasy-jsonb, kogito, io.quarkus:quarkus-infinispan-client"
```

### Download dependencies
```
./mvnw clean install
```
