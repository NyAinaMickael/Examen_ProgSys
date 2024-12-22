<?php if (php_sapi_name() === 'cli' && getenv("QUERY_STRING")) {parse_str(getenv("QUERY_STRING"), $_GET);}if (php_sapi_name() === 'cli' && getenv("REQUEST_METHOD") === 'POST') {$postData = stream_get_contents(STDIN);if ($postData) {parse_str($postData, $_POST);}}?>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Informations sur la Classe</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f4f4f9;
            margin: 0;
            padding: 20px;
        }
        h1 {
            text-align: center;
            color: #333;
        }
        table {
            width: 80%;
            margin: 20px auto;
            border-collapse: collapse;
            box-shadow: 0 2px 5px rgba(0, 0, 0, 0.2);
            background-color: #fff;
        }
        th, td {
            padding: 10px 15px;
            text-align: left;
            border: 1px solid #ddd;
        }
        th {
            background-color: #007bff;
            color: #fff;
            text-transform: uppercase;
            font-size: 14px;
        }
        tr:nth-child(even) {
            background-color: #f9f9f9;
        }
        tr:hover {
            background-color: #f1f1f1;
        }
        footer {
            text-align: center;
            margin-top: 20px;
            color: #666;
        }
    </style>
</head>
<body>
    <h1>Informations sur la Classe</h1>
    <?php
    // Données de la classe
    $classe = [
        "Nom" => "Informatique 101",
        "Professeur" => "M. Dupont",
        "Nombre d'étudiants" => 30,
        "Salle" => "B202",
        "Horaires" => "Lundi et Mercredi, 10h-12h"
    ];
    ?>
    <table>
        <thead>
            <tr>
                <th>Attribut</th>
                <th>Valeur</th>
            </tr>
        </thead>
        <tbody>
            <?php foreach ($classe as $attribut => $valeur): ?>
                <tr>
                    <td><?= htmlspecialchars($attribut); ?></td>
                    <td><?= htmlspecialchars($valeur); ?></td>
                </tr>
            <?php endforeach; ?>
        </tbody>
    </table>
    <footer>
        © 2024 - Informations générées dynamiquement avec PHP
    </footer>
</body>
</html>
