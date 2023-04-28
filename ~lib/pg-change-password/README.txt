Ce dossier contient un script Ant qui permet de changer
le mot de passe administrateur d'une base PostgreSQL.

Mode d'emploi
-------------

 1. Ouvrez le fichier pg-change-password.xml avec un
    éditeur de texte. Si nécessaire, modifiez les
    différents paramètres du traitement :
      pg.dir.home = dossier où est installé PostgreSQL
      pg.dir.data = dossier qui contient la base de données
      pg.database = nom de la base de données
      pg.user     = nom du compte administrateur
      pg.password = mot de passe que vous souhaitez donner 
                    au compte administrateur.
 2. Arrêtez le serveur PostgreSQL s'il est démarré.
 3. Utilisez ANT pour exécuter le script contenu dans le
    fichier pg-change-password.xml (cible par défaut). 
 4. Vous pouvez redémarrer le serveur PostgreSQL.
    Il utilisera désormais le mot de passe que vous avez 
    indiqué.     
    