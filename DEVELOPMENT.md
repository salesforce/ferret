
## Development setup
* Install maven.
* JDK based on java version in pom.xml.
* Allow annotation-processor to work on your [ide](https://immutables.github.io/apt.html)

## Development
### Native image
* Install graalvm JDK based on java version (8 currently) in pom.xml. If already using another jdk Version locally, I recommend to work with [jenv](https://github.com/jenv/jenv) cli.
* Install graalvm native-image. ```gu install native-image```
* Compile to native-image ```mvn package -Dpackaging=native-image```

### Debug with jar
* ```java -cp ferret-0.1.jar com.datorama.FerretCommand``` in target directory after compile

### Working with ferret
* download the correct executable for your OS
    * Mac - symbolic link for ease of usage ```ln -s <full path to exectuable> /usr/local/bin/ferret```


## Production
### compile
* Compile to native-image ```mvn package -Dpackaging=native-image```
* ```tar -zcvf ferret-mac-{version}.tar.gz target/ferret```
* get sha256 ```shasum -a 256 ferret-mac-{version}.tar.gz``` save it for homebrew
