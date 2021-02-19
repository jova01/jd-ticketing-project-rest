package com.cybertek.entity;

import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "confirmation_email")
@Where(clause = "is_deleted=false")
@NoArgsConstructor
public class ConfirmationToken extends BaseEntity{

    private String token;

    @OneToOne(targetEntity = User.class)
    @JoinColumn(name = "user_id")
    private User user;

    private LocalDate expireDate;

    public Boolean isTokenValid(LocalDate date){
        LocalDate now = LocalDate.now();
        return date.equals(now) || date.isAfter(now);
        //return date.equals(now) || date.isEqual(now.plusDays(1));
    }

    public ConfirmationToken(User user){
        this.user = user;
        expireDate = LocalDate.now().plusDays(1);
        token = UUID.randomUUID().toString();
    }

}
