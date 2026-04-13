package modules;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import app.MenuPrincipal;
import java.util.*;
import java.sql.*;
import javafx.scene.layout.HBox;
public class TextAnalyser {
    
    private Stage stage;
    private TextArea zoneTexte;
    private Label lblResultat;
    private Label lblListCaracteres;
    private Label lblMotsCles;
    private Label lblSentiment;
    
    public TextAnalyser(Stage stage) {
        this.stage = stage;
    }

    public void afficher() {
        Label titre = new Label("ANALYSEUR DE TEXTE");
        titre.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
    
        zoneTexte = new TextArea();
        zoneTexte.setPromptText("Tapez ou collez votre texte ici...");
        zoneTexte.setPrefSize(900, 180);
        zoneTexte.setWrapText(true);
        zoneTexte.setStyle(
            "-fx-font-size: 14px;" +
            "-fx-background-radius: 10px;" +
            "-fx-border-radius: 10px;" +
            "-fx-border-color: #bdc3c7;" +
            "-fx-border-width: 2px;"
        );
        
        Button btnAnalyser = new Button("Analyser");
        btnAnalyser.setPrefSize(180, 45);
        btnAnalyser.setStyle(
            "-fx-background-color: #27ae60;" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 16px;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 10px;" +
            "-fx-cursor: hand;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0, 0, 3);"
        );
        btnAnalyser.setOnAction(e -> analyserTexte());
        
        Button btnRetour = new Button("Retour au menu");
        btnRetour.setPrefSize(180, 45);
        btnRetour.setStyle(
            "-fx-background-color: #e74c3c;" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 16px;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 10px;" +
            "-fx-cursor: hand;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0, 0, 3);"
        );
        btnRetour.setOnAction(e -> retourMenu());
        
        HBox conteneurBoutons = new HBox(20);
        conteneurBoutons.getChildren().addAll(btnAnalyser, btnRetour);
        conteneurBoutons.setAlignment(Pos.CENTER);
        
        lblResultat = new Label("");
        lblResultat.setStyle("-fx-font-size: 16px; -fx-text-fill: #2c3e50; -fx-font-weight: bold;");
        lblResultat.setWrapText(true);

        lblListCaracteres = new Label("");
        lblListCaracteres.setStyle("-fx-font-size: 14px; -fx-text-fill: #34495e;");
        lblListCaracteres.setWrapText(true);

        lblMotsCles = new Label("");
        lblMotsCles.setStyle("-fx-font-size: 14px; -fx-text-fill: #34495e;");
        lblMotsCles.setWrapText(true);

        lblSentiment = new Label("");
        lblSentiment.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        lblSentiment.setWrapText(true);
        
        VBox carteStats = new VBox(10);
        carteStats.getChildren().add(lblResultat);
        carteStats.setStyle(
            "-fx-background-color: #ecf0f1;" +
            "-fx-background-radius: 12px;" +
            "-fx-padding: 20px;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 8, 0, 0, 2);"
        );
        carteStats.setAlignment(Pos.CENTER_LEFT);
        carteStats.setPrefWidth(900);
        
        VBox carteLettres = new VBox(10);
        carteLettres.getChildren().add(lblListCaracteres);
        carteLettres.setStyle(
            "-fx-background-color: #e8f4f8;" +
            "-fx-background-radius: 12px;" +
            "-fx-padding: 20px;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 8, 0, 0, 2);"
        );
        carteLettres.setAlignment(Pos.CENTER_LEFT);
        carteLettres.setPrefWidth(900);
        
        VBox carteMotsCles = new VBox(10);
        carteMotsCles.getChildren().add(lblMotsCles);
        carteMotsCles.setStyle(
            "-fx-background-color: #e8f5e9;" +
            "-fx-background-radius: 12px;" +
            "-fx-padding: 20px;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 8, 0, 0, 2);"
        );
        carteMotsCles.setAlignment(Pos.CENTER_LEFT);
        carteMotsCles.setPrefWidth(900);
        
        VBox carteSentiment = new VBox(10);
        carteSentiment.getChildren().add(lblSentiment);
        carteSentiment.setStyle(
            "-fx-background-color: #fff3e0;" +
            "-fx-background-radius: 12px;" +
            "-fx-padding: 20px;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 8, 0, 0, 2);"
        );
        carteSentiment.setAlignment(Pos.CENTER);
        carteSentiment.setPrefWidth(900);
        
        VBox zoneResultats = new VBox(15);
        zoneResultats.getChildren().addAll(carteStats, carteLettres, carteMotsCles, carteSentiment);
        zoneResultats.setAlignment(Pos.CENTER);
        
        ScrollPane scrollResultats = new ScrollPane(zoneResultats);
        scrollResultats.setFitToWidth(true);
        scrollResultats.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        VBox layoutPrincipal = new VBox(25);
        layoutPrincipal.getChildren().addAll(titre, zoneTexte, conteneurBoutons, scrollResultats);
        layoutPrincipal.setAlignment(Pos.TOP_CENTER);
        layoutPrincipal.setStyle(
            "-fx-background-color: #f5f6fa;" +
            "-fx-padding: 30px;"
        );

        Scene scene = new Scene(layoutPrincipal, 1250, 1000);
        stage.setScene(scene);
    }

    private void analyserTexte() {
        String texte = zoneTexte.getText();
        
        if (texte.isEmpty()) {
            lblResultat.setText("Veuillez saisir du texte !");
            return;
        }
        
        int nbMots = compterMots(texte);
        int nbPhrases = compterPhrases(texte);
        int nbCaracteres = texte.length();
        
        lblResultat.setText("Mots : " + nbMots + " | Phrases : " + nbPhrases + " | Caractères : " + nbCaracteres);
        
        afficherFrequenceLettres(texte);
        
        afficherMotsCles(texte);
        
        analyserSentiment(texte);
    }

