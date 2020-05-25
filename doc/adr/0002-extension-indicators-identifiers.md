# 2. Extension indicators identifiers

Date: 2020-05-13

## Status

Accepted

## Context

Example: take principles from an asciidoc source.

For sync, having uuid might be an issue unless we use a "source" optional
attribute. That might make things more complex...

A source can be empty (when created from GUI) or set to the FQCN of the
provisioner.

Even with a source attribute, having UUID does not make the automatic 
identification of a category or a type, based on name only, easy.
Hence the need for a semantic id.

Risks of collision for provisioners remains and provisioned items will have 
to prepend the id of their provisioner to their id.

A qualified source attribute still make sense to identify items which 
cannot be edited. To fit with the usual way of Ontrack, the source must 
be a FQCN which maps to an `Extension`.

## Decision


* do not use UUID for indicator items (categories, types & protfolios)
* for provisioned items, prepend the id of the provisioner to the item id
* introduce a source attribute and an indicator source model

## Consequences

IDs need to be managed.
