package net.nemerosa.ontrack.extension.svn.db;

public interface SVNRepositoryDao {

    Integer findByName(String name);

    void delete(int id);

    int create(String name);

    int getByName(String name);
}
