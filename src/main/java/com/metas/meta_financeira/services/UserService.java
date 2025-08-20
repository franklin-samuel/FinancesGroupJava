package com.metas.meta_financeira.services;

import com.metas.meta_financeira.models.User;
import com.metas.meta_financeira.repositories.UserRespository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRespository userRepository;

    public UserService(UserRespository userRepository) {
        this.userRepository = userRepository;
    }

    public User buscarOuCriar(String oauthId, String email, String name, String pictureUrl) {
        try {
            logger.info("[LOG] Iniciando busca ou criação de usuário: oauthId={}, email={}", oauthId, email);

            if (oauthId == null || oauthId.trim().isEmpty()) {
                throw new IllegalArgumentException("O oauthId não pode ser nulo ou vazio.");
            }
            if (email == null || email.trim().isEmpty()) {
                throw new IllegalArgumentException("O email não pode ser nulo ou vazio.");
            }

            Optional<User> userOpt = userRepository.findByOauthId(oauthId);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                logger.info("[LOG] Usuário encontrado por oauthId={}, atualizando dados...", oauthId);
                user.setEmail(email);
                user.setName(name);
                user.setPictureUrl(pictureUrl);
                User updated = userRepository.save(user);
                logger.info("[LOG] Usuário atualizado com sucesso: id={}, email={}", updated.getId(), updated.getEmail());
                return updated;
            }

            userOpt = userRepository.findByEmail(email);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                logger.info("[LOG] Usuário encontrado por email={}, vinculando oauthId={}", email, oauthId);
                user.setOauthId(oauthId);
                user.setName(name);
                user.setPictureUrl(pictureUrl);
                User updated = userRepository.save(user);
                logger.info("[LOG] Usuário atualizado com sucesso: id={}, email={}", updated.getId(), updated.getEmail());
                return updated;
            }

            logger.info("[LOG] Nenhum usuário encontrado, criando novo: email={}", email);
            User novo = new User(oauthId, email, name, pictureUrl);
            User saved = userRepository.save(novo);
            logger.info("[LOG] Novo usuário criado: id={}, email={}", saved.getId(), saved.getEmail());
            return saved;

        } catch (Exception e) {
            logger.error("[LOG] Erro ao buscar ou criar usuário: oauthId={}, email={}", oauthId, email, e);
            throw e;
        }
    }

    public Optional<User> buscarPorEmail(String email) {
        try {
            if (email == null || email.trim().isEmpty()) {
                throw new IllegalArgumentException("O email não pode ser nulo ou vazio.");
            }
            logger.info("[LOG] Buscando usuário por email={}", email);
            Optional<User> user = userRepository.findByEmail(email);
            if (user.isPresent()) {
                logger.info("[LOG] Usuário encontrado: id={}, email={}", user.get().getId(), user.get().getEmail());
            } else {
                logger.warn("[LOG] Nenhum usuário encontrado com email={}", email);
            }
            return user;
        } catch (Exception e) {
            logger.error("[LOG] Erro ao buscar usuário por email={}", email, e);
            throw e;
        }
    }

    public Optional<User> buscarPorOauthId(String oauthId) {
        try {
            if (oauthId == null || oauthId.trim().isEmpty()) {
                throw new IllegalArgumentException("O oauthId não pode ser nulo ou vazio.");
            }
            logger.info("[LOG] Buscando usuário por oauthId={}", oauthId);
            Optional<User> user = userRepository.findByOauthId(oauthId);
            if (user.isPresent()) {
                logger.info("[LOG] Usuário encontrado: id={}, email={}", user.get().getId(), user.get().getEmail());
            } else {
                logger.warn("[LOG] Nenhum usuário encontrado com oauthId={}", oauthId);
            }
            return user;
        } catch (Exception e) {
            logger.error("[LOG] Erro ao buscar usuário por oauthId={}", oauthId, e);
            throw e;
        }
    }
}
