package org.worker.deserializers;


import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.util.Objects;

@AllArgsConstructor
@RequiredArgsConstructor
public class IndexObject {

    String collectionName;
    String fieldName;
    String value;

    public String getCollectionName() {
        return collectionName;
    }

    public void setCollectionName(String collectionName) {
        this.collectionName = collectionName;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IndexObject that = (IndexObject) o;
        return Objects.equals(collectionName, that.collectionName) &&
                Objects.equals(fieldName, that.fieldName) &&
                Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(collectionName, fieldName, value);
    }
//
//    @Override
//    public String toString() {
//        return "(" + collectionName + ", " + fieldName + ", " + value + ")";
//
//    }
}
