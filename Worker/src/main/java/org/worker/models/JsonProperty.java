package org.worker.models;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class JsonProperty<T> {
    private String key;
    private T value;
}
