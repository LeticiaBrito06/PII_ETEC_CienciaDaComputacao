package com.labquest.backend.service;

import com.labquest.backend.entity.enums.UserType;
import com.labquest.backend.exception.ApiException;
import com.labquest.backend.security.AuthenticatedUser;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class CurrentUserService {

    public AuthenticatedUser getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof AuthenticatedUser user)) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Usuario nao autenticado.");
        }
        return user;
    }

    public void requireRole(UserType tipo) {
        if (getCurrentUser().tipo() != tipo) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Perfil sem permissao para esta operacao.");
        }
    }
}
