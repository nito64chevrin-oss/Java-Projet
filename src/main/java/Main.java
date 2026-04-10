import javafx.application.Application;
import javafx.stage.Stage;
import javafx.application.Application;
import javafx.stage.Stage;
import app.MenuPrincipal;

public class Main extends Application {
    
    @Override
    public void start(Stage primaryStage) {
        // primaryStage = la fenêtre principale de votre app
        
        // On affiche le menu principal
        MenuPrincipal menu = new MenuPrincipal(primaryStage);
        menu.afficher();
        
        // Configuration de la fenêtre
        primaryStage.setTitle("Data & IA Toolbox");
        primaryStage.setWidth(800);
        primaryStage.setHeight(600);
        primaryStage.show();
    }
    
    public static void main(String[] args) {
        launch(args); // Démarre l'application JavaFX
    }
}