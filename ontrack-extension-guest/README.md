Guest account extension
=======================

This extension allows the creation of a guest account.

The creation of the guest account is disabled by default and must be explicitely enabled using:

```
ontrack.extension.guest.enabled = true
```

Additionally, the user name & password can be configured using the following properties:

```
ontrack.extension.guest.username = guest
ontrack.extension.guest.password = guest
```

Note that the password can also be changed by the admin. Once the guest account has been created, the initial properties are not used any longer.

If the guest account is deleted, it'll be recreated at the next startup (if it remains enabled).

The guest account will be _deleted_ if Ontrack starts with the extension being disabled.

On the login page, the guest account information is displayed, with the following text as default:

> You can login in read-only mode with the guest account: guest / guest

This display can be disabled by using:

```
ontrack.extension.guest.display = false
```
