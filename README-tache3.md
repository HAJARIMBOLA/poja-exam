# Tâche 3 — GET /files (find all) + tests

Fichiers à copier dans le projet (mêmes chemins) :

- src/main/java/com/example/demo/endpoint/rest/controller/file/FileController.java
  -> ajout de GET /files qui renvoie tous les UploadedFile persistés (200 OK)
     (fichier complet, remplace celui de la Tâche 2)

- src/test/java/com/example/demo/endpoint/rest/controller/file/FileControllerTest.java
  -> ajout de 2 tests Mockito pour findAll (avec données / vide)
     (fichier complet, remplace celui de la Tâche 2)

- src/test/java/com/example/demo/endpoint/rest/controller/file/FileControllerIT.java (nouveau)
  -> test d'intégration FacadeIT : POST /files (fichier texte, pas de JPEG pour ne pas
     dépendre d'un vrai S3/SES en CI) puis GET /files, vérifie que l'upload apparaît

Dépend des fichiers des Tâches 1 et 2.
Code formaté avec google-java-format-1.23.0-all-deps.jar (./format.sh).
