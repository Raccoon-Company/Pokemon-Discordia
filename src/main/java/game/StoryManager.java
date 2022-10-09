package game;

public class StoryManager {

    volatile static StoryManager instance;

    /**
     * Constructeur.
     */
    StoryManager() {
    }

    /**
     * Retourne l'instance de StoryManager.
     */
    public static StoryManager getInstance() {
        if (instance == null) {
            synchronized (StoryManager.class) {
                if (instance == null) {
                    instance = new StoryManager();
                }
            }
        }
        return instance;
    }


}
