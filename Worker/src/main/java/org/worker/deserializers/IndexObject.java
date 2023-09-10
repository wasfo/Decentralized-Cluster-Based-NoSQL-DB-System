package org.worker.deserializers;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.Objects;

@AllArgsConstructor
@RequiredArgsConstructor
@Data
@Builder
public class IndexObject {
    private String collectionName;
    private String fieldName;
    private String value;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IndexObject that = (IndexObject) o;
        return Objects.equals(collectionName, that.collectionName) &&
                Objects.equals(fieldName, that.fieldName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(collectionName, fieldName, value);
    }

    @Override
    public String toString() {
        return "(" + collectionName + ", " + fieldName + ", " + value + ")";
    }
}
