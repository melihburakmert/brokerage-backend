package mbm.brokerage_backend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.docs.Documenter;

@SpringBootTest
class BrokerageBackendApplicationIT {

	@Test
	void contextLoads() {
	}

	@Test
	void writeDocumentationSnippets() {
		final var modules = ApplicationModules.of(BrokerageBackendApplication.class).verify();
		modules.forEach(System.out::println);

		new Documenter(modules).writeModulesAsPlantUml().writeIndividualModulesAsPlantUml();
	}

}
