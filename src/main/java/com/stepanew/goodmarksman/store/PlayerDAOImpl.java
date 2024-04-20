package com.stepanew.goodmarksman.store;

import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class PlayerDAOImpl implements PlayerDAO {

    private final SessionFactory sessionFactory;

    @Override
    public void addPlayer(PlayerEntity player) {
        try(Session session = sessionFactory.openSession()) {
            System.out.println("Adding" + player.getName());
            Transaction transaction = session.beginTransaction();
            session.merge(player);
            transaction.commit();
        }
    }

    @Override
    public Optional<Integer> getPlayerWins(String name) {
        return Optional.empty();
    }

    @Override
    public void setPlayerWins(PlayerEntity player) {

    }

    @Override
    public void incrementPlayerWins(PlayerEntity player) {

    }

    @Override
    public List<PlayerEntity> getAllPlayers() {
        return null;
    }
}
