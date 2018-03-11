# Spring Cloud Feign

Spring Cloud Feign是一套基于Netflix Feign实现的声明式服务调用客户端。

它使得编写Web服务客户端变得更加简单。

我们只需要通过创建接口并用注解来配置它既可完成对Web服务接口的绑定。

它具备可插拔的注解支持，包括Feign注解、JAX-RS注解。它也支持可插拔的编码器和解码器。

Spring Cloud Feign还扩展了对Spring MVC注解的支持，同时还整合了Ribbon和Eureka来提供均衡负载的HTTP客户端实现。

## 创建eureka-consumer-ribbon

这里使用eureka-server作为服务注册中心，eureka-client作为服务提供者作为基础，创建eureka-consumer-ribbon。导入pom.xml文件：

```xml
	<name>eureka-consumer-ribbon</name>
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
            <artifactId>spring-cloud-starter-feign</artifactId>
    	</dependency>
		<dependency>
			<groupId>org.springframework.cloud</groupId>
			<artifactId>spring-cloud-starter-eureka</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-web</artifactId>
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
```

## 配置application.properties文件

指定注册中心地址

```xml
spring.application.name=eureka-consumer-tough
server.port=2101

eureka.client.serviceUrl.defaultZone=http://localhost:1001/eureka/
```

## 创建主类，添加@EnableFeignClients注解

通过`@EnableFeignClients`注解开启扫描Spring Cloud Feign客户端的功能：

```java
@EnableFeignClients
@EnableDiscoveryClient
@SpringBootApplication
public class EurekaConsumerApplication {

	public static void main(String[] args) {
		SpringApplication.run(EurekaConsumerApplication.class, args);
	}
}
```

## 创建一个Feign客户端接口定义

使用`@FeignClient`注解来指定这个接口所要调用的服务名称，接口中定义各个函数使用Spring MVC的注解就可以来绑定服务提供方的REST接口

```
@FeignClient("eureka-client-tough")
public interface DcClient {

    @GetMapping("/dc")
    String consumer();

}
```

## 通过定义的feign客户端来调用服务提供方的接口

通过`@FeignClient`定义的接口来统一的声明所需要依赖的微服务接口，而在具体使用的时候就跟调用本地方法一点的进行调用即可。

由于Feign是给予Ribbon实现的，所以它自带了客户端负载均衡功能。也可以通过Ribbon的IRule进行策略扩展。

此外，Feign还整合的Hystrix来实现服务的容错保护，在Dalston版本中，Feign的Hystrix默认是关闭的。

```java
@RestController
public class DcController {

    @Autowired
    DcClient dcClient;

    @GetMapping("/consumer")
    public String dc(){
        return dcClient.consumer();
    }
}
```

启动三个工程，访问http://localhost:2101/comsumer来跟踪观察eureka-consumer服务是如何消费eureka-client的/dc接口的。