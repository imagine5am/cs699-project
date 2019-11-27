package pm9.trackingserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * Spring boot application which is run.
 * This file is the starting point when the application is deployed. It extends SpringBootServletInitializer.
 */
@SpringBootApplication
public class TrackingserverApplication extends SpringBootServletInitializer {

	/**
	 * Configures the SpringApplicationBuilder.
	 *
	 * @param application SpringApplicationBuilder which needs to be configured
	 * @return updated SpringApplicationBuilder
	 */
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(TrackingserverApplication.class);
	}

	/**
	 * Driver function.
	 * @param args Command line arguments.
	 */
	public static void main(String[] args) {
		SpringApplication.run(TrackingserverApplication.class, args);
	}

}
