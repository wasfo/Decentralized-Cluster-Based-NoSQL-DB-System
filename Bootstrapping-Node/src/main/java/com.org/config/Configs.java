package com.org.config;

import com.org.loadbalance.RoundRobinLoadBalancer;
import com.org.node.Node;
import com.org.node.NodeStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;


@Configuration
public class Configs {


    @Bean
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }

    @Bean
    public List<Node> getNodes() {
        String node1HostName = System.getenv("NODE1_NAME");
        String node2HostName = System.getenv("NODE2_NAME");
        String node3HostName = System.getenv("NODE3_NAME");
        String node4HostName = System.getenv("NODE4_NAME");

        Node node1 = new Node(node1HostName, 8080, "1", NodeStatus.ACTIVE);
        Node node2 = new Node(node2HostName, 8080, "2", NodeStatus.ACTIVE);
        Node node3 = new Node(node3HostName, 8080, "3", NodeStatus.ACTIVE);
        Node node4 = new Node(node4HostName, 8080, "4", NodeStatus.ACTIVE);

        return new ArrayList<>(List.of(node1, node2, node3, node4));
    }
}
