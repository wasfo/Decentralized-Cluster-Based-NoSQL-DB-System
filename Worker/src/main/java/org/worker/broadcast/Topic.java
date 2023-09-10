package org.worker.broadcast;

public enum Topic {

    Create_Database_Topic("createDatabaseTopic"),
    Register_User_Topic("registerUserTopic"),
    Delete_Database_Topic("deleteDatabaseTopic"),
    Add_Document_Topic("addDocumentTopic"),
    Delete_Collection_Topic("deleteCollectionTopic"),
    Delete_Document_Topic("deleteDocumentTopic"),
    Delete_All_Documents_Topic("deleteAllDocumentsTopic"),
    New_Collection_Topic("newCollectionTopic"),
    New_Empty_Collection_Topic("newEmptyCollectionTopic");


    private final String topicValue;

    Topic(String topicValue) {
        this.topicValue = topicValue;
    }

    public String getTopicValue() {
        return topicValue;
    }
}
