package com.cybertek.repository;

import com.cybertek.entity.Role;
import com.cybertek.entity.User;
import com.cybertek.enums.Gender;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User findByUserName(String username);

//    @Modifying
//    @Transactional
//    @Query("update User e set e.firstName = :firstName, e.lastName= :lastname, e.phone= :phone, e.gender=:gender, e.role=:role where e.userName = :username")
//    void updateByUserName(String firstName, String lastname, String phone, Gender gender, Role role, String username);

    @Transactional
    void deleteByUserName(String username);

    List<User> findAllByRoleDescriptionIgnoreCase(String role);


}
