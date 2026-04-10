package modules;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import app.MenuPrincipal;

public class TextAnalyser {
    
    private Stage stage;
    
    public TextAnalyser(Stage stage) {
        this.stage = stage;
    }

public void afficher() {
    // === 1. TITRE ===
    Label titre = new Label("ANALYSEUR DE TEXTE");
    titre.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
    
    // === 2. ZONE DE TEXTE ===
    TextArea zoneTexte = new TextArea();
    zoneTexte.setPromptText("Tapez ou collez votre texte ici...");
    zoneTexte.setPrefSize(700, 200);
    zoneTexte.setWrapText(true);
    
    // === 3. BOUTON RETOUR ===
    Button btnRetour = new Button("Retour au menu");
    btnRetour.setStyle(
        "-fx-background-color: #f44336;" +
        "-fx-text-fill: white;" +
        "-fx-font-size: 14px;" +
        "-fx-padding: 10px 20px;"
    );
    btnRetour.setOnAction(e -> retourMenu());
    
    // === 4. DISPOSITION ===
    VBox layoutPrincipal = new VBox(20);
    layoutPrincipal.getChildren().addAll(titre, zoneTexte, btnRetour);
    layoutPrincipal.setAlignment(Pos.CENTER);
    layoutPrincipal.setStyle("-fx-background-color: white; -fx-padding: 20px;");
    
    // === 5. SCÈNE ===
    Scene scene = new Scene(layoutPrincipal, 800, 600);
    stage.setScene(scene);
}

private void retourMenu() {
    MenuPrincipal menu = new MenuPrincipal(stage);
    menu.afficher();
}
}