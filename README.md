# ![RealWorld Example App using Java and Graphql](example-logo.png)

> ### Spring boot + Graphql codebase containing real world examples (CRUD, auth, advanced patterns, etc) that adheres to the [RealWorld](https://github.com/gothinkster/realworld-example-apps) spec and API.


# Database

It uses Mongo database

# Getting started

You need Java installed.

    mvn jetty:run
    open http://localhost:8080

# Debug in vscode
0. 添加.vscode/launch.json配置
```json
  "configurations": [
    {
      "type": "java",
      "name": "Debug (Attach)",
      "request": "attach",
      "hostName": "localhost",
      "port": 8000
    }
  ]
```
1. 添加maven参数
```sh
export MAVEN_OPTS="-Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,address=8000,server=y,suspend=n"
```

2. 运行程序
```sh
mvn jetty:run
```

3. 启动Debug F5


