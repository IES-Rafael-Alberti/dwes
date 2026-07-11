package com.example.minitasks.entities;
import jakarta.persistence.*;
@Entity public class Task {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
  @Column(nullable=false) private String title; private boolean done=false;
  public Task(){} public Task(String title){this.title=title;}
  public Long getId(){return id;} public void setId(Long id){this.id=id;}
  public String getTitle(){return title;} public void setTitle(String t){this.title=t;}
  public boolean isDone(){return done;} public void setDone(boolean d){this.done=d;}
}