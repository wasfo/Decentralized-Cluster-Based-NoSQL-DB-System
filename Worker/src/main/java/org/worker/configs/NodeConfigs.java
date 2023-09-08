package org.worker.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.worker.node.Node;
import org.worker.node.NodeStatus;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class NodeConfigs {

    @Bean
    public List<Node> getNodes() {

        Node node1 = new Node("Node1", 8080, NodeStatus.ACTIVE);
        Node node2 = new Node("Node2", 8081, NodeStatus.ACTIVE);

        return new ArrayList<>(List.of(node1, node2));
    }
}
