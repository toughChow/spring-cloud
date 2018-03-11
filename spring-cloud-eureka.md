# Spring Cloud Eureka

Spring Cloud Eureka是Spring Cloud Netflix项目下的服务治理模块。Spring Cloud Netflix项目是Spring Cloud的子项目之一，主要是对Netflix公司的一系列开源产品的包装，它为Spring Boot应用提供了自配置的Netflix OSS整合。它主要提供的模块包括：服务发现（Eureka），断路器（Hystrix），智能路由（Zuul），客户端负载均衡（Ribbon）等。

## Spring Cloud Eureka实现服务治理

#### 创建“服务注册中心”

创建一个1.5.10版本的Sptring Boot工程（昨天引入2.0.0创建失败，不知道是网络原因或者什么，再此tip），并在pom.xml引入需要的依赖。

```xml
	<parent>
		<groupId>org.springframework.boot</groupId>
		<artifactId>spring-boot-starter-parent</artifactId>
		<version>1.5.10.RELEASE</version>
		<relativePath/> <!-- lookup parent from repository -->
	</parent>

	<properties>
		<project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
		<project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
		<java.version>1.8</java.version>
		<spring-cloud.version>Edgware.SR2</spring-cloud.version>
	</properties>

	<dependencies>
		<dependency>
			<groupId>org.springframework.cloud</groupId>
			<artifactId>spring-cloud-starter-eureka-server</artifactId>
		</dependency>

		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-test</artifactId>
			<scope>test</scope>
		</dependency>
	</dependencies>

	<dependencyManagement>
		<dependencies>
			<dependency>
				<groupId>org.springframework.cloud</groupId>
				<artifactId>spring-cloud-dependencies</artifactId>
				<version>${spring-cloud.version}</version>
				<type>pom</type>
				<scope>import</scope>
			</dependency>
		</dependencies>
	</dependencyManagement>

	<build>
		<plugins>
			<plugin>
				<groupId>org.springframework.boot</groupId>
				<artifactId>spring-boot-maven-plugin</artifactId>
			</plugin>
		</plugins>
	</build>
```

#### 通过@EnableEurekaServer注解启动一个服务注册中心

```java
@EnableEurekaServer
@SpringBootApplication
public class SpringCloudEurekaApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringCloudEurekaApplication.class, args);
	}
}
```

#### 配置服务注册中心

禁用eureka的客户端注册行为

```xml
server.apllication.name=eureka-server-tough
server.port=1001

eureka.insance.hostname=localhost
#   此处设置为false禁止eureka客户端注册行为
eureka.client.register-with-eureka=false
eureka.client.fetch-registry=false
```

#### 启动工程

输入http://localhost:1001/，即可查看。

------

## 创建“服务提供方”

#### 配置pom.xml文件

创建一个Spring Boot工程，命名为eureka-client，在pom.xml文件中配置：

```xml
	<name>eureka-server</name>
	<description>Demo project for Spring Boot</description>

	<parent>
		<groupId>org.springframework.boot</groupId>
		<artifactId>spring-boot-starter-parent</artifactId>
		<version>1.5.10.RELEASE</version>
		<relativePath/> <!-- lookup parent from repository -->
	</parent>

	<properties>
		<project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
		<project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
		<java.version>1.8</java.version>
		<spring-cloud.version>Edgware.SR2</spring-cloud.version>
	</properties>

	<dependencies>
		<dependency>
			<groupId>org.springframework.cloud</groupId>
			<artifactId>spring-cloud-starter-eureka-server</artifactId>
		</dependency>

		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-test</artifactId>
			<scope>test</scope>
		</dependency>
	</dependencies>

	<dependencyManagement>
		<dependencies>
			<dependency>
				<groupId>org.springframework.cloud</groupId>
				<artifactId>spring-cloud-dependencies</artifactId>
				<version>${spring-cloud.version}</version>
				<type>pom</type>
				<scope>import</scope>
			</dependency>
		</dependencies>
	</dependencyManagement>

	<build>
		<plugins>
			<plugin>
				<groupId>org.springframework.boot</groupId>
				<artifactId>spring-boot-maven-plugin</artifactId>
			</plugin>
		</plugins>
	</build>
```

#### 实现请求处理接口

```java
@RestController
public class DcController {

    @Autowired
    DiscoveryClient discoveryClient;

    @GetMapping("/dc")
    public String dc(){
        String services = "Services : " + discoveryClient.getServices();
        System.out.println(services);
        return services;
    }
}
```

#### 添加@EnableDiscoveryClient注解

```
@EnableDiscoveryClient
@SpringBootApplication
public class EurekaClientApplication {

	public static void main(String[] args) {
		SpringApplication.run(EurekaClientApplication.class, args);
	}
}
```

#### 配置application.properties

通过spring.application.name属性，可以指定微服务的名称以便后续调用的时候只需要使用该名称就可以进行服务的访问。eureka.client.serviceUrl.defaultZone属性对应服务注册中心的配置内容，制定服务中心的位置。

```xml
spring.application.name=eureka-client-tough
server.port=2001
eureka.client.serviceUrl.defaultZone=http://localhost:1001/eureka/
```

#### 启动工程

启动工程再次访问http://localhost:1001/，可以发现服务注册成功。

通过访问http://localhost:2001/dc可以得到输出结果为：Services : [eureka-client-tough]



#### tips：

将eureka注册到spring-cloud-consul,windows需要装exe文件并配置环境方可访问。