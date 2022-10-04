l
## Development setup
* Install maven.
* JDK based on java version in pom.xml.
* Allow annotation-processor to work on your [ide](https://immutables.github.io/apt.html)
## Running the application in dev mode

You can run your application in dev mode that enables live coding using:
```shell script
./mvnw compile quarkus:dev
```

## Development
### Native image
* Install graalvm JDK based on java version (11 currently) in pom.xml. If already using another jdk Version locally, I recommend to work with [jenv](https://github.com/jenv/jenv) cli.
* Install graalvm native-image. ```gu install native-image```
* Compile to native-image ```mvn package -Pnative```
* You can then execute your native executable with: `./target/*-runner`

### Debug with jar
```shell script
./mvnw package -Dquarkus.package.type=uber-jar
```
* the application, packaged as an _über-jar_, is now runnable using `java -jar target/*-runner.jar`.


## Related Guides

- Picocli ([guide](https://quarkus.io/guides/picocli)): Develop command line applications with Picocli

## Provided Code

### Picocli Example

Hello and goodbye are civilization fundamentals. Let's not forget it with this example picocli application by changing the <code>command</code> and <code>parameters</code>.

[Related guide section...](https://quarkus.io/guides/picocli#command-line-application-with-multiple-commands)

Also for picocli applications the dev mode is supported. When running dev mode, the picocli application is executed and on press of the Enter key, is restarted.

As picocli applications will often require arguments to be passed on the commandline, this is also possible in dev mode via:
```shell script
./mvnw compile quarkus:dev -Dquarkus.args='Quarky'
```


### Working with ferret
* download the correct executable for your OS
    * Mac - symbolic link for ease of usage ```ln -s <full path to exectuable> /usr/local/bin/ferret```


## Production
### compile
* Compile to native-image ```mvn package -Dpackaging=native-image```
* ```tar -zcvf ferret-mac-{version}.tar.gz target/ferret```
* get sha256 ```shasum -a 256 ferret-mac-{version}.tar.gz``` save it for homebrew
