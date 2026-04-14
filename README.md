# Lancer l'application

```bash
    mvn clean compile
    mvn javafx:run
```

# TextAnalyser

## Test Positif

L'intelligence artificielle transforme notre monde ! Les voitures autonomes roulent sur nos routes. Les assistants virtuels répondent à nos questions. Les robots industriels travaillent dans nos usines. Cette révolution technologique est fascinante et inquiétante à la fois. Comment l'IA va-t-elle changer nos emplois ? Quels métiers vont disparaître ? Quels nouveaux métiers vont apparaître ? L'avenir reste incertain, mais une chose est sûre : l'intelligence artificielle ne s'arrêtera pas. Elle continuera à évoluer, à apprendre, à progresser. Nous devons nous adapter et apprendre nous aussi.

## Test Negatif

Le changement climatique menace notre planète. Les températures augmentent dangereusement. Les glaciers fondent rapidement. Les océans montent inexorablement. Cette situation est alarmante et préoccupante. Pourquoi n'agissons-nous pas plus vite ? Les catastrophes naturelles se multiplient. Les incendies dévastent nos forêts. Les inondations détruisent nos villes. L'avenir semble incertain et sombre. Nous devons réagir maintenant ! Il est urgent de changer nos habitudes. Pouvons-nous encore sauver notre environnement ? La réponse dépend de nos actions immédiates. Chaque geste compte dans cette lutte difficile.

# ChatBot

## Ajouter réponse :

```bash
sqlite3 chatbot.db
```

```bash
    INSERT INTO reponses (mots_cles, reponse) VALUES 
    ('mc1,mc2', 
    'réponse.');
```


---

## Modifier :

```bash
qlite3 chatbot.db
```

```bash
    UPDATE reponses 
    SET mots_cles = 'modification' 
    WHERE id = 2;
```

## Delete : 

```bash
sqlite3 chatbot.db
```

```bash
    DELETE FROM reponses WHERE id = 5;
```



# Predicteur Risque

## BARÈME DE SCORING (sur 100 points)

### 1. ÂGE (max 20 points)

    < 25 ans : +5 points 
    25-40 ans : +15 points 
    40-60 ans : +20 points 
    > 60 ans : +10 points

### 2. REVENUS MENSUELS (max 30 points)

    < 1500€ : +5 points
    1500-2500€ : +15 points
    2500-4000€ : +25 points 
    > 4000€ : +30 points

### 3. TYPE D'EMPLOI (max 30 points)

    CDI : +25 points 
    Fonctionnaire : +30 points 
    CDD : +10 points 
    Indépendant : +5 points 
    Étudiant : +0 points 

### 4. RATIO DETTE/REVENUS (max 15 points)

    < 20% : +15 points
    20-40% : +10 points
    40-60% : +5 points 
    > 60% : +0 points 

### 5. RATIO MENSUALITÉ/REVENUS (max 15 points)

    < 30% : +15 points 
    30-40% : +10 points 
    > 40% : +0 points 

### DÉCISION FINALE

    Score ≥ 70 → ACCEPTÉ
    Score 40-69 → EN ATTENTE
    Score < 40 → REFUSÉ