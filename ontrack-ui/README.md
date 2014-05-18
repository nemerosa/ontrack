UI
==

## Following the links

`GET /projects/1?follow=branches,builds`

    UIProject      UIBranch        UIBuild

    (x) ---------> (x) -----------> (x)

`GET /branch/10?follow=project,build`

    UIBranch       UIProject       UIBuild

    (x) ---------> (x)
        |------------------------> (x)

We define a cyclic dependency by following the links between the controllers themselves.

It'd be better to have an interceptor that expands the links once the first call has been done:

`GET /projects/1?follow=branches,build`

    UIProject:
        {
            id: 1, name: "", description: "",
            branches: {
                href: "http://host/ui/projects/1/branches"
            },
            promotionLevels: {
                href: "http://host/ui/projects/1/promotionLevels"
            }
        }

Then the interceptor must expand the `branches` link. We do not want (and cannot) to expand the link at server side
using the HREF. We miss the knowledge that the `branches` link can be expanded using the
`UIBranch#getBranchesForProject` method.

