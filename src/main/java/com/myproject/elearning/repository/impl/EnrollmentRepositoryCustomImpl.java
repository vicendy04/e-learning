package com.myproject.elearning.repository.impl;

// import com.myproject.elearning.dto.response.enrollment.EnrollmentGetRes;
// import com.myproject.elearning.repository.custom.EnrollmentRepositoryCustom;
// import jakarta.persistence.EntityManager;
// import jakarta.persistence.PersistenceContext;
// import org.springframework.data.domain.Page;
// import org.springframework.data.domain.PageImpl;
// import org.springframework.data.domain.Pageable;
// import org.springframework.stereotype.Repository;
//
// import java.util.List;
//
// @Repository
// public class EnrollmentRepositoryCustomImpl implements EnrollmentRepositoryCustom {
//
//    @PersistenceContext
//    private EntityManager entityManager;
//
//    @Override
//    public Page<EnrollmentGetRes> findAllByUserId(Long userId, Pageable pageable) {
//        List<EnrollmentGetRes> enrollments = entityManager.createQuery("""
//                        SELECT new com.myproject.elearning.dto.response.enrollment.EnrollmentGetRes(
//                            e.id,
//                            new com.myproject.elearning.dto.response.enrollment.EnrollmentGetRes.UserInfo(u.id,
// u.email, u.username),
//                            new com.myproject.elearning.dto.response.enrollment.EnrollmentGetRes.CourseInfo(c.id,
// c.title, c.description, c.price, c.category),
//                            e.enrolledAt,
//                            e.status
//                        )
//                        FROM Enrollment e
//                        JOIN e.user u
//                        JOIN e.course c
//                        WHERE u.id = :userId
//                        """, EnrollmentGetRes.class)
//                .setParameter("userId", userId)
//                .setFirstResult((int) pageable.getOffset())
//                .setMaxResults(pageable.getPageSize())
//                .getResultList();
//
//        long total = entityManager.createQuery("""
//                        SELECT COUNT(e)
//                        FROM Enrollment e
//                        WHERE e.user.id = :userId
//                        """, Long.class)
//                .setParameter("userId", userId)
//                .getSingleResult();
//
//        return new PageImpl<>(enrollments, pageable, total);
//    }
//
//    @Override
//    public Page<EnrollmentGetRes> findAllByCourseId(Long courseId, Pageable pageable) {
//        List<EnrollmentGetRes> enrollments = entityManager.createQuery("""
//                        SELECT new com.myproject.elearning.dto.response.enrollment.EnrollmentGetRes(
//                            e.id,
//                            new com.myproject.elearning.dto.response.enrollment.CourseInfo(c.id, c.title,
// c.description, c.price, c.category),
//                            new com.myproject.elearning.dto.response.enrollment.UserInfo(u.id, u.email, u.username),
//                            e.enrolledAt,
//                            e.status
//                        )
//                        FROM Enrollment e
//                        JOIN e.user u
//                        JOIN e.course c
//                        WHERE c.id = :courseId
//                        """, EnrollmentGetRes.class)
//                .setParameter("courseId", courseId)
//                .setFirstResult((int) pageable.getOffset())
//                .setMaxResults(pageable.getPageSize())
//                .getResultList();
//
//        long total = entityManager.createQuery("""
//                        SELECT COUNT(e)
//                        FROM Enrollment e
//                        WHERE e.course.id = :courseId
//                        """, Long.class)
//                .setParameter("courseId", courseId)
//                .getSingleResult();
//
//        return new PageImpl<>(enrollments, pageable, total);
//    }
// }
