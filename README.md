Welcome to the ferret wiki! the new way to run local pipelines on your local computer. From small pipelines to complex pipelines.

Ferret is a Command-line application that executes Ferret pipelines written in YAML, there is **no** ferret server, it uses your organization git repositories to fetch the pipelines or files that you told it to get, to be used locally for you.

## Prerequisites

- [Brew](https://brew.sh/)
- [Github Token](https://github.com/salesforce/ferret/wiki/Github-Token) (if you use private repository)


## Getting Started


### Installation
Prepare your Github user and token, and run the following commands:

```
$ brew tap datorama/ferret
$ brew install ferret
$ ferret init
$ ferret repository --owner=datorama --repository=ferret-common --branch=master
$ ferret credentials --username=<GITHUB_USER> --token=<GITHUB_TOKEN>
```

**Note:** If it fails or asks for a user/password than stop (<kbd>Ctrl</kbd> + <kbd>C</kbd>),  try instead:

```
brew untap salesforce/homebrew-ferret
brew tap datorama/ferret git@github.com:salesforce/homebrew-ferret.git
```



### Usage

List all the available pipelines, and then choose one and run it:

```
$ ferret pipelines

Pulling datorama/ferret-common...
Finished pulling datorama/ferret-common
To run one of those pipelines, type: ferret setup --pipeline <pipeline-name>
Pipelines from common repository (in pipelines directory):
...
...
pipeline: redis-local | file in repository: redis-local.yaml
pipeline: rabbitmq-local | file in repository: rabbitmq-local.yaml
pipeline: lite-connect | file in repository: lite-connect.yaml
pipeline: hybriddb-local | file in repository: hybriddb-local.yaml
pipeline: dato/onboarding | file in repository: dato/onboarding.yaml
...
...

# Run your first pipeline:
$ ferret setup --pipeline dato/onboarding
```

That's it!  Read further to learn about Ferret properties, using repos etc.



## Ferret Command-line commands

### Credentials command

**Credentials settings for different version control (github) that need authentication.**

`ferret credentials [-t=<token>] [-u=<username>] [COMMAND] `

`-t, --token=<token>   Token for github`

`-u, --username=<username> username of github`

### Initialize command

One time command to setup ferret file system locally

### Pipelines command

Information about all pipelines from the common repository that was configured by you.

To configure common-repository use repository command to set location and use [credentials command](https://github.com/salesforce/ferret#credentials-command) to set token and user


By typing `ferret pipelines` you will get a list of all ferret pipelines that are in your common repository.

### Properties command

Information about all properties from the common repository that was configured by you.

By typing `ferret properties` you will see a list of all properties that are in the common repository.

### Repository command

Settings for the common repository to get all ferret common pipeline and properties from VCS (git, etc.).
If the repository need credentials to access configure it with [credentials command](https://github.com/salesforce/ferret#credentials-command)

`-b, --branch=<branch>   the repository branch name`

`-g, --get               show current repository settings.`

`-o, --owner=<owner>     owner name of the repository`

`-r, --repository=<repository> the repository name`

### Setup command

`ferret setup [-f=yaml file] [-p=<pipeline>] [-s=<stage>] [COMMAND] runs setup part of stages in a Ferret pipeline Yaml.`

`-a, --arg, --argument=<String=String> arguments to pass to the pipeline to use. example: --argument key=value`

`-f, --file=yaml file   Yaml file.`

`-p, --pipeline=<pipeline>  pipeline from repository to run.`

`-s, --stage=<stage>    Stage to run.`

### User command

User properties settings set or get current user properties.

These properties are unique properties whose value only belongs to the running computer (passwords, unique paths, etc.).


`-g, --get   show all properties currently stored in user properties.`


`-P, -p, --property=<String=String> store new properties for user. example: --property key=value`

### Teardown command

`ferret teardown [-f=yaml file] [-p=<pipeline>] [-s=<stage>]`  runs teardown part of stages in a Ferret pipeline Yaml.

`-a, --arg, --argument=<String=String> arguments to pass to the pipeline to use. example: --argument key=value`

`-f, --file=yaml file   Yaml file.`

`-p, --pipeline=<pipeline>  pipeline from repository to run.`

`-s, --stage=<stage>    Stage to run.`

### Arguments command

Usage: `ferret arguments [-f=yaml file] [-p=<pipeline>]`
shows arguments list of specific pipeline or file

` -f, --file=yaml file   Yaml file.`

`-p, --pipeline=<pipeline>   pipeline from repository to run.`


## How to create your own pipeline?

Ferret read the YAML pipeline with its own syntax, so let's learn how you write them.

Example of selenoid local pipeline that will work for all:
```
stages:
  pull selenoid images:
    description: pulling needed selenoid images, server,ui,video-recorder and the broweser chrome
    setup:
      -
        command: docker pull aerokube/selenoid:1.9.2
      -
        command: docker pull aerokube/selenoid-ui:1.9.0
      -
        command: docker pull selenoid/video-recorder:latest-release
      -
        command: docker pull selenoid/vnc_chrome:83.0

  configure selenoid files:
    description:   setting needed files, notice that need to give permission with password.
    directory: selenoid/video
    inputs:
      -
        key: browsers
        remote:
          owner: datorama
          repository: ferret
          branch: master
          file: /src/test/resources/browsers.json
    setup:
      -
        command: sudo chmod 777 {{user.home}}/selenoid
      -
        command: sudo chmod 777 {{user.home}}/selenoid/video
      -
        command: cp {{input.browsers}} {{user.home}}/selenoid/browsers.json

  run selenoid:
    description: run selenoid server and ui
    setup:
      -
        command: docker run -d --name selenoid --restart always -p 8081:4444 -v /var/run/docker.sock:/var/run/docker.sock -v {{user.home}}/selenoid:/etc/selenoid/:ro -v {{user.home}}/selenoid/video:/opt/selenoid/video -e OVERRIDE_VIDEO_OUTPUT_DIR={{user.home}}/selenoid/video/ aerokube/selenoid:1.9.2 -limit 2 -timeout 5m0s -service-startup-timeout 2m0s
      -
        command: docker run -d --name selenoid-ui --link selenoid -p 8080:8080  aerokube/selenoid-ui:1.9.0 --selenoid-uri=http://selenoid:4444
      -
        command: open http://localhost:8080
```


Each ferret pipeline will start with
```stages:```

then you start writing each stage on its own
```
stages:
  pull selenoid images:
    description: pulling needed selenoid images, server,ui,video-recorder and the broweser chrome
    setup:
      -
        command: docker pull aerokube/selenoid:1.9.2
      -
        command: docker pull aerokube/selenoid-ui:1.9.0
      -
        command: docker pull selenoid/video-recorder:latest-release
      -
        command: docker pull selenoid/vnc_chrome:83.0
```

```pull selenoid images``` is a generic name I gave to this step. you can name the stage as you see fit (we recommend a name in the context)

```description:``` to add more description of what you doing in this stage (optional)

```
setup:
  -
   command: docker pull aerokube/selenoid:1.9.2
``` 
here is where the magic happens, you describe what commands you want to do, for example, we did a docker CLI pull command.
when you run in the terminal `ferret setup` command, the commands executed are under the setup section.

### Directory

```
directory: selenoid/video
```
directory field is where to execute the command for example: in a terminal, you will execute `pwd` and it will give you the current directory you located.
Ferret pipeline is executed in the user home directory by default.

```
directory: downloads
stage:
  check directory stage:
    directory: video
    setup:
      - 
        command: pwd
        directory: movies
      - 
        command: pwd
```

In the example above you will see you can define the directory in 3 levels at the pipeline level, at stage level, and the command level.
Because we defined at the pipeline level so all stages will run under `$HOME/downloads directory`.
When we defined at the stage level of `check directory stage` all the commands in that stage will run `$HOME/downloads/video`.
When we defined at the command level only this command will run in `$HOME/downloads/video/movies`.
If those directories do not exist they will be created.
If you give the absolute path of the directory it will run in that directory as it is and will not use it relatively. (example `/downloads` will give `/downloads` directory)


### Input

use input when you want to receive a value dynamically, by file from remote (git repository) value will be the absolute path of the file, by request input from the user, or by running command and receive its output as the input.

``` 
inputs:
  -
    key: name
    request: please give me your name
    defaultValue: pp
stages: 
 configure selenoid files:
    description: setting needed files, notice that needs to give permission with a password.
    directory: selenoid/video
    inputs:
      - key: browsers
        remote:
          owner: datorama
          repository: ferret
          branch: master
          file: /src/test/resources/browsers.json
    setup:
      - command: cp {{input.browsers}} {{user.home}}/selenoid/browsers.json

 check input dynamic:
    description: checking input dynamically
    inputs:
      -
        key: wow
        command: git --version
    setup:
      -
        command: echo {{input.wow}}
 check request input:
    description: checking request input
    setup:
      -
        command: echo {{input.name}}
```

input can be written in two levels, pipeline level which means it will be shared by all stages, or by stage level, and only the stage will use it.

Each input needs a key (its name) and (command/remote/request) examples above.

remote need credentials (use the credentials command) if it's a private repository.

you use the input with double curly brackets `{{}}` and input. prefix and the key name after for example `{{input.name}}`.

### When (if statement)

you use when you want to add a condition to execute the command in setup or teardown.

examples:

```
stages:
  check when conditions:
    description: check multi conditions
    when:
      operator: or
      conditions:
           -
            operator: equals
            command: git --version
            compareTo: git version 2.28.0www
           -
            operator:  exit_code_equals
            command: git --version
            compareTo: 0

    setup:
      -
        command: docker pull aerokube/selenoid:1.9.2


  check when condition:
    description: checking one condition
    when:
      condition:
        operator: equals
        command: git --version
        compareTo: git version 2.28.0
    setup:
      -
        command: docker pull aerokube/selenoid:1.9.2

  check docker is up:
    description: checking timeout and stop script if failed when
    when:
      timeout: 20s
      retryInterval: 5s
      fail: true
      condition:
        operator: contains
        command: docker version
        compareTo: "Server: Docker Engine"
    setup:
         -
           command: echo "docker is up"
```

When uses a condition or conditions (with and/or relation between each condition). if the condition or conditions return true it will execute the setup or teardown.

you can use timeout and retryInterval to check for a condition in a loop (minutes or seconds example: 60s or 30m).

you can use fail (default is false) to stop the pipeline if the condition returns false.


### Apply

Apply is used to apply a pipeline to be run in this stage, this allows you to use pipelines from other sources as part of your pipeline.

Example:

```
stages:
  setup docker:
    description: setup selenoid local from remote
    setup:
      - apply:
          remote:
            owner: datorama
            repository: ferret
            branch: master
            file: /src/test/resources/selenoid-local-remote.yaml

  teardown:
    description: remove docker with apply
    setup:
      - apply:
            file: {{script.directory.path}}/teardown-docker-containers.yaml

  apply pipeline:
    description: applying pipeline from common repository
    teardown:
      - apply:
          pipeline: rabbitmq-local
``` 

applying a pipeline can be fetched from local by file, from a repository by remote, or by the common repository by pipeline.

notice it is applied under setup or teardown which mean it will fetch the related part of the specific pipeline (setup/teardown)

## Properties

properties are used to share constants in your different pipelines, you can use user properties, special properties, or common repository properties.

### User properties

unique properties that are only related to this specific user on the local computer for example password, unique paths, etc.

there are two ways to set a user property with the user command `ferret user` or in the YAML pipeline with properties field.

Example in the pipeline:

```
properties:
  -
    key: wow
    description: checking how wow it is
  -
    key: another
    description: checking multi values
    values:
      - one
      - two
      - three
```

Ferret will check if you have the property (by the key) if do not have the property it will output the user a request to receive the relevant value for this property, use description to give the user the information of what the value needed to be.

when you have a set of values you want the user to choose from using the `values` field. it will only allow putting one of those values.

To use user properties in the pipeline you write the prefix user. and the key of property inside double curly brackets for example `{{user.another}}`

### Special properties

special are unique properties to ease the ability to fetch relevant paths relative to a specific location needed.

currently, Ferret support:

`{{git.root.directory}}` this property will give you the root directory of the git repository the pipeline file is located in.

`{{script.directory.path}}` this property will give you the directory that the pipeline file is located in.

`{{user.home}}` this property will give you the user home directory `$HOME`.

If you think you need more special properties to allow ease of writing pipelines let us know and will add support.

### Common repository properties

These properties are located in the common repository under the properties directory, this allows you to share commonly used properties with all pipelines.

you can use as many properties files as you want under the directory, to fetch the correct property in your pipeline you write the property file name in the prefix and the property key after it.

Example:

you have `common.properties` file with `google.url` property key, to fetch the value in your pipeline you will write `{{common.google.url}}`



## Common repository

repository to share pipelines and properties between all developers.

to set/get which common repository type `ferret repository` command.

to fetch the pipelines in the repository type `ferret pipelines` command.

to fetch the properties in the repository type `ferret properties` command.

if the repository is private use `ferret credentials` to give the credentials that are needed to clone/pull from the repository.

the common repository must have pipelines and properties directories under the root directory of the repository.

to `ferret setup --pipeline pipeline-name` or `ferret teardown --pipeline pipeline-name` from a pipeline (from the common-repository) just use the pipeline name (use `ferret pipelines`), the pipeline name is also used when applying those pipelines inside other pipelines.



## Arguments

Arguments are unique values of the specific pipeline.

example of how setting arguments in a pipeline

```
arguments:
  -
    key: check.arg
    description: some check
    defaultValue: value
stages:
  check arguments live:
    description: checking injecting arguments correctly
    setup:
      -
        command: echo {{argument.check.arg}}
```

How to set arguments for a pipeline. (example below)

```
arguments:
  -
    key: check.arg
    description: some check
    defaultValue: value
```

`defaultValue` field is mandatory.

To use an argument in the pipeline you use `{{}}` with argument prefix `{{argument.check.arg}}` (from the example above)

```
 command: echo {{argument.check.arg}}
```

To call a pipeline with arguments, you add an arguments field to apply

Example:

```
stages:
  use argument in the pipeline:
    setup:
      -
        apply:
          file: {{script.directory.path}}/arguments.yaml
          arguments:
            -
              key: check.arg
              value: popo
```




