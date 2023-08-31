package com.org.loadbalance;
import com.org.node.Node;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
public class RoundRobinLoadBalancer {
    private List<Node> nodes;
    private int currentIndex;

    @Autowired
    public RoundRobinLoadBalancer(List<Node> nodes) {
        this.nodes = nodes;
        this.currentIndex = 0;
    }
    @PostConstruct
    public void displayNodes(){
        for (Node node: nodes) {
            System.out.println(node);
        }
    }
    public Node getNextNode() {
        Node node = nodes.get(currentIndex);
        currentIndex = (currentIndex + 1) % nodes.size();
        return node;
    }
}
