#!/bin/bash

# Créer le répertoire d'installation
INSTALL_DIR="/opt/serveur-Apache-Lite"
mkdir -p "$INSTALL_DIR"

# Créer la structure des répertoires (gui, sock, Pages, logs)
mkdir -p "$INSTALL_DIR/Pages"
mkdir -p "$INSTALL_DIR/logs"
mkdir -p "$INSTALL_DIR/sock"
mkdir -p "$INSTALL_DIR/gui"

# Copier les fichiers .class de sock dans le répertoire sock
cp ./sock/*.class "$INSTALL_DIR/sock/"

# Copier les fichiers .class de gui dans le répertoire gui
cp ./gui/*.class "$INSTALL_DIR/gui/"

touch "$INSTALL_DIR/config.conf"

# Créer un script de démarrage
cat > "$INSTALL_DIR/start-server.sh" <<EOL
#!/bin/bash

# Lancer le serveur Java
java gui.ServerGUI
EOL

# Créer un fichier de log vide dans le répertoire logs
touch "$INSTALL_DIR/logs/server.log"


# Donner les droits d'exécution au script de démarrage
chmod +x "$INSTALL_DIR/start-server.sh"

#Initialsation des autorisations des fichiers
chmod 766 "$INSTALL_DIR/logs/server.log"
chmod 766 "$INSTALL_DIR/config.conf"

# Donner les droits d'exécution au fichier de démarrage Java si nécessaire
chmod +x "$INSTALL_DIR/gui/ServerGUI.class"

echo "L'installation est terminée. Vous pouvez démarrer le serveur en exécutant le script start-server.sh."
