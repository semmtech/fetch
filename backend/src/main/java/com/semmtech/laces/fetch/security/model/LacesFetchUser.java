package com.semmtech.laces.fetch.security.model;


import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "users")
public class LacesFetchUser {
    @Id
    private ObjectId _id;
    private String username;
    private String password;

}
