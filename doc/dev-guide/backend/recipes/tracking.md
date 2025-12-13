# Tracking

While some links between entities are tracked statically (like a branch being linked to its project), we often need to
track dynamic relationships, like an auto-versioning request to a promotion run, etc.

This mechanism is called _tracking_.

> This mechanism is in place for the auto-versioning but is not applied yet to other types of entities. This will happen
> in the future.

## Design

An _entity_ can track a series of _sources_.

Each source is a combination of:

* a _type_ (_what type of entity_ is being tracked)
* some _data_ (_which entity exactly_ is being tracked)

For example:

| Source                  | Type              | Data                       |
|-------------------------|-------------------|----------------------------|
| Promotion run source    | "Promotion run"   | Promotion run ID           |
| Notification source     | "Notification"    | Notification record UUID   |
| Auto-versioning request | "Auto versioning" | Auto-versioning order UUID |

## Storage

How are sources associated to their target entities in the database?

One unique table is used to track all targets & sources. This way, we can navigate the relationships in both
directions.

The `tracking` table is used for this purpose and contains two main lots of columns:

* identification of the source (type & data)
* identification of the target (type & data)

## Extensions

Each type of source is associated with an extension (`TrackingSourceExtension`), used to:

* create some tracking data from an actual source entity
* getting the entity from its tracking data
* rendering a link in the UI to the source entity

## Cleanup

By storing untyped tracking data in the database, we can't easily clean up obsolete tracking data.

For example, a promotion run is being tracked as a source, but it's been deleted. The tracking data is still there.

Either we need a mechanism to clean up obsolete tracking data, or the tracking source extensions are responsible for returning a non-existent entity (null) when the source is not found.

## Usage

### Registering a tracking source

Two things must be done:

* getting the target entity tracking info
* getting the source entity tracking info
* registering the tracking info

### Getting the tracking info for an entity

Getting the list of source tracking info for a given entity (it being the target)

