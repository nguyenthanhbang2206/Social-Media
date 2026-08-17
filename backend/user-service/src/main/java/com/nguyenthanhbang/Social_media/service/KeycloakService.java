package com.nguyenthanhbang.Social_media.service;

import com.nguyenthanhbang.Social_media.dto.request.CreateUserRequest;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor
@Slf4j
public class KeycloakService {

    private final Keycloak keycloak;

    @Value("${keycloak.realm}")
    private String realm;

   
    public UUID createUser(CreateUserRequest request) {
        UserRepresentation userRep = new UserRepresentation();
        userRep.setUsername(request.getUsername());
        userRep.setEmail(request.getEmail());
        userRep.setEnabled(true);
        userRep.setEmailVerified(false);

        if (request.getFullName() != null && !request.getFullName().isBlank()) {
            String[] parts = request.getFullName().trim().split("\\s+", 2);
            userRep.setFirstName(parts[0]);
            if (parts.length > 1) {
                userRep.setLastName(parts[1]);
            }
        }

        // Set password ngay lúc tạo
        userRep.setCredentials(List.of(buildPasswordCredential(request.getPassword())));

        try (Response response = realmUsers().create(userRep)) {
            int status = response.getStatus();
            if (status == 201) {
                String location   = response.getLocation().getPath();
                String keycloakId = location.substring(location.lastIndexOf('/') + 1);
                log.info("Keycloak user created: email={}, keycloakId={}", request.getEmail(), keycloakId);

                // Gán role mặc định
                assignRealmRole(keycloakId, "USER");

                return UUID.fromString(keycloakId);

            } else if (status == 409) {
                String body = response.readEntity(String.class);
                throw new IllegalArgumentException("User already exists in Keycloak: " + body);
            } else {
                String body = response.readEntity(String.class);
                throw new IllegalStateException(
                        "Keycloak user creation failed [" + status + "]: " + body);
            }
        }
    }

   
    public void resetPassword(UUID keycloakId, String newPassword) {
        userResource(keycloakId).resetPassword(buildPasswordCredential(newPassword));
        log.info("Password reset for keycloakId={}", keycloakId);
    }

  
    public void updateUser(UUID keycloakId, String firstName, String lastName, String email) {
        UserResource   resource = userResource(keycloakId);
        UserRepresentation rep  = resource.toRepresentation();

        if (firstName != null) rep.setFirstName(firstName);
        if (lastName  != null) rep.setLastName(lastName);
        if (email     != null) rep.setEmail(email);

        resource.update(rep);
        log.info("Keycloak user updated: keycloakId={}", keycloakId);
    }

   
    public void assignRealmRole(String keycloakId, String roleName) {
        RealmResource realmResource = keycloak.realm(realm);
        RoleRepresentation role     = realmResource.roles().get(roleName).toRepresentation();
        realmResource.users().get(keycloakId).roles().realmLevel().add(List.of(role));
        log.info("Role '{}' assigned to keycloakId={}", roleName, keycloakId);
    }

    public void deleteUser(UUID keycloakId) {
        realmUsers().get(keycloakId.toString()).remove();
        log.info("Keycloak user deleted: keycloakId={}", keycloakId);
    }

    private UsersResource realmUsers() {
        return keycloak.realm(realm).users();
    }

    private UserResource userResource(UUID keycloakId) {
        return realmUsers().get(keycloakId.toString());
    }


    private CredentialRepresentation buildPasswordCredential(String password) {
        CredentialRepresentation cred = new CredentialRepresentation();
        cred.setType(CredentialRepresentation.PASSWORD);
        cred.setValue(password);
        cred.setTemporary(false);
        return cred;
    }
}
