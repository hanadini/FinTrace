package com.FinTrace.customerSystem.console;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("console")
public class ConsoleAppRunner implements CommandLineRunner {

    private final MainConsoleInterface mainConsoleInterface;

    @Autowired
    public ConsoleAppRunner(MainConsoleInterface mainConsoleInterface) {
        this.mainConsoleInterface = mainConsoleInterface;
    }

    @Override
    public void run(String... args) throws Exception {
        mainConsoleInterface.start();
    }
}
