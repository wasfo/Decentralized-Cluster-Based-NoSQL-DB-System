package com.org;

import com.org.node.Node;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

import java.util.List;

@SpringBootApplication
public class BootstrappingNodeApplication {

	public static void main(String[] args) {
		SpringApplication.run(BootstrappingNodeApplication.class, args);
	}
}
