敏捷集成培训作业

一、准备基础依赖包：
At the command line, build the parent project:

mvn clean install

Build the artifacts project:

mvn clean install


二、安装integration test 的ws
启动fuse on karaf
安装：
>install mvn:com.customer.app/integration-test-server/1.0-SNAPSHOT
>install mvn:com.customer.app/artifacts/1.0-SNAPSHOT
>start [bundle-id for integration-test-server]
>start [bundle-id for artifacts]
>list

访问 http://localhost:8181/cxf/PersonEJBService/PersonEJB?wsdl

