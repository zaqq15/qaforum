package com.blueseals.qaforum;

import com.blueseals.qaforum.model.Role;
import com.blueseals.qaforum.model.User;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UserTest {


    @Test
    void testNoArgsConstructor() {
        User user = new User();
        assertNotNull(user);
        assertEquals(0, user.getReputation()); // Default int
    }

    @Test
    void testAllArgsConstructor() {
        User user = new User(1L, "email@test.com", "pass", "Name", Role.STUDENT, 100, "badge");
        assertEquals(1L, user.getId());
        assertEquals("email@test.com", user.getEmail());
    }

    @Test
    void testEquality_SameId() {
        User u1 = new User();
        u1.setId(100L);
        User u2 = new User();
        u2.setId(100L);

        assertEquals(u1, u2);
    }

    @Test
    void testEquality_DifferentId() {
        User u1 = new User();
        u1.setId(100L);
        User u2 = new User();
        u2.setId(200L);

        assertNotEquals(u1, u2);
    }

    @Test
    void testHashCode_Consistency() {
        User u1 = new User();
        u1.setId(50L);
        User u2 = new User();
        u2.setId(50L);

        assertEquals(u1.hashCode(), u2.hashCode());
    }
}
