package messageService;

import aws.annotation.EnableAwsClient;
import feignClient.EnableFeignClient;
import kafka.annotation.EnableKafkaClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;
import security.EnableMicroserviceSecurity;

@EnableAwsClient
@EnableFeignClient
@EnableKafkaClient
@SpringBootApplication
@EnableMicroserviceSecurity
public class MessageApplication {
	public static void main(String[] args) {
		SpringApplication.run(MessageApplication.class, args);
	}
}
