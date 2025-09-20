package com.FinTrace.smartWallet.CustomerService.console;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("console")
public class ConsoleAppRunner implements CommandLineRunner {

    private final ConsoleInterface consoleInterface;
    @Autowired
    public ConsoleAppRunner(ConsoleInterface consoleInterface) {
        this.consoleInterface = consoleInterface;
    }

    @Override
    public void run(String... args) throws Exception {
        consoleInterface.start();
    }
}