    private int compterMots(String texte) {
        String[] mots = texte.trim().split("\\s+");
        return mots.length;
    }
    private int compterPhrases(String texte) {
        String[] phrases = texte.split("[.!?]+");
        return phrases.length;
    }

    private void afficherFrequenceLettres(String texte) {
        String texteMin = texte.toLowerCase();
        int[] frequences = new int[26];
        
        for (char c : texteMin.toCharArray()) {
            if (c >= 'a' && c <= 'z') {
                int index = c - 'a';
                frequences[index]++;
            }
        }
        
        StringBuilder sb = new StringBuilder("Fréquence des lettres : ");
        int compteur = 0;
        
        for (int i = 0; i < 26; i++) {
            if (frequences[i] > 0) {
                char lettre = (char) ('a' + i);
                sb.append(lettre).append(":").append(frequences[i]).append("  ");
                compteur++;
                
                if (compteur % 8 == 0) {
                    sb.append("\n");
                }
            }
        }
        
        lblListCaracteres.setText(sb.toString());
    }

    private void afficherMotsCles(String texte) {
        Set<String> motsAIgnorer = new HashSet<>(Arrays.asList(
            "le", "la", "les", "un", "une", "des", "de", "du", "et", "ou", "mais", 
            "donc", "car", "ni", "or", "à", "au", "aux", "ce", "ces", "cet", "cette",
            "mon", "ma", "mes", "ton", "ta", "tes", "son", "sa", "ses", "notre", "nos",
            "votre", "vos", "leur", "leurs", "que", "qui", "quoi", "dont", "où",
            "il", "elle", "on", "nous", "vous", "ils", "elles", "je", "tu",
            "être", "avoir", "faire", "dire", "aller", "voir", "savoir", "pouvoir",
            "dans", "sur", "pour", "par", "avec", "sans", "sous", "vers", "chez",
            "en", "y", "plus", "moins", "très", "tout", "toute", "tous", "toutes","est"
        ));
        
        HashMap<String, Integer> compteurMots = new HashMap<>();
        String[] mots = texte.toLowerCase().split("[\\s,.!?;:]+");
        
        for (String mot : mots) {
            mot = mot.trim();
            if (mot.length() > 2 && !motsAIgnorer.contains(mot)) {
                compteurMots.put(mot, compteurMots.getOrDefault(mot, 0) + 1);
            }
        }
        
        List<Map.Entry<String, Integer>> listeMots = new ArrayList<>(compteurMots.entrySet());
        listeMots.sort((a, b) -> b.getValue().compareTo(a.getValue()));
        
        StringBuilder sb = new StringBuilder("Mots-clés (les plus fréquents) : ");
        int limite = Math.min(10, listeMots.size());
        
        for (int i = 0; i < limite; i++) {
            Map.Entry<String, Integer> entry = listeMots.get(i);
            sb.append(entry.getKey()).append("(").append(entry.getValue()).append(")");
            if (i < limite - 1) sb.append(", ");
        }
        
        lblMotsCles.setText(sb.toString());
    }

    private void analyserSentiment(String texte) {
        String[] mots = texte.toLowerCase().split("[\\s,.!?;:]+");
        int scorePositif = 0;
        int scoreNegatif = 0;
        
        try {
            // Connexion à la base de données
            Connection conn = DriverManager.getConnection("jdbc:sqlite:sentiment.db");
            
            for (String mot : mots) {
                mot = mot.trim();
                
                PreparedStatement stmtPos = conn.prepareStatement(
                    "SELECT COUNT(*) FROM mots_positifs WHERE mot = ?"
                );
                stmtPos.setString(1, mot);
                ResultSet rsPos = stmtPos.executeQuery();
                if (rsPos.next() && rsPos.getInt(1) > 0) {
                    scorePositif++;
                }
                
                PreparedStatement stmtNeg = conn.prepareStatement(
                    "SELECT COUNT(*) FROM mots_negatifs WHERE mot = ?"
                );
                stmtNeg.setString(1, mot);
                ResultSet rsNeg = stmtNeg.executeQuery();
                if (rsNeg.next() && rsNeg.getInt(1) > 0) {
                    scoreNegatif++;
                }
            }
            
            conn.close();
            
            int scoreFinal = scorePositif - scoreNegatif;
            String sentiment;
            String couleur;
            
            if (scoreFinal > 2) {
                sentiment = "TRÈS POSITIF";
                couleur = "#4CAF50";
            } else if (scoreFinal > 0) {
                sentiment = "Positif";
                couleur = "#8BC34A";
            } else if (scoreFinal == 0) {
                sentiment = "Neutre";
                couleur = "#FFC107";
            } else if (scoreFinal > -3) {
                sentiment = "Négatif";
                couleur = "#FF9800";
            } else {
                sentiment = "TRÈS NÉGATIF";
                couleur = "#F44336";
            }
            
            lblSentiment.setText("Sentiment : " + sentiment + " (Score : " + scoreFinal + ")");
            lblSentiment.setStyle("-fx-font-size: 16px; -fx-padding: 10px; -fx-font-weight: bold; -fx-background-color: " + couleur + "; -fx-text-fill: white;");
            
        } catch (SQLException e) {
            lblSentiment.setText("Base de données introuvable. Créez sentiment.db !");
            lblSentiment.setStyle("-fx-font-size: 14px; -fx-padding: 10px; -fx-background-color: #FF5722; -fx-text-fill: white;");
        }
    }

    private void retourMenu() {
        MenuPrincipal menu = new MenuPrincipal(stage);
        menu.afficher();
    }
}