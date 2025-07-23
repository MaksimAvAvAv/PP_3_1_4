package org.example.service;

import java.util.List;

import org.example.model.Role;
import org.example.repository.RoleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoleServiceImpl implements RoleService {

    private static final Logger logger = LoggerFactory.getLogger(RoleServiceImpl.class);

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public List<Role> findAll() {
        try {
            List<Role> roles = roleRepository.findAll();

            if (roles.isEmpty()) {
                logger.warn("Нет доступных ролей в базе данных.");
                logger.info("Найдено {} ролей.", roles.size());
            }

            return roles;
        } catch (Exception e) {
            logger.error("Ошибка при загрузке ролей из базы данных: {}", e.getMessage());
            throw e;
        }
    }
}