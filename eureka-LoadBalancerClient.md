# LoadBalancerClient

在Spring Cloud Commons中提供了大量的与服务治理相关的抽象接口，包括DiscoveryClient。Spring Cloud做这一层抽象，很好的解耦了服务治理体系，使得我们可以轻易的替换不同的服务治理设施。

LoadBalancerClient接口是一个负载均衡客户端的抽象定义。

## 创建eureka-consumer

这里使用eureka-server作为服务注册中心，eureka-client作为服务提供者作为基础，创建eureka-consumer。导入pom.xml文件：

```xml
	<name>eureka-consumer</name>
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

## 创建主类

初始化RestTemplate，用来真正发起REST请求。并使用@EnableDiscoveryClient注解将当前应用加入到服务治理体系中。

```java
@EnableDiscoveryClient
@SpringBootApplication
public class EurekaConsumerApplication {

	@Bean
	public RestTemplate restTemplate(){
		return new RestTemplate();
	}

	public static void main(String[] args) {
		SpringApplication.run(EurekaConsumerApplication.class, args);
	}
}
```

## 创建消费eureka-client提供接口的接口

注入LoadBalancerClient和RestTemplate，并在/client接口的实现中，

先通过loadBalancerClient的choose函数来负责负载均衡的选出一个eureka-client的服务实例。

这个服务实例的基本信息存储在serviceInstance中，

然后通过这些对象中的信息拼接处访问/dc接口的详细地址，

最后再利用RestTemplate对象实现对服务提供者接口的调用。

```java
@RestController
public class DcController {

    @Autowired
    LoadBalancerClient loadBalancerClient;

    @Autowired
    RestTemplate restTemplate;

    @GetMapping("/consumer")
    public String dc(){
        ServiceInstance serviceInstance = loadBalancerClient.choose("eureka-client-tough");
        String url = "http://" + serviceInstance.getHost() + ":" + serviceInstance.getPort() + "/dc";
        System.out.println(url);
        return restTemplate.getForObject(url,String.class);
    }
}
```

启动三个工程，访问http://localhost:2101/comsumer来跟踪观察eureka-consumer服务是如何消费eureka-client的/dc接口的。