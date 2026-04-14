package modules;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Priority;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;
import app.MenuPrincipal;
import java.io.*;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class PredicteurRisque {
    
    private Stage stage;
    private Connection conn;
    private TableView<Demande> tableView;
    private ObservableList<Demande> demandes;
    private Label lblStats;
    
    private Slider sliderAge;
    private Slider sliderRevenus;
    private Slider sliderMontant;
    private Slider sliderDuree;
    private ComboBox<String> comboEmploi;
    private Slider sliderDette;
    private Label lblScoreSimu;
    private Label lblDecisionSimu;
    private Rectangle jaugeRisque;
    
    public PredicteurRisque(Stage stage) {
        this.stage = stage;
        initialiserBaseDeDonnees();
        demandes = FXCollections.observableArrayList();
    }
    
    public void afficher() {
        Label titre = new Label("PRÉDICTEUR DE RISQUE - PRÊTS");
        titre.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        
        // ===== BOUTONS =====
        Button btnImporter = new Button("Importer CSV");
        btnImporter.setPrefSize(180, 45);
        btnImporter.setStyle(
            "-fx-background-color: #3498db;" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 16px;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 10px;" +
            "-fx-cursor: hand;"
        );
        btnImporter.setOnAction(e -> importerCSV());
        
        Button btnActualiser = new Button("Actualiser");
        btnActualiser.setPrefSize(180, 45);
        btnActualiser.setStyle(
            "-fx-background-color: #27ae60;" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 16px;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 10px;" +
            "-fx-cursor: hand;"
        );
        btnActualiser.setOnAction(e -> chargerDemandes());
        
        Button btnVider = new Button("Vider la base");
        btnVider.setPrefSize(180, 45);
        btnVider.setStyle(
            "-fx-background-color: #e67e22;" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 16px;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 10px;" +
            "-fx-cursor: hand;"
        );
        btnVider.setOnAction(e -> viderBase());
        
        Button btnRetour = new Button("Retour au menu");
        btnRetour.setPrefSize(180, 45);
        btnRetour.setStyle(
            "-fx-background-color: #e74c3c;" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 16px;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 10px;" +
            "-fx-cursor: hand;"
        );
        btnRetour.setOnAction(e -> retourMenu());
        
        HBox conteneurBoutons = new HBox(15);
        conteneurBoutons.getChildren().addAll(btnImporter, btnActualiser, btnVider, btnRetour);
        conteneurBoutons.setAlignment(Pos.CENTER);
        
        lblStats = new Label("Chargez un fichier CSV pour commencer");
        lblStats.setStyle("-fx-font-size: 16px; -fx-padding: 15px; -fx-background-color: #ecf0f1; -fx-background-radius: 10px;");
        
        tableView = new TableView<>();
        tableView.setItems(demandes);
        
        TableColumn<Demande, String> colNom = new TableColumn<>("Nom");
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colNom.setPrefWidth(150);
        
        TableColumn<Demande, Integer> colAge = new TableColumn<>("Âge");
        colAge.setCellValueFactory(new PropertyValueFactory<>("age"));
        colAge.setPrefWidth(60);
        
        TableColumn<Demande, Double> colRevenus = new TableColumn<>("Revenus");
        colRevenus.setCellValueFactory(new PropertyValueFactory<>("revenusMensuels"));
        colRevenus.setPrefWidth(100);
        
        TableColumn<Demande, Double> colMontant = new TableColumn<>("Montant");
        colMontant.setCellValueFactory(new PropertyValueFactory<>("montantDemande"));
        colMontant.setPrefWidth(100);
        
        TableColumn<Demande, Integer> colDuree = new TableColumn<>("Durée");
        colDuree.setCellValueFactory(new PropertyValueFactory<>("dureeMois"));
        colDuree.setPrefWidth(70);
        
        TableColumn<Demande, String> colEmploi = new TableColumn<>("Emploi");
        colEmploi.setCellValueFactory(new PropertyValueFactory<>("emploi"));
        colEmploi.setPrefWidth(120);
        
        TableColumn<Demande, Double> colDette = new TableColumn<>("Dette");
        colDette.setCellValueFactory(new PropertyValueFactory<>("detteActuelle"));
        colDette.setPrefWidth(90);
        
        TableColumn<Demande, Integer> colScore = new TableColumn<>("Score");
        colScore.setCellValueFactory(new PropertyValueFactory<>("scoreRisque"));
        colScore.setPrefWidth(70);
        
        TableColumn<Demande, String> colDecision = new TableColumn<>("Décision");
        colDecision.setCellValueFactory(new PropertyValueFactory<>("decision"));
        colDecision.setPrefWidth(120);
        
        colDecision.setCellFactory(column -> new TableCell<Demande, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    if (item.equals("ACCEPTÉ")) {
                        setStyle("-fx-background-color: #d4edda; -fx-text-fill: #155724; -fx-font-weight: bold;");
                    } else if (item.equals("REFUSÉ")) {
                        setStyle("-fx-background-color: #f8d7da; -fx-text-fill: #721c24; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-background-color: #fff3cd; -fx-text-fill: #856404; -fx-font-weight: bold;");
                    }
                }
            }
        });
        
        tableView.getColumns().addAll(colNom, colAge, colRevenus, colMontant, colDuree, colEmploi, colDette, colScore, colDecision);
        
        VBox zoneSimulation = creerZoneSimulation();
        
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: #f5f6fa; -fx-background-color: #f5f6fa;");
        
        VBox conteneurScroll = new VBox(20);
        conteneurScroll.getChildren().addAll(tableView, zoneSimulation);
        conteneurScroll.setPadding(new Insets(10));
        
        scrollPane.setContent(conteneurScroll);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        
        VBox layoutPrincipal = new VBox(20);
        layoutPrincipal.getChildren().addAll(titre, conteneurBoutons, lblStats, scrollPane);
        layoutPrincipal.setAlignment(Pos.TOP_CENTER);
        layoutPrincipal.setPadding(new Insets(30));
        layoutPrincipal.setStyle("-fx-background-color: #f5f6fa;");
        
        Scene scene = new Scene(layoutPrincipal, 1600, 1000);
        stage.setFullScreen(true);
        stage.setScene(scene);
        
        chargerDemandes();
    }
    
    private VBox creerZoneSimulation() {
        VBox zoneSimu = new VBox(15);
        zoneSimu.setPadding(new Insets(20));
        zoneSimu.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 12px;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 3);"
        );
        
        Label titreSimu = new Label("SIMULATION WHAT-IF");
        titreSimu.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        
        HBox ligne1 = new HBox(30);
        ligne1.setAlignment(Pos.CENTER);
        
        VBox boxAge = new VBox(5);
        Label lblAge = new Label("Âge : 30 ans");
        lblAge.setStyle("-fx-font-size: 13px; -fx-font-weight: bold;");
        sliderAge = new Slider(18, 70, 30);
        sliderAge.setShowTickLabels(true);
        sliderAge.setShowTickMarks(true);
        sliderAge.setMajorTickUnit(10);
        sliderAge.setPrefWidth(400);
        sliderAge.valueProperty().addListener((obs, oldVal, newVal) -> {
            lblAge.setText("Âge : " + newVal.intValue() + " ans");
            calculerSimulation();
        });
        boxAge.getChildren().addAll(lblAge, sliderAge);
        
        VBox boxRevenus = new VBox(5);
        Label lblRevenus = new Label("Revenus : 2500 €");
        lblRevenus.setStyle("-fx-font-size: 13px; -fx-font-weight: bold;");
        sliderRevenus = new Slider(1000, 8000, 2500);
        sliderRevenus.setShowTickLabels(true);
        sliderRevenus.setShowTickMarks(true);
        sliderRevenus.setMajorTickUnit(1000);
        sliderRevenus.setPrefWidth(400);
        sliderRevenus.valueProperty().addListener((obs, oldVal, newVal) -> {
            lblRevenus.setText("Revenus : " + newVal.intValue() + " €");
            calculerSimulation();
        });
        boxRevenus.getChildren().addAll(lblRevenus, sliderRevenus);
        
        VBox boxMontant = new VBox(5);
        Label lblMontant = new Label("Montant : 15000 €");
        lblMontant.setStyle("-fx-font-size: 13px; -fx-font-weight: bold;");
        sliderMontant = new Slider(1000, 50000, 15000);
        sliderMontant.setShowTickLabels(true);
        sliderMontant.setShowTickMarks(true);
        sliderMontant.setMajorTickUnit(10000);
        sliderMontant.setPrefWidth(400);
        sliderMontant.valueProperty().addListener((obs, oldVal, newVal) -> {
            lblMontant.setText("Montant : " + newVal.intValue() + " €");
            calculerSimulation();
        });
        boxMontant.getChildren().addAll(lblMontant, sliderMontant);
        
        ligne1.getChildren().addAll(boxAge, boxRevenus, boxMontant);
        
        HBox ligne2 = new HBox(30);
        ligne2.setAlignment(Pos.CENTER);
        
        VBox boxDuree = new VBox(5);
        Label lblDuree = new Label("Durée : 60 mois");
        lblDuree.setStyle("-fx-font-size: 13px; -fx-font-weight: bold;");
        sliderDuree = new Slider(12, 240, 60);
        sliderDuree.setShowTickLabels(true);
        sliderDuree.setShowTickMarks(true);
        sliderDuree.setMajorTickUnit(48);
        sliderDuree.setPrefWidth(400);
        sliderDuree.valueProperty().addListener((obs, oldVal, newVal) -> {
            lblDuree.setText("Durée : " + newVal.intValue() + " mois");
            calculerSimulation();
        });
        boxDuree.getChildren().addAll(lblDuree, sliderDuree);
        
        VBox boxEmploi = new VBox(5);
        Label lblEmploiTitle = new Label("Emploi :");
        lblEmploiTitle.setStyle("-fx-font-size: 13px; -fx-font-weight: bold;");
        comboEmploi = new ComboBox<>();
        comboEmploi.getItems().addAll("CDI", "Fonctionnaire", "CDD", "Indépendant", "Étudiant");
        comboEmploi.setValue("CDI");
        comboEmploi.setPrefWidth(400);
        comboEmploi.setOnAction(e -> calculerSimulation());
        boxEmploi.getChildren().addAll(lblEmploiTitle, comboEmploi);
        
        VBox boxDette = new VBox(5);
        Label lblDette = new Label("Dette : 5000 €");
        lblDette.setStyle("-fx-font-size: 13px; -fx-font-weight: bold;");
        sliderDette = new Slider(0, 30000, 5000);
        sliderDette.setShowTickLabels(true);
        sliderDette.setShowTickMarks(true);
        sliderDette.setMajorTickUnit(5000);
        sliderDette.setPrefWidth(400);
        sliderDette.valueProperty().addListener((obs, oldVal, newVal) -> {
            lblDette.setText("Dette : " + newVal.intValue() + " €");
            calculerSimulation();
        });
        boxDette.getChildren().addAll(lblDette, sliderDette);
        
        ligne2.getChildren().addAll(boxDuree, boxEmploi, boxDette);
        
        VBox zoneResultat = new VBox(10);
        zoneResultat.setPadding(new Insets(20));
        zoneResultat.setStyle("-fx-background-color: #ecf0f1; -fx-background-radius: 10px;");
        zoneResultat.setAlignment(Pos.CENTER);
        
        lblScoreSimu = new Label("Score : 0");
        lblScoreSimu.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        
        lblDecisionSimu = new Label("Décision : -");
        lblDecisionSimu.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        
    
        VBox boxJauge = new VBox(5);
        boxJauge.setAlignment(Pos.CENTER);
        Label lblJauge = new Label("Niveau de risque :");
        lblJauge.setStyle("-fx-font-size: 13px;");
        
        HBox conteneurJauge = new HBox();
        conteneurJauge.setPrefSize(500, 30);
        conteneurJauge.setMaxSize(500, 30);
        conteneurJauge.setStyle("-fx-background-color: #e0e0e0; -fx-background-radius: 10px;");
        
        jaugeRisque = new Rectangle(0, 30);
        jaugeRisque.setArcWidth(10);
        jaugeRisque.setArcHeight(10);
        jaugeRisque.setFill(Color.GRAY);
        
        conteneurJauge.getChildren().add(jaugeRisque);
        boxJauge.getChildren().addAll(lblJauge, conteneurJauge);
        
        Button btnRecalculer = new Button("Appliquer à toutes les demandes");
        btnRecalculer.setPrefSize(280, 40);
        btnRecalculer.setStyle(
            "-fx-background-color: #9b59b6;" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 14px;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 10px;" +
            "-fx-cursor: hand;"
        );
        btnRecalculer.setOnAction(e -> recalculerToutesLesDemandes());
        
        zoneResultat.getChildren().addAll(lblScoreSimu, lblDecisionSimu, boxJauge, btnRecalculer);
        
        zoneSimu.getChildren().addAll(
            titreSimu,
            new Separator(),
            ligne1,
            ligne2,
            new Separator(),
            zoneResultat
        );
        
        calculerSimulation();
        
        return zoneSimu;
    }
    
    private void calculerSimulation() {
        int age = (int) sliderAge.getValue();
        double revenus = sliderRevenus.getValue();
        double montant = sliderMontant.getValue();
        int duree = (int) sliderDuree.getValue();
        String emploi = comboEmploi.getValue();
        double dette = sliderDette.getValue();
        
        int score = calculerScore(age, revenus, montant, duree, emploi, dette);
        String decision = determinerDecision(score);
        
        lblScoreSimu.setText("Score : " + score + " / 100");
        lblDecisionSimu.setText("Décision : " + decision);
        
        double largeur = (score / 100.0) * 500;
        jaugeRisque.setWidth(largeur);
        
        if (decision.equals("ACCEPTÉ")) {
            jaugeRisque.setFill(Color.rgb(76, 175, 80));
            lblDecisionSimu.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #4CAF50;");
        } else if (decision.equals("REFUSÉ")) {
            jaugeRisque.setFill(Color.rgb(244, 67, 54));
            lblDecisionSimu.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #F44336;");
        } else {
            jaugeRisque.setFill(Color.rgb(255, 193, 7));
            lblDecisionSimu.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #FFC107;");
        }
    }
    
    // ===== INITIALISER LA BASE DE DONNÉES =====
    private void initialiserBaseDeDonnees() {
        try {
            conn = DriverManager.getConnection("jdbc:sqlite:predicteur.db");
        } catch (SQLException e) {
            afficherAlerte("Erreur", "Impossible de se connecter à la base de données");
        }
    }
    
    private void importerCSV() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Sélectionner un fichier CSV");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Fichiers CSV", "*.csv")
        );
        
        File fichier = fileChooser.showOpenDialog(stage);
        
        if (fichier != null) {
            traiterCSV(fichier);
        }
    }
    
    private void traiterCSV(File fichier) {
        int compteur = 0;
        int erreurs = 0;
        
        try (BufferedReader br = new BufferedReader(new FileReader(fichier))) {
            String ligne;
            boolean premiereLigne = true;
            
            while ((ligne = br.readLine()) != null) {
                if (premiereLigne) {
                    premiereLigne = false;
                    continue;
                }
                
                String[] valeurs = ligne.split(",");
                
                if (valeurs.length >= 7) {
                    try {
                        String nom = valeurs[0].trim();
                        int age = Integer.parseInt(valeurs[1].trim());
                        double revenus = Double.parseDouble(valeurs[2].trim());
                        double montant = Double.parseDouble(valeurs[3].trim());
                        int duree = Integer.parseInt(valeurs[4].trim());
                        String emploi = valeurs[5].trim();
                        double dette = Double.parseDouble(valeurs[6].trim());
                        
                        // Calculer le score
                        int score = calculerScore(age, revenus, montant, duree, emploi, dette);
                        String decision = determinerDecision(score);
                        
                        // Insérer dans la base
                        insererDemande(nom, age, revenus, montant, duree, emploi, dette, score, decision);
                        compteur++;
                        
                    } catch (NumberFormatException e) {
                        erreurs++;
                    }
                }
            }
            
            afficherAlerte("Import terminé", 
                compteur + " demandes importées avec succès.\n" +
                (erreurs > 0 ? erreurs + " lignes ignorées (erreur de format)." : ""));
            
            chargerDemandes();
            
        } catch (IOException e) {
            afficherAlerte("Erreur", "Impossible de lire le fichier CSV");
        }
    }
    
    private int calculerScore(int age, double revenus, double montant, int duree, String emploi, double dette) {
        int score = 0;
        
        if (age < 25) score += 5;
        else if (age >= 25 && age <= 40) score += 15;
        else if (age > 40 && age <= 60) score += 20;
        else score += 10;
        
        if (revenus < 1500) score += 5;
        else if (revenus >= 1500 && revenus < 2500) score += 15;
        else if (revenus >= 2500 && revenus < 4000) score += 25;
        else score += 30;
        
        if (emploi.equalsIgnoreCase("CDI")) score += 25;
        else if (emploi.equalsIgnoreCase("Fonctionnaire")) score += 30;
        else if (emploi.equalsIgnoreCase("CDD")) score += 10;
        else if (emploi.equalsIgnoreCase("Indépendant")) score += 5;
        else score += 0;

        double ratioDette = (dette / revenus) * 100;
        if (ratioDette < 20) score += 15;
        else if (ratioDette < 40) score += 10;
        else if (ratioDette < 60) score += 5;
        
        // Mensualité estimée
        double mensualite = montant / duree;
        double ratioMensualite = (mensualite / revenus) * 100;
        if (ratioMensualite < 30) score += 15;
        else if (ratioMensualite < 40) score += 10;
        
        return score;
    }
    
    private String determinerDecision(int score) {
        if (score >= 70) return "ACCEPTÉ";
        else if (score >= 40) return "EN ATTENTE";
        else return "REFUSÉ";
    }
    
    private void insererDemande(String nom, int age, double revenus, double montant, 
                                 int duree, String emploi, double dette, int score, String decision) {
        try {
            String dateImport = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            
            PreparedStatement pstmt = conn.prepareStatement(
                "INSERT INTO demandes_pret (nom, age, revenus_mensuels, montant_demande, " +
                "duree_mois, emploi, dette_actuelle, score_risque, decision, date_import) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"
            );
            
            pstmt.setString(1, nom);
            pstmt.setInt(2, age);
            pstmt.setDouble(3, revenus);
            pstmt.setDouble(4, montant);
            pstmt.setInt(5, duree);
            pstmt.setString(6, emploi);
            pstmt.setDouble(7, dette);
            pstmt.setInt(8, score);
            pstmt.setString(9, decision);
            pstmt.setString(10, dateImport);
            
            pstmt.executeUpdate();
            
        } catch (SQLException e) {
            System.err.println("Erreur insertion : " + e.getMessage());
        }
    }
    
    private void chargerDemandes() {
        demandes.clear();
        
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM demandes_pret ORDER BY id DESC");
            
            int acceptes = 0, refuses = 0, enAttente = 0;
            
            while (rs.next()) {
                Demande d = new Demande(
                    rs.getString("nom"),
                    rs.getInt("age"),
                    rs.getDouble("revenus_mensuels"),
                    rs.getDouble("montant_demande"),
                    rs.getInt("duree_mois"),
                    rs.getString("emploi"),
                    rs.getDouble("dette_actuelle"),
                    rs.getInt("score_risque"),
                    rs.getString("decision")
                );
                
                demandes.add(d);
                
                if (d.getDecision().equals("ACCEPTÉ")) acceptes++;
                else if (d.getDecision().equals("REFUSÉ")) refuses++;
                else enAttente++;
            }
            
            lblStats.setText(String.format(
                "Total : %d demandes | Acceptées : %d | En attente : %d | Refusées : %d",
                demandes.size(), acceptes, enAttente, refuses
            ));
            
        } catch (SQLException e) {
            afficherAlerte("Erreur", "Impossible de charger les demandes");
        }
    }
    
    private void viderBase() {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation");
        confirmation.setHeaderText("Vider toute la base de données ?");
        confirmation.setContentText("Cette action est irréversible !");
        
        if (confirmation.showAndWait().get() == ButtonType.OK) {
            try {
                Statement stmt = conn.createStatement();
                stmt.execute("DELETE FROM demandes_pret");
                chargerDemandes();
                afficherAlerte("Succès", "Base de données vidée");
            } catch (SQLException e) {
                afficherAlerte("Erreur", "Impossible de vider la base");
            }
        }
    }

    private void recalculerToutesLesDemandes() {
        if (demandes.isEmpty()) {
            afficherAlerte("Aucune demande", "Importez d'abord un fichier CSV");
            return;
        }
        
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM demandes_pret");
            
            int compteur = 0;
            
            while (rs.next()) {
                int id = rs.getInt("id");
                int age = rs.getInt("age");
                double revenus = rs.getDouble("revenus_mensuels");
                double montant = rs.getDouble("montant_demande");
                int duree = rs.getInt("duree_mois");
                String emploi = rs.getString("emploi");
                double dette = rs.getDouble("dette_actuelle");
                
                int nouveauScore = calculerScore(age, revenus, montant, duree, emploi, dette);
                String nouvelleDecision = determinerDecision(nouveauScore);
                
                PreparedStatement pstmt = conn.prepareStatement(
                    "UPDATE demandes_pret SET score_risque = ?, decision = ? WHERE id = ?"
                );
                pstmt.setInt(1, nouveauScore);
                pstmt.setString(2, nouvelleDecision);
                pstmt.setInt(3, id);
                pstmt.executeUpdate();
                
                compteur++;
            }
            
            afficherAlerte("Recalcul terminé", compteur + " demandes recalculées avec les nouveaux critères");
            chargerDemandes();
            
        } catch (SQLException e) {
            afficherAlerte("Erreur", "Impossible de recalculer les demandes");
        }
    }
    
    private void afficherAlerte(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void retourMenu() {
        try {
            if (conn != null) conn.close();
        } catch (SQLException e) {
            System.err.println("Erreur fermeture DB");
        }
        
        MenuPrincipal menu = new MenuPrincipal(stage);
        menu.afficher();
    }
    
    public static class Demande {
        private String nom;
        private int age;
        private double revenusMensuels;
        private double montantDemande;
        private int dureeMois;
        private String emploi;
        private double detteActuelle;
        private int scoreRisque;
        private String decision;
        
        public Demande(String nom, int age, double revenus, double montant, int duree,
                      String emploi, double dette, int score, String decision) {
            this.nom = nom;
            this.age = age;
            this.revenusMensuels = revenus;
            this.montantDemande = montant;
            this.dureeMois = duree;
            this.emploi = emploi;
            this.detteActuelle = dette;
            this.scoreRisque = score;
            this.decision = decision;
        }
        
        // Getters
        public String getNom() { return nom; }
        public int getAge() { return age; }
        public double getRevenusMensuels() { return revenusMensuels; }
        public double getMontantDemande() { return montantDemande; }
        public int getDureeMois() { return dureeMois; }
        public String getEmploi() { return emploi; }
        public double getDetteActuelle() { return detteActuelle; }
        public int getScoreRisque() { return scoreRisque; }
        public String getDecision() { return decision; }
    }
}