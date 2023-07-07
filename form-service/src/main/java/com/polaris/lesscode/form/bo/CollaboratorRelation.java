package com.polaris.lesscode.form.bo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CollaboratorRelation {
    private List<CollaboratorColumnUser> addUsers;
    private List<CollaboratorColumnUser> deleteUsers;

    public CollaboratorRelation() {
        addUsers = new ArrayList<>();
        deleteUsers = new ArrayList<>();
    }

    public void addUser(CollaboratorColumnUser user) {
        addUsers.add(user);
    }
    public void addUser(List<CollaboratorColumnUser> users) {
        addUsers.addAll(users);
    }

    public void deleteUser(CollaboratorColumnUser user) {
        deleteUsers.add(user);
    }
    public void deleteUser(List<CollaboratorColumnUser> user) {
        deleteUsers.addAll(user);
    }
}
