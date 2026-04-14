package modules;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import app.MenuPrincipal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ChatBot {
    
    private Stage stage;
    private TextArea zoneConversation;
    private TextField champSaisie;
    private Connection conn;
    
    public ChatBot(Stage stage) {
        this.stage = stage;
        initialiserBaseDeDonnees();
    }
    
    public void afficher() {
        Label titre = new Label("CHATBOT DE SUPPORT");
        titre.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        
        zoneConversation = new TextArea();
        zoneConversation.setEditable(false);
        zoneConversation.setWrapText(true);
        zoneConversation.setPrefSize(900, 400);
        zoneConversation.setStyle(
            "-fx-font-size: 14px;" +
            "-fx-background-radius: 10px;" +
            "-fx-border-radius: 10px;" +
            "-fx-border-color: #bdc3c7;" +
            "-fx-border-width: 2px;" +
            "-fx-control-inner-background: #ecf0f1;"
        );
        
        ajouterMessage("Bot", "Bonjour ! Je suis votre assistant virtuel. Comment puis-je vous aider ?");
        
        champSaisie = new TextField();
        champSaisie.setPromptText("Tapez votre message ici...");
        champSaisie.setPrefHeight(40);
        champSaisie.setStyle(
            "-fx-font-size: 14px;" +
            "-fx-background-radius: 10px;" +
            "-fx-border-radius: 10px;" +
            "-fx-border-color: #3498db;" +
            "-fx-border-width: 2px;"
        );
        
        champSaisie.setOnAction(e -> envoyerMessage());
        
        Button btnEnvoyer = new Button("Envoyer");
        btnEnvoyer.setPrefSize(120, 40);
        btnEnvoyer.setStyle(
            "-fx-background-color: #3498db;" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 14px;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 10px;" +
            "-fx-cursor: hand;"
        );
        btnEnvoyer.setOnAction(e -> envoyerMessage());
        
        Button btnRetour = new Button("Retour au menu");
        btnRetour.setPrefSize(150, 40);
        btnRetour.setStyle(
            "-fx-background-color: #e74c3c;" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 14px;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 10px;" +
            "-fx-cursor: hand;"
        );
        btnRetour.setOnAction(e -> retourMenu());
        
        HBox zoneSaisie = new HBox(10);
        zoneSaisie.getChildren().addAll(champSaisie, btnEnvoyer);
        HBox.setHgrow(champSaisie, Priority.ALWAYS);
        zoneSaisie.setAlignment(Pos.CENTER);
        
        VBox layoutPrincipal = new VBox(20);
        layoutPrincipal.getChildren().addAll(titre, zoneConversation, zoneSaisie, btnRetour);
        layoutPrincipal.setAlignment(Pos.TOP_CENTER);
        layoutPrincipal.setPadding(new Insets(30));
        layoutPrincipal.setStyle("-fx-background-color: #f5f6fa;");
        
        Scene scene = new Scene(layoutPrincipal, 1600, 1000);
        stage.setFullScreen(true);
        stage.setScene(scene);
    }
    
    private void initialiserBaseDeDonnees() {
        try {
            conn = DriverManager.getConnection("jdbc:sqlite:chatbot.db");
        } catch (SQLException e) {
            System.err.println("Erreur connexion base de données: " + e.getMessage());
        }
    }
    
    private void envoyerMessage() {
        String message = champSaisie.getText().trim();
        
        if (message.isEmpty()) {
            return;
        }
        
        ajouterMessage("Vous", message);
        champSaisie.clear();
        
        String reponse = chercherReponse(message);
        ajouterMessage("Bot", reponse);
    }
    
    private String chercherReponse(String question) {
        try {
            String[] sousQuestions = decouperParConnecteurs(question);
            
            List<String> reponsesTrouvees = new ArrayList<>();
            List<String> questionsSansReponse = new ArrayList<>();
            
            for (String sousQuestion : sousQuestions) {
                String sousQuestionMin = sousQuestion.trim().toLowerCase();
                
                if (sousQuestionMin.isEmpty()) {
                    continue;
                }
                
                String reponse = chercherReponseSimple(sousQuestionMin);
                
                if (reponse != null) {
                    reponsesTrouvees.add(reponse);
                } else {
                    questionsSansReponse.add(sousQuestion.trim());
                }
            }
            
            StringBuilder reponseFinal = new StringBuilder();
            
            if (!reponsesTrouvees.isEmpty()) {
                for (int i = 0; i < reponsesTrouvees.size(); i++) {
                    reponseFinal.append(reponsesTrouvees.get(i));
                    if (i < reponsesTrouvees.size() - 1) {
                        reponseFinal.append("\n\n---\n\n");
                    }
                }
            }
            
            if (!questionsSansReponse.isEmpty()) {
                for (String q : questionsSansReponse) {
                    sauvegarderQuestionSansReponse(q);
                }
                
                if (reponsesTrouvees.isEmpty()) {
                    return "Désolé, je n'ai pas compris votre question. Elle a été enregistrée et notre équipe y répondra prochainement.";
                } else {
                    reponseFinal.append("\n\n---\n\nNote : Certaines parties de votre question n'ont pas pu être traitées et ont été enregistrées pour notre équipe.");
                }
            }
            
            return reponseFinal.toString();
            
        } catch (SQLException e) {
            return "Une erreur s'est produite. Veuillez réessayer.";
        }
    }

    private String[] decouperParConnecteurs(String question) {
        String[] connecteurs = {
            " et ",
            " puis ",
            " ensuite ",
            " aussi ",
            " également ",
            " de plus ",
            " en plus ",
            " par ailleurs ",
            " d'autre part ",
            " en outre "
        };
        
        String questionModifiee = question;
        for (String connecteur : connecteurs) {
            questionModifiee = questionModifiee.replaceAll("(?i)" + connecteur, "|||");
        }
        
        return questionModifiee.split("\\|\\|\\|");
    }

    private void sauvegarderQuestionSansReponse(String question) {
        try {
            String dateHeure = java.time.LocalDateTime.now().format(
                java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            );
            PreparedStatement pstmt = conn.prepareStatement(
                "INSERT INTO questions_sans_reponse (question, date_heure) VALUES (?, ?)"
            );
            pstmt.setString(1, question);
            pstmt.setString(2, dateHeure);
            pstmt.executeUpdate();
            System.out.println("[ADMIN] Question enregistrée : " + question);
        } catch (SQLException e) {
            System.err.println("Erreur sauvegarde question : " + e.getMessage());
        }
    }

    private String chercherReponseSimple(String question) throws SQLException {
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT mots_cles, reponse FROM reponses");
        while (rs.next()) {
            String motsCles = rs.getString("mots_cles").toLowerCase();
            String[] mots = motsCles.split(",");
            for (String mot : mots) {
                if (question.contains(mot.trim())) {
                    return rs.getString("reponse");
                }
            }
        }
        return null;
    }
    
    private void ajouterMessage(String expediteur, String texte) {
        String timestamp = java.time.LocalTime.now().format(
            java.time.format.DateTimeFormatter.ofPattern("HH:mm")
        );
        
        String message = String.format("[%s] %s: %s\n\n", timestamp, expediteur, texte);
        zoneConversation.appendText(message);
        zoneConversation.setScrollTop(Double.MAX_VALUE);
    }
    private void retourMenu() {
        try {
            if (conn != null) conn.close();
        } catch (SQLException e) {
            System.err.println("Erreur fermeture DB: " + e.getMessage());
        }
        
        MenuPrincipal menu = new MenuPrincipal(stage);
        menu.afficher();
    }
}