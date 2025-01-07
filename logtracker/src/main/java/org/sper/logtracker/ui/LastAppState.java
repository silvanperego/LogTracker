package org.sper.logtracker.ui;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * This class stores and loads the last file that was used for monitoring.
 * It uses a specific directory for this.
 */
public class LastAppState {

    private static final File APP_STATE_FILE =
            new File(System.getProperty("user.home") + File.separator + ".logtracker");

    public String getLastFileName() {
        if (APP_STATE_FILE.exists()) {
            try (var is = new BufferedReader(new FileReader(APP_STATE_FILE))) {
                return is.readLine();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    public void storeLastFileName(String lastFileName) {
        try (var os = new BufferedWriter(new FileWriter(APP_STATE_FILE))) {
            os.write(lastFileName);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
