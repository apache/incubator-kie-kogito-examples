# Serverless Workflow OAuth2 Token Exchange Example

```shell
KEYCLOAK_ADDRESS=http://192.168.106.2:32769

curl -X POST "${KEYCLOAK_ADDRESS}/realms/quarkus/protocol/openid-connect/token" \
    -H "Content-Type: application/x-www-form-urlencoded" \
    -d "grant_type=client_credentials" \
    -d "client_id=quarkus-app" \
    -d "client_secret=secret"
```

## References

- [Using OpenID Connect (OIDC) and Keycloak to centralize authorization](https://quarkus.io/guides/security-keycloak-authorization)

## Troubleshooting

Problems with Colima on MacOs: https://github.com/testcontainers/testcontainers-java/issues/6450