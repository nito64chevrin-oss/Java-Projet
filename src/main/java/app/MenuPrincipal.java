package app;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import modules.TextAnalyser;
import modules.ChatBot;
public class MenuPrincipal {
    
    private Stage stage;

    public MenuPrincipal(Stage stage) {
        this.stage = stage;
    }
    
    public void afficher() {
        Label titre = new Label("DATA & IA");
        titre.setStyle("-fx-font-size: 32px; -fx-font-weight: bold;");
        Button btnTexte = new Button("Analyseur\nde Texte");
        styliserBouton(btnTexte);
        btnTexte.setOnAction(e -> ouvrirModuleTexte());

        Button btnReco = new Button("Système de\nRecommandation");
        styliserBouton(btnReco);
        btnReco.setOnAction(e -> ouvrirModuleReco());
        
        Button btnMeteo = new Button("ChatBot");
        styliserBouton(btnMeteo);
        btnMeteo.setOnAction(e -> ouvrirModuleChatBot());
        
        HBox conteneurBoutons = new HBox(30); // 30 = espacement entre boutons
        conteneurBoutons.getChildren().addAll(btnTexte, btnReco, btnMeteo);
        conteneurBoutons.setAlignment(Pos.CENTER); // Centrer horizontalement
        
        // === 4. DISPOSITION VERTICALE GLOBALE ===
        VBox layoutPrincipal = new VBox(50); // 50 = espacement vertical
        layoutPrincipal.getChildren().addAll(titre, conteneurBoutons);
        layoutPrincipal.setAlignment(Pos.CENTER); // Tout centré
        layoutPrincipal.setStyle("-fx-background-color: #f0f0f0;"); // Fond gris clair
        
        // === 5. CRÉATION DE LA SCÈNE ===
        Scene scene = new Scene(layoutPrincipal, 800, 600);
        stage.setScene(scene);
    }
    
    // Méthode pour styliser les boutons (coins arrondis, taille, etc.)
    private void styliserBouton(Button btn) {
        btn.setPrefSize(200, 150); // Largeur 200, Hauteur 150
        btn.setStyle(
            "-fx-background-color: white;" +
            "-fx-border-color: black;" +
            "-fx-border-width: 2px;" +
            "-fx-border-radius: 20px;" +
            "-fx-background-radius: 20px;" +
            "-fx-font-size: 16px;" +
            "-fx-cursor: hand;" // Curseur en forme de main
        );
        
        // Effet au survol de la souris
        btn.setOnMouseEntered(e -> btn.setStyle(
            "-fx-background-color: #e0e0e0;" +
            "-fx-border-color: black;" +
            "-fx-border-width: 2px;" +
            "-fx-border-radius: 20px;" +
            "-fx-background-radius: 20px;" +
            "-fx-font-size: 16px;"
        ));
        
        btn.setOnMouseExited(e -> btn.setStyle(
            "-fx-background-color: white;" +
            "-fx-border-color: black;" +
            "-fx-border-width: 2px;" +
            "-fx-border-radius: 20px;" +
            "-fx-background-radius: 20px;" +
            "-fx-font-size: 16px;"
        ));
    }
    
    // === MÉTHODES POUR OUVRIR LES MODULES ===
    
    private void ouvrirModuleTexte() {
        TextAnalyser module = new TextAnalyser(stage);
        module.afficher();
    }
    
    private void ouvrirModuleReco() {
        System.out.println("Ouverture du module Recommandation...");
    }
    
    private void ouvrirModuleChatBot() {
        ChatBot module = new ChatBot(stage);
        module.afficher();
    }
}