package com.smartschool;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class AppTest {

    @Test
    void testAppInitialization() {
        assertNotNull(App.class, "App class should be loadable.");
    }
}
