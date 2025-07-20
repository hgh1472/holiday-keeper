package com.holidaykeeper.utils;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DatabaseCleanUp implements InitializingBean {

    private final List<String> tableNames = new ArrayList<>();
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void afterPropertiesSet() {
        entityManager.getMetamodel().getEntities().stream()
                .filter(entity -> entity.getJavaType().getAnnotation(Entity.class) != null)
                .map(entity -> entity.getJavaType().getAnnotation(Table.class).name())
                .forEach(tableNames::add);
    }

    @Transactional
    public void truncateAllTables() {
        entityManager.flush();
        entityManager.createNativeQuery("SET REFERENTIAL_INTEGRITY FALSE").executeUpdate();

        for (String table : tableNames) {
            entityManager.createNativeQuery("TRUNCATE TABLE \"" + table.toUpperCase() + "\"").executeUpdate();
        }

        entityManager.createNativeQuery("SET REFERENTIAL_INTEGRITY TRUE").executeUpdate();
    }
}
