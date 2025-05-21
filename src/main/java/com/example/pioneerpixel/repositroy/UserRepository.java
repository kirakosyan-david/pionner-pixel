package com.example.pioneerpixel.repositroy;

import com.example.pioneerpixel.entity.User;
import io.lettuce.core.dynamic.annotation.Param;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, Long> {

  @Query(
      """
            SELECT DISTINCT u FROM User u
            LEFT JOIN FETCH u.emails
            LEFT JOIN FETCH u.account
            WHERE u.id = :id
            """)
  Optional<User> findByIdWithEmailsAndAccount(@Param("id") Long id);

  @Query(
      """
            SELECT DISTINCT u FROM User u
            LEFT JOIN FETCH u.phones
            WHERE u.id = :id
            """)
  Optional<User> findByIdWithPhones(@Param("id") Long id);

  @Query("SELECT u FROM User u " + "LEFT JOIN FETCH u.account " + "WHERE u.id IN :userIds")
  List<User> findUsersWithDetailsByIds(@Param("userIds") List<Long> userIds);

  @Query("SELECT u FROM User u " + "LEFT JOIN FETCH u.emails " + "WHERE u.id IN :userIds")
  void fetchEmailsByIds(@Param("userIds") List<Long> userIds);

  @Query("SELECT u FROM User u " + "LEFT JOIN FETCH u.phones " + "WHERE u.id IN :userIds")
  void fetchPhonesByIds(@Param("userIds") List<Long> userIds);

  @Query(
      "SELECT DISTINCT u FROM User u "
          + "LEFT JOIN u.emails e "
          + "LEFT JOIN u.phones p "
          + "WHERE (:name IS NULL OR u.name LIKE CONCAT('%', :name, '%')) "
          + "AND (:phone IS NULL OR p.phone LIKE CONCAT('%', :phone, '%')) "
          + "AND (:email IS NULL OR e.email LIKE CONCAT('%', :email, '%')) "
          + "AND u.dateOfBirth = :dateOfBirth")
  Page<User> searchByUserWithDate(
      @Param("name") String name,
      @Param("phone") String phone,
      @Param("email") String email,
      @Param("dateOfBirth") LocalDate dateOfBirth,
      Pageable pageable);

  @Query(
      "SELECT DISTINCT u FROM User u "
          + "WHERE (:name IS NULL OR u.name LIKE CONCAT('%', :name, '%')) "
          + "AND (:phone IS NULL OR EXISTS (SELECT 1 FROM PhoneData p WHERE p.user = u AND p.phone LIKE CONCAT('%', :phone, '%'))) "
          + "AND (:email IS NULL OR EXISTS (SELECT 1 FROM EmailData e WHERE e.user = u AND e.email LIKE CONCAT('%', :email, '%')))")
  Page<User> searchByUserWithoutDate(
      @Param("name") String name,
      @Param("phone") String phone,
      @Param("email") String email,
      Pageable pageable);
}
