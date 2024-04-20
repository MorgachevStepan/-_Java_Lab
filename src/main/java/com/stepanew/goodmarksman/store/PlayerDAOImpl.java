package com.stepanew.goodmarksman.store;

import lombok.RequiredArgsConstructor;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import java.util.List;

@RequiredArgsConstructor
public class PlayerDAOImpl implements PlayerDAO {

    private final SessionFactory sessionFactory;

    @Override
    public void addPlayer(PlayerEntity player) {
        try(Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();

            PlayerEntity playerEntity = (PlayerEntity) session.createCriteria(PlayerEntity.class)
                    .add(Restrictions.eq("name", player.getName()))
                    .uniqueResult();

            if(playerEntity == null) {

                session.merge(player);
                transaction.commit();
            }
        }
    }

    @Override
    public void updatePlayer(PlayerEntity player) {
        try(Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            PlayerEntity oldPlayer = (PlayerEntity) session.createCriteria(PlayerEntity.class)
                    .add(Restrictions.eq("id", player.getId()))
                    .uniqueResult();

            if(oldPlayer != null) {
                oldPlayer.setWins(player.getWins());

                session.update(oldPlayer);
            }

            transaction.commit();
        }
    }

    @Override
    public List<PlayerEntity> getAllPlayers() {
        List<PlayerEntity> result;

        try(Session session = sessionFactory.openSession()) {
            Criteria criteria = session.createCriteria(PlayerEntity.class)
                    .addOrder(Order.desc("wins"));
            result = criteria.list();
        }

        return result;
    }
}
