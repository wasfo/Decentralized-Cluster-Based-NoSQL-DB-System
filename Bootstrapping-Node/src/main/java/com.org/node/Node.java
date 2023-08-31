package com.org.node;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class Node {
    private String hostname;
    private int port;
    private String nodeId;
    private NodeStatus status;

}
