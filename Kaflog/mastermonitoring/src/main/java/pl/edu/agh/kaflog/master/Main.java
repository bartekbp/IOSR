package pl.edu.agh.kaflog.master;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import pl.edu.agh.kaflog.master.monitoring.ProducerMonitoring;

import java.util.Arrays;

@Configuration
@EnableAutoConfiguration
@ComponentScan
@EnableScheduling
public class Main extends SpringBootServletInitializer  {
    @Autowired
    ProducerMonitoring producerMonitoring;

    public static void main(String... args) {
        SpringApplication.run(Main.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(Main.class);
    }

    @Scheduled(fixedRate = 20000)
    public void listClients() {
        System.out.println(producerMonitoring.listClients());
    }
}