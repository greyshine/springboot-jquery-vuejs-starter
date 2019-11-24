# springboot-jquery-vuejs-starter

Using the following aspects

- Java based on Springboot  
  
  [https://spring.io/projects/spring-boot]()  
  

- Thymeleaf  
  
  [https://spring.io/guides/gs/serving-web-content](), [https://www.thymeleaf.org]()  
  
- vue.js

  [https://vuejs.org/]()  


Note that under Eclipse debug mode it did not seem that updating thymeleaf web artefacts is taken into account of updating.  
It may be enabled as seen by using: [https://www.dineshonjava.com/customizing-spring-boot-auto-configuration/]()
Use this within the maven's _pom.xml_

    <dependency>
	      <groupId>org.springframework.boot</groupId>
	      <artifactId>spring-boot-devtools</artifactId>
	      <optional>true</optional>
    </dependency>
    
    

