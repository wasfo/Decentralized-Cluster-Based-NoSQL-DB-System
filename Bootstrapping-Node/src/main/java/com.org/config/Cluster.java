package com.org.config;

import com.org.node.Node;
import com.org.util.Url;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@Service
public class Cluster {

    private List<Node> nodes;
    private RestTemplate restTemplate;

    @Autowired
    public Cluster(List<Node> nodes, RestTemplate restTemplate) {
        this.nodes = nodes;
        this.restTemplate = restTemplate;
    }

    public boolean isClusterRunning() throws IOException {
        List<Boolean> checksList = new ArrayList<>();
        try {
            for (Node node : nodes) {
                String url = Url.getUrl(node.getHostname(), node.getPort(), "/status");
                ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
                boolean isOK = response.getStatusCode().equals(HttpStatus.OK);
                boolean isActive = Objects.equals(response.getBody(), "isActive");
                checksList.add(isOK && isActive);
            }
        } catch (RestClientException e) {
            System.out.println(e.getMessage());
            return false;
        }
        return checksList.contains(true);
    }


    @PostConstruct
    public void startUpCluster() throws IOException {
        if (!isClusterRunning()) {
            dockerComposeUpNodes();
        }
    }

    static public void dockerComposeUpNodes() {
        try {
            String[] command = {"docker-compose", "up", "-d"};

            ProcessBuilder processBuilder = new ProcessBuilder(command);
            processBuilder.directory(new File("C:\\Users\\super\\Desktop\\" +
                    "Decentralizd Nosql DB\\BootstrappingNode\\BootstrappingNode\\Node")); // Replace with the actual path

            processBuilder.redirectOutput(ProcessBuilder.Redirect.INHERIT);
            processBuilder.redirectError(ProcessBuilder.Redirect.INHERIT);

            Process process = processBuilder.start();

            int exitCode = process.waitFor();
            if (exitCode == 0) {
                System.out.println("Docker Compose process completed successfully.");
            } else {
                System.err.println("Docker Compose process failed with exit code " + exitCode);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
