package com.example.pioneerpixel.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(length = 500, nullable = false)
  private String name;

  @Column(name = "date_of_birth", nullable = false)
  @JsonFormat(pattern = "yyyy-MM-dd")
  private LocalDate dateOfBirth;

  @Column(nullable = false, length = 500)
  private String password;

  @OneToOne(
      mappedBy = "user",
      cascade = CascadeType.ALL,
      orphanRemoval = true,
      fetch = FetchType.LAZY)
  private Account account;

  @OneToMany(
      mappedBy = "user",
      cascade = CascadeType.PERSIST,
      orphanRemoval = true,
      fetch = FetchType.LAZY)
  private List<EmailData> emails = new ArrayList<>();

  @OneToMany(
      mappedBy = "user",
      cascade = CascadeType.PERSIST,
      orphanRemoval = true,
      fetch = FetchType.LAZY)
  private List<PhoneData> phones = new ArrayList<>();
}
