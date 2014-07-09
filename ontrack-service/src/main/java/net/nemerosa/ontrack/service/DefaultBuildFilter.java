package net.nemerosa.ontrack.service;

import net.nemerosa.ontrack.model.structure.BuildFilter;

public final class DefaultBuildFilter implements BuildFilter {

    public static final int MAX_COUNT = 10;
    public static final BuildFilter INSTANCE = new DefaultBuildFilter();

    private DefaultBuildFilter() {
    }

    @Override
    public boolean acceptCount(int size) {
        return size <= MAX_COUNT;
    }
}
