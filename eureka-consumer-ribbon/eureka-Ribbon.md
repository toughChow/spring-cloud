# Spring Cloud Ribbon

Spring Cloud Ribbon是基于Netflix Ribbon实现的一套客户端负载均衡的工具。

它是一个基于HTTP和TCP的客户端负载均衡器。

它可以通过在客户端中配置ribbonServerList来设置服务端列表去轮询访问以达到均衡负载的作用。

当Ribbon与Eureka联合使用时，ribbonServerList会被DiscoveryEnabledNIWSServerList重写，扩展成从Eureka注册中心中获取服务实例列表。

同时它也会用NIWSDiscoveryPing来取代IPing，它将职责委托给Eureka来确定服务端是否已经启动。

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
            <artifactId>spring-cloud-starter-ribbon</artifactId>
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

## 创建主类，为RestTemplate添加@LoadBalanced注解

初始化RestTemplate，用来真正发起REST请求。并使用@EnableDiscoveryClient注解将当前应用加入到服务治理体系中。

```java
@EnableDiscoveryClient
@SpringBootApplication
public class EurekaConsumerApplication {

	@Bean
    @LoadBalanced
	public RestTemplate restTemplate(){
		return new RestTemplate();
	}

	public static void main(String[] args) {
		SpringApplication.run(EurekaConsumerApplication.class, args);
	}
}
```

## 创建消费eureka-client提供接口的接口

这里直接通过RestTemplate发起请求。

使用RestTemplate第一个参数直接采用了服务名的方式，这种方式之所以可以成功是因为Spring Cloud Ribbon有一个拦截器，能够在实际调用的时候，自动去选取服务实例，并将实际要请求的IP地址和端口代替这里的服务名，从而完成服务接口调用。

```java
@RestController
public class DcController {

    @Autowired
    RestTemplate restTemplate;

    @GetMapping("/consumer")
    public String dc(){
        return restTemplate.getForObject("http://eureka-client-tough/dc",String.class);
    }
}
```

启动三个工程，访问http://localhost:2101/comsumer来跟踪观察eureka-consumer服务是如何消费eureka-client的/dc接口的。