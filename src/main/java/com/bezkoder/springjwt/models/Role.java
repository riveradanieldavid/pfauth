package com.bezkoder.springjwt.models;

import javax.persistence.*;

@Entity
// USE roles TABLE FROM testdb DB
@Table(name = "roles")
public class Role {

  // SET UNIQUE IDENTIFIER METHOD
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  // EACH ROLE NAME HAS A ENUMERATION
  @Enumerated(EnumType.STRING)
  @Column(length = 20)
  private ERole name;

  // Role CLASS
  public Role() {}

  // THE CLASS "Role" HAS A PARAMETER A ROLE NAME
  public Role(ERole name) {
    this.name = name;
  }

  // GETTER AND SETTER
  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public ERole getName() {
    return name;
  }

  public void setName(ERole name) {
    this.name = name;
  }
}
