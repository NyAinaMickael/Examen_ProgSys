package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.*;
import sock.*;

public class ServerGUI extends JFrame {

    private JButton startButton;
    private JButton stopButton;
    private JLabel statusLabel;
    private boolean active;

    public ServerGUI() {
        initMainWindow();
        stopButton.setEnabled(false);//satria suppose mbola maty ilay serveur
    }
    public void reinitMainWindow()
    {
        getContentPane().removeAll();
        initMainWindow();
    }
    public void initMainWindow()
    {  
        setTitle("Serveur Apache - Contrôle");
        setSize(600, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Statut du serveur
        statusLabel = new JLabel("Statut : Serveur arrêté", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Arial", Font.BOLD, 16));
        add(statusLabel, BorderLayout.NORTH);

        // Boutons de contrôle
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());

        startButton = new JButton("Démarrer le serveur");
        stopButton = new JButton("Arrêter le serveur");// Désactiver le bouton "Arrêter" au départ

        buttonPanel.add(startButton);
        buttonPanel.add(stopButton);
        JButton toConfig=new JButton("Configure");
        toConfig.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                switchToConfig();
            }
        });
        buttonPanel.add(toConfig);


        add(buttonPanel, BorderLayout.CENTER);

        // Actions des boutons
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startServer();
            }
        });

        stopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                stopServer();
            }
        });
        if(active)
        {
            stopButton.setEnabled(true);
            startButton.setEnabled(false);
        }
        else{
            stopButton.setEnabled(false);
            startButton.setEnabled(true);
        }
    }
    public void switchToConfig()
    {
        getContentPane().removeAll();
        setTitle("Serveur Apache - Contrôle");
        setSize(600, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(4, 1)); // 4 lignes pour 4 panneaux distincts

        // Panneau pour la section "Port"
        JPanel portPanel = new JPanel(new FlowLayout());
        portPanel.add(new JLabel("Port"));
        JTextField in1 = new JTextField(10);
        portPanel.add(in1);
        JButton b1 = new JButton("Set Port");
        b1.addActionListener(e -> {
            try {
                ServeurApache.setConfigPort(Integer.parseInt(in1.getText()));
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Veuillez entrer un numéro valide pour le port.", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });
        portPanel.add(b1);
        add(portPanel);

        // Panneau pour la section "ROOT_DIR"
        JPanel rootDirPanel = new JPanel(new FlowLayout());
        rootDirPanel.add(new JLabel("ROOT_DIR"));
        JTextField in2 = new JTextField(20);
        rootDirPanel.add(in2);
        JButton b2 = new JButton("Set Directory");
        b2.addActionListener(e -> ServeurApache.setRepertoire(in2.getText()));
        rootDirPanel.add(b2);
        add(rootDirPanel);

        // Panneau pour la section "PHP_ENABLE"
        JPanel phpPanel = new JPanel(new FlowLayout());
        phpPanel.add(new JLabel("PHP_ENABLE"));
        JButton btnTrue = new JButton("True");
        JButton btnFalse = new JButton("False");
        btnTrue.addActionListener(e -> ServeurApache.setHandlePHP(true));
        btnFalse.addActionListener(e -> ServeurApache.setHandlePHP(false));
        phpPanel.add(btnTrue);
        phpPanel.add(btnFalse);
        add(phpPanel);

        // Panneau pour le bouton "Retour"
        JPanel retourPanel = new JPanel(new FlowLayout());
        JButton retour = new JButton("Retour");
        retour.addActionListener(e -> backToMainWindow());
        retourPanel.add(retour);
        add(retourPanel);

        // Rendre la fenêtre visible
        setVisible(true);

    }
    private void backToMainWindow()
    {
        reinitMainWindow();
    }
    private void startServer() {
        active=true;
        // Démarrer le serveur dans un nouveau thread
        ExecutorService threadPool = Executors.newCachedThreadPool(); // Gestion des threads
        threadPool.execute(()->ServeurApache.start());
        statusLabel.setText("Statut : Serveur démarré(Port "+ServeurApache.getConfigPort()+")");
        startButton.setEnabled(false);
        stopButton.setEnabled(true);

    }

    private void stopServer() {
        active=false;
        try {
            // Envoyer une requête pour arrêter le serveur
            //System.out.println("Port utilise:"+ServeurApache.getPort());
            ServerStopper.stopServer("localhost", ServeurApache.getConfigPort());
            statusLabel.setText("Statut : Serveur arrêté");
        } catch (Exception ex) {
            ex.printStackTrace();
            statusLabel.setText("Erreur lors de l'arrêt du serveur");
            Logger.error("Erreur lors de l'arrêt du serveur:\n"+ex.getMessage());
        }
        startButton.setEnabled(true);
        stopButton.setEnabled(false);
    }

    public static void main(String[] args) {
        ServerGUI gui = new ServerGUI();
        gui.setVisible(true);
    }
}
