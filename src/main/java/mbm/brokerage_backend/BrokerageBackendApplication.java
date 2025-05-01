package mbm.brokerage_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class BrokerageBackendApplication {

	public static void main(final String[] args) {
		SpringApplication.run(BrokerageBackendApplication.class, args);
	}

}
