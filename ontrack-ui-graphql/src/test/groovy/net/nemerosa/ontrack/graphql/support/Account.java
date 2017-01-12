package net.nemerosa.ontrack.graphql.support;

import lombok.Data;

@Data
public class Account {

    private final String username;
    private final String password;
    private final Person identity;

}
