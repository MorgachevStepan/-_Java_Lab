package com.stepanew.goodmarksman.store;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
public class PlayerDAOBuilder {

    static private final PlayerDAO playerDAO;
    static private final Configuration configuration;
    static private final SessionFactory sessionFactory;

    static {
        System.out.println("Before config");
        configuration = new Configuration()
                .addAnnotatedClass(PlayerEntity.class);
        System.out.println("After config");
        sessionFactory = configuration.buildSessionFactory();
        playerDAO = new PlayerDAOImpl(sessionFactory);
        System.out.println("After session factory");
    }

    static public PlayerDAO build() {
        System.out.println("playerDAO is builded");
        return playerDAO;
    }

}
