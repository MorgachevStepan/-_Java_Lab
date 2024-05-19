package com.stepanew.goodmarksman.store;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
public class PlayerDAOBuilder {

    static private final PlayerDAO playerDAO;
    static private final Configuration configuration;
    static private final SessionFactory sessionFactory;

    static {
        configuration = new Configuration()
                .addAnnotatedClass(PlayerEntity.class);
        sessionFactory = configuration.buildSessionFactory();
        playerDAO = new PlayerDAOImpl(sessionFactory);
    }

    static public PlayerDAO build() {
        return playerDAO;
    }

}
