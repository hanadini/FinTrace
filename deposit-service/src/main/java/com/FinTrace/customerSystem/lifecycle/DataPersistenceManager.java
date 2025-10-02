package com.FinTrace.customerSystem.lifecycle;

import com.FinTrace.customerSystem.dto.FileType;
import com.FinTrace.customerSystem.facade.DepositFacade;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.FileOutputStream;
import java.io.IOException;

@Component
@Profile("memo")
@ConditionalOnProperty(
        prefix = "memo.lifecycle",
        name = "enabled",
        havingValue = "true",
        matchIfMissing = true
)
public class DataPersistenceManager {

    private final DepositFacade depositFacade;

    private final FileType fileType = FileType.JSON;

    @Value("${memo.fileName.deposit}")
    private String depositFileName;

    @Autowired
    public DataPersistenceManager(DepositFacade depositFacade) {
        this.depositFacade = depositFacade;
    }

    @PostConstruct
    public void onStartup() {
        // Initialize or load data from the database
        // This could be loading initial customers or any other setup needed
        System.out.println("DataPersistenceManager: Initializing data...");
        importFromFile();
    }

    @PreDestroy
    public void onShutdown() {
        // Handle any cleanup or data persistence before the application shuts down
        System.out.println("DataPersistenceManager: Cleaning up data...");
        exportToFile();
    }

    private void exportToFile() {
        try {
            byte[] depositFileContent = depositFacade.exportDeposits(fileType);
            writeToFile(depositFileContent, depositFileName);
        } catch (IOException e) {
            System.err.println("Error exporting customers: " + e.getMessage());
        }
    }

    private void writeToFile(byte[] fileContent, String fileName) {
        try (FileOutputStream fos = new FileOutputStream(fileName)) {
            fos.write(fileContent);
            System.out.println("Customers exported successfully");
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());

        }
    }

    private void importFromFile() {
        try {
            byte[] depositFileContent = java.nio.file.Files.readAllBytes(java.nio.file.Paths.get(depositFileName));
            depositFacade.importDeposits(depositFileContent, fileType);
            System.out.println("Customers imported successfully");
        } catch (IOException e) {
            System.err.println("Error reading from file: " + e.getMessage());
        }
    }
}
