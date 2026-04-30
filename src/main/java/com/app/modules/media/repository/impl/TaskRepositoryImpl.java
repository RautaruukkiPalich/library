package com.app.modules.media.repository.impl;

import com.app.modules.media.enums.SortOrder;
import com.app.modules.media.enums.TaskStatus;
import com.app.modules.media.model.MediaTask;
import com.app.modules.media.repository.TaskRepositoryCustom;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.NonNull;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;

public class TaskRepositoryImpl implements TaskRepositoryCustom {

    private final static String ORDER_COLUMN = "createdAt";

    @PersistenceContext
    private EntityManager em;

    @Override
    public long count(@NonNull Long userId, TaskStatus status) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<MediaTask> root = countQuery.from(MediaTask.class);

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(root.get("userId"), userId));

        if (status != null) {
            predicates.add(cb.equal(root.get("status"), status));
        }

        countQuery.where(predicates.toArray(new Predicate[0]));
        countQuery.select(cb.countDistinct(root));

        return em.createQuery(countQuery).getSingleResult();
    }

    @Override
    public List<MediaTask> find(Long userId,
                                @NonNull Pageable pageable,
                                SortOrder order,
                                TaskStatus status) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<MediaTask> cq = cb.createQuery(MediaTask.class);
        Root<MediaTask> root = cq.from(MediaTask.class);

        List<Predicate> predicates = new ArrayList<>();
        if (userId != null) {
            predicates.add(cb.equal(root.get("userId"), userId));
        }

        if (status != null) {
            predicates.add(cb.equal(root.get("status"), status));
        }

        cq.where(predicates.toArray(new Predicate[0]));


        cq.orderBy(order != null && order.isDesc() ?
                cb.desc(root.get(ORDER_COLUMN)) :
                cb.asc(root.get(ORDER_COLUMN)));

        return em.createQuery(cq)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();
    }

}
