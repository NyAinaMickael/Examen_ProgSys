<?php if (php_sapi_name() === 'cli' && getenv("QUERY_STRING")) {parse_str(getenv("QUERY_STRING"), $_GET);}if (php_sapi_name() === 'cli' && getenv("REQUEST_METHOD") === 'POST') {$postData = stream_get_contents(STDIN);if ($postData) {parse_str($postData, $_POST);}}?>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Document</title>
</head>
<body>
    <?php 
    if(1==1)
    {
        echo "Bienvenue ".$_POST['nom']."^^<br>"; 
        echo $_GET['popo'];
    }
    ?>
</body>
</html>
<?php
header("location:index.html");
echo "aaa";
exit;
?>