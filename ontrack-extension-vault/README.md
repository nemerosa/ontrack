To launch a local development Vault Docker container:

```bash
docker run --cap-add=IPC_LOCK -d 
    -e 'VAULT_DEV_ROOT_TOKEN_ID=test' 
    --publish 8200:8200 vault
```

Then, launch Ontrack with following options:

```yaml
ontrack:
  config:
    key-store: vault
    vault:
      token: test
```
