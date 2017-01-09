package net.nemerosa.ontrack.graphql.support;

import lombok.Data;

@Data
public class Person {

    private final String name;
    private final String address;
    private final int age;
    private final boolean developer;

}
