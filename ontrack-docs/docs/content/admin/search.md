# Search

Yontrack indexes all the data it manages, and allows the users to search for them.

## Configuration

### Indexing of commits and issues

> In 5.1, two distinct ways of indexing commits and issues are available.
>
> The legacy way is to index this data in ElasticSearch and the new, experimental way, is to index this data in
> PostgreSQL.
>
> The legacy way is still available until it's fully decommissioned (in 5.2)

To switch between the two, you set the `ONTRACK_CONFIG_EXTENSION_SCM_SEARCH_INDEXATIONTYPE` to:

* `ELASTIC_SEARCH` - the default, legacy way
* `DATABASE` - the new experimental way
