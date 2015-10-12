package backend.stbi.application;

import java.util.Set;

/**
 * Application Logic.
 * Handle all application features.
 */
public class ApplicationLogic {
    public Set<String> stopwords;

    private ApplicationLogic() {}

    public void loadStopwords() {
        System.out.println("loaded stopwords");
    }

    private static final ApplicationLogic oneInstance = new ApplicationLogic();

    public static ApplicationLogic getInstance() {
        return oneInstance;
    }
}
