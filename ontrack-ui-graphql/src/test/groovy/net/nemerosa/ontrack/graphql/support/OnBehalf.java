package net.nemerosa.ontrack.graphql.support;

import lombok.Data;

@Data
public class OnBehalf {

    private final Account delegate;
    private final Account account;

}
